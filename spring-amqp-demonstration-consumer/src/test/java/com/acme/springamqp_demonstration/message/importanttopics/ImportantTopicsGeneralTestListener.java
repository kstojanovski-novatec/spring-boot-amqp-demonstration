package com.acme.springamqp_demonstration.message.importanttopics;

import com.acme.springamqp_demonstration.message.importanttopics.model.ImportantTopic;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;

import java.util.ArrayList;
import java.util.List;

@RabbitListener(
    queues = {"${important.topics.queue.name.general1}"},
    containerFactory = "defaultContainerFactory"
)
public class ImportantTopicsGeneralTestListener {

  private static final Logger LOGGER = LoggerFactory.getLogger(ImportantTopicsGeneralTestListener.class);

  static final List<String> RECEIVED_MESSAGES = new ArrayList<>();
  static final List<ImportantTopic> RECEIVED_IMPORTANT_TOPIC = new ArrayList<>();

  @RabbitHandler
  public void receiveGeneralTopicsString(String message) {
    RECEIVED_MESSAGES.add(message);
    LOGGER.info("Received Important Topics with topic general: " + message);
  }

  @RabbitHandler
  public void receiveGeneralTopics(final ImportantTopic importantTopic) {
    RECEIVED_IMPORTANT_TOPIC.add(importantTopic);
    LOGGER.info("Received Important Topics with topic general: {} with date time {}",
        importantTopic.messageContent(), importantTopic.currentDateTime());
  }

}