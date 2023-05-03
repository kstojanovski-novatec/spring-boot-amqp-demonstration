package com.acme.springamqp_demonstration.message.importanttopics;

import com.acme.springamqp_demonstration.message.MessageConverterBeans;
import com.acme.springamqp_demonstration.message.ParallelRetryQueuesInterceptor;
import com.acme.springamqp_demonstration.message.importanttopics.model.ImportantTopic;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.util.TestPropertyValues;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.test.context.ContextConfiguration;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static org.awaitility.Awaitility.await;
import static org.hamcrest.CoreMatchers.is;


@SpringBootTest
@ContextConfiguration(
    initializers = RetryQueuesContainerFactoryIntegrationTcTest.Initializer.class,
    classes = {
        MessageConverterBeans.class,
        ImportantTopicsParallelRetryConfig.class, // creates the exchange and the queue if not already created.
        ImportantTopicsParallelRetryListenerConfiguration.class, // loads the logic for the retry queues container factory.
        ImportantTopicsParallelRetryListenerTestConfig.class // creates rabbitTemplate instance for auto writing.
    }
)
@Testcontainers
public class RetryQueuesContainerFactoryIntegrationTcTest {

  private static final Logger LOGGER = LoggerFactory.getLogger(RetryQueuesContainerFactoryIntegrationTcTest.class);

  @SuppressWarnings("rawtypes")
  @Container
  public static GenericContainer rabbit = new GenericContainer("rabbitmq:3-management")
      .withExposedPorts(5672, 15672);

  @Autowired
  private RabbitTemplate rabbitTemplate;

  @Value("${important.topics.exchange.name.pr}")
  private String IMPORTANT_TOPICS_EXCHANGE_NAME_PR;


  @Test
  public void testRetryQueuesContainerFactory() {
    int nb = 2;
    for (int i = 1; i <= nb; i++) {
      rabbitTemplate.convertAndSend(
          IMPORTANT_TOPICS_EXCHANGE_NAME_PR,
          "com.acme.general",
          new ImportantTopic(IMPORTANT_TOPICS_EXCHANGE_NAME_PR, String.valueOf(i))
      );
    }
    await()
        .atMost(30, TimeUnit.SECONDS)
        .until(
            () -> messageFromQueue.size() == 8,
            is(true)
        );
  }

  List<ImportantTopic> messageFromQueue = new ArrayList<>();

  @RabbitListener(
      queues = {"${important.topics.queue.name.general.pr}"},
      containerFactory = "retryQueuesContainerFactory",
      ackMode = "MANUAL")
  public void receiveGeneralTopics(final ImportantTopic importantTopic) throws Exception {
    LOGGER.info("Received general topics: {} with date time {}", importantTopic.messageContent(), importantTopic.currentDateTime());
    messageFromQueue.add(importantTopic);
    throw new Exception("This is a very evil exception!");
  }

  @RabbitListener(
      queues = {"${important.topics.queue.name.general.pr.dlq}"},
      containerFactory = "defaultContainerFactory",
      messageConverter = "simpleMessageConverter")
  public void receiveGeneralTopicsDlq(
      Message message,
      @Payload ImportantTopic importantTopic
  ) {
    LOGGER.info("Queue com.acme.general.queue.pr {} failed!", importantTopic.currentDateTime());
    MessageProperties props = message.getMessageProperties();
    rabbitTemplate.convertAndSend(
        props.getHeader(ParallelRetryQueuesInterceptor.HEADER_X_ORIGINAL_EXCHANGE),
        props.getHeader(ParallelRetryQueuesInterceptor.HEADER_X_ORIGINAL_ROUTING_KEY),
        message);
  }

  public static class Initializer implements
      ApplicationContextInitializer<ConfigurableApplicationContext> {
    @Override
    public void initialize(@NotNull ConfigurableApplicationContext configurableApplicationContext) {
      TestPropertyValues testPropertyValues = TestPropertyValues.of(
          "spring.rabbitmq.host=" + rabbit.getHost(),
          "spring.rabbitmq.port=" + rabbit.getMappedPort(5672)
      );
      System.setProperty("spring.rabbitmq.host", rabbit.getHost());
      System.setProperty("spring.rabbitmq.port", String.valueOf(rabbit.getMappedPort(5672)));
      testPropertyValues.applyTo(configurableApplicationContext);
    }
  }

}
