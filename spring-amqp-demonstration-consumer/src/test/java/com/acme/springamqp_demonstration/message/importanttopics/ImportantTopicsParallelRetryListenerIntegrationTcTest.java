package com.acme.springamqp_demonstration.message.importanttopics;

import com.acme.springamqp_demonstration.message.MessageConverterBeans;
import com.acme.springamqp_demonstration.message.importanttopics.model.ImportantTopic;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.util.TestPropertyValues;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.test.context.ContextConfiguration;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

@SpringBootTest
@ContextConfiguration(
    initializers = ImportantTopicsParallelRetryListenerIntegrationTcTest.Initializer.class,
    classes = {
        MessageConverterBeans.class,
        ImportantTopicsParallelRetryConfig.class, // creates the exchange and the queue if not already created.
        ImportantTopicsParallelRetryListenerConfiguration.class, // loads the logic for the retry queues container factory.
        ImportantTopicsParallelRetryListenerTestConfig.class, // creates rabbitTemplate instance for auto writing.
        ImportantTopicsParallelRetryListener.class // loads the listeners.
    }
    )
@Testcontainers
public class ImportantTopicsParallelRetryListenerIntegrationTcTest {

  private static final Logger LOGGER = LoggerFactory.getLogger(ImportantTopicsParallelRetryListenerIntegrationTcTest.class);

  @SuppressWarnings("rawtypes")
  @Container
  public static GenericContainer rabbit = new GenericContainer("rabbitmq:3-management")
      .withExposedPorts(5672, 15672);

  @Autowired
  private RabbitTemplate rabbitTemplate;

  @Value("${important.topics.exchange.name.pr}")
  private String IMPORTANT_TOPICS_EXCHANGE_NAME_PR;

  @Test
  public void testParallelRetryListeners() {
    int nb = 2;
    for (int i = 1; i <= nb; i++) {
      rabbitTemplate.convertAndSend(
          IMPORTANT_TOPICS_EXCHANGE_NAME_PR,
          "com.acme.general",
          new ImportantTopic(IMPORTANT_TOPICS_EXCHANGE_NAME_PR, String.valueOf(i))
      );
    }
    try {
      Thread.sleep(15000);
    } catch (InterruptedException e) {
      throw new RuntimeException(e);
    }
    LOGGER.info("The manual integration test ends!!!");
  }

  public static class Initializer implements
      ApplicationContextInitializer<ConfigurableApplicationContext> {

    @Override
    public void initialize(@NotNull ConfigurableApplicationContext configurableApplicationContext) {
      String rabbitHost = rabbit.getHost();
      Integer rabbitMappedPort = rabbit.getMappedPort(5672);
      TestPropertyValues testPropertyValues = TestPropertyValues.of(
          "spring.rabbitmq.host=" + rabbitHost,
          "spring.rabbitmq.port=" + rabbitMappedPort
      );
      System.setProperty("spring.rabbitmq.host", rabbitHost);
      System.setProperty("spring.rabbitmq.port", String.valueOf(rabbitMappedPort));
      testPropertyValues.applyTo(configurableApplicationContext);
    }

  }

}
