package com.acme.springamqp_demonstration.message.importanttopics;

import com.acme.springamqp_demonstration.message.CurrentDateTimeProvider;
import com.acme.springamqp_demonstration.message.importanttopics.model.ImportantTopic;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@PropertySource("classpath:important-topics.properties")
@Service
public class ImportantTopicsSender {

  private static final Logger LOGGER = LoggerFactory.getLogger(ImportantTopicsSender.class);

  @Value("${important.topics.exchange.name1}")
  public String IMPORTANT_TOPICS_EXCHANGE_NAME_1;

  private final RabbitTemplate rabbitTemplate;

  private final CurrentDateTimeProvider currentDateTimeProvider = new CurrentDateTimeProvider();

  public ImportantTopicsSender(RabbitTemplate rabbitTemplate) {
    this.rabbitTemplate = rabbitTemplate;
  }

  @Scheduled(cron = "${important.topics.sender1.cron}")
  private void reportCurrentTime() {
    sendImportantTopics(rabbitTemplate);
    sendImportantTopicsObjects(rabbitTemplate);
  }

  private void sendImportantTopics(RabbitTemplate rabbitTemplate) {
    String message = "Important Topics " + currentDateTimeProvider.getCurrentDateTime();
    LOGGER.info("Sending following Important Topics: {}", message);
    rabbitTemplate.convertAndSend(IMPORTANT_TOPICS_EXCHANGE_NAME_1, "com.acme.general", message.concat(" general"));
    rabbitTemplate.convertAndSend(IMPORTANT_TOPICS_EXCHANGE_NAME_1, "com.acme.general.sport", message.concat(" general.sport"));
    rabbitTemplate.convertAndSend(IMPORTANT_TOPICS_EXCHANGE_NAME_1, "com.acme.important-topics.lifestyle", message.concat(" important-topics.lifestyle"));
    rabbitTemplate.convertAndSend(IMPORTANT_TOPICS_EXCHANGE_NAME_1, "com.acme.important-topics.sport.football", message.concat(" important-topics.sport.football"));
  }

  private void sendImportantTopicsObjects(RabbitTemplate rabbitTemplate) {
    String message = "Important Topics - general 1";
    String currentDateTime = currentDateTimeProvider.getCurrentDateTime();
    LOGGER.info("Sending following Important Topics {} and current date time {}", message, currentDateTime);
    rabbitTemplate.convertAndSend(IMPORTANT_TOPICS_EXCHANGE_NAME_1, "com.acme.general", new ImportantTopic(message, currentDateTime));
  }

}
