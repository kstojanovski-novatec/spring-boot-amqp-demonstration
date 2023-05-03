package com.acme.springamqp_demonstration.message.simplenews;

import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.util.TestPropertyValues;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
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
    initializers = SimpleNewsSenderIntegrationTest.Initializer.class,
    classes = MessageConverterBeans.class)
@Testcontainers
public class SimpleNewsSenderIntegrationTest {

  private static final Logger LOGGER = LoggerFactory.getLogger(SimpleNewsSenderIntegrationTest.class);

  @SuppressWarnings("rawtypes")
  @Container
  public static GenericContainer rabbit = new GenericContainer("rabbitmq:3-management")
      .withExposedPorts(5672, 15672);

  @Autowired
  private SimpleNewsSender simpleNewsSender;

  @Test
  public void testBroadcast() {
    simpleNewsSender.sendSimpleNews();
    await()
        .atMost(10, TimeUnit.SECONDS)
        .until(
            () -> !messageFromQueue1.isEmpty() && !messageFromQueue2.isEmpty(),
            is(true)
        );
  }

  List<String> messageFromQueue1 = new ArrayList<>();
  List<String> messageFromQueue2 = new ArrayList<>();

  @RabbitListener(queues = { "${simple.news.queue.name.1}" }, messageConverter = "simpleMessageConverter")
  public void receiveSimpleNews1(String message) {
    messageFromQueue1.add(message);
    LOGGER.info("Received simple news 1: " + message);
  }

  @RabbitListener(queues = { "${simple.news.queue.name.2}" }, messageConverter = "simpleMessageConverter")
  public void receiveSimpleNews2(String message) {
    messageFromQueue2.add(message);
    LOGGER.info("Received simple news 2: " + message);
  }

  public static class Initializer implements
      ApplicationContextInitializer<ConfigurableApplicationContext> {

    @Override
    public void initialize(@NotNull ConfigurableApplicationContext configurableApplicationContext) {
      TestPropertyValues testPropertyValues = TestPropertyValues.of(
          "spring.rabbitmq.host=" + rabbit.getHost(),
          "spring.rabbitmq.port=" + rabbit.getMappedPort(5672)
      );
      testPropertyValues.applyTo(configurableApplicationContext);
    }

  }

}
