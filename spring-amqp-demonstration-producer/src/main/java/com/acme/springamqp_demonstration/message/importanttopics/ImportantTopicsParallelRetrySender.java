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
public class ImportantTopicsParallelRetrySender {

  private static final Logger LOGGER = LoggerFactory.getLogger(ImportantTopicsParallelRetrySender.class);

  @Value("${important.topics.exchange.name.pr}")
  private String IMPORTANT_TOPICS_EXCHANGE_NAME_PR;

  private final RabbitTemplate rabbitTemplate;

  private final CurrentDateTimeProvider currentDateTimeProvider = new CurrentDateTimeProvider();

  public ImportantTopicsParallelRetrySender(RabbitTemplate rabbitTemplate) {
    this.rabbitTemplate = rabbitTemplate;
  }

  @Scheduled(cron = "${important.topics.sender.pr.cron}")
  private void reportCurrentTime() {
    sendImportantTopicsObjects(rabbitTemplate);
  }

  private void sendImportantTopicsObjects(RabbitTemplate rabbitTemplate) {
    String message = "Important Topics - general 2";
    String currentDateTime = currentDateTimeProvider.getCurrentDateTime();
    LOGGER.info("Sending following Important Topics {} and current date time {}", message, currentDateTime);
    rabbitTemplate.convertAndSend(IMPORTANT_TOPICS_EXCHANGE_NAME_PR, "com.acme.general", new ImportantTopic(message, currentDateTime));
  }

}
