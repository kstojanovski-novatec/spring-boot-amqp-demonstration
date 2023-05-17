package com.acme.springamqp_demonstration.message.simplenews;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.context.annotation.PropertySource;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@PropertySource("classpath:simple-news.properties")
@RabbitListener(
    queues = { "${simple.news.queue.name.1}" },
    containerFactory = "defaultContainerFactory"
)
public class SimpleNewsWorkerTest {

  private static final Logger LOGGER = LoggerFactory.getLogger(SimpleNewsWorker.class);

  static final Map<Integer, List<String>> RECEIVED_MESSAGES = new ConcurrentHashMap<>();

  private final int snCo;

  public SimpleNewsWorkerTest(int snCo) {
    this.snCo = snCo;
  }

  @RabbitHandler
  public void receiveMsg(final String message) {
    RECEIVED_MESSAGES.computeIfAbsent(snCo, k -> new ArrayList<>());
    RECEIVED_MESSAGES.computeIfPresent(snCo, (k, v) -> { v.add(message); return v; });
    LOGGER.info("consumer {},  Received Simple News from Queue 1: {}", snCo, message);
  }

}
