package com.acme.springamqp_demonstration.message.specialmessages;

import com.acme.springamqp_demonstration.message.CurrentDateTimeProvider;
import com.acme.springamqp_demonstration.message.specialmessages.model.SpecialMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@PropertySource("classpath:special-messages.properties")
@Service
public class SpecialMessagesSender {

  private static final Logger LOGGER = LoggerFactory.getLogger(SpecialMessagesSender.class);

  @Value("${special.messages.exchange.name1}")
  private String SPECIAL_MESSAGE_EXCHANGE_NAME_1;

  @Value("${special.messages.routing.key}")
  private String SPECIAL_MESSAGES_ROUTING_KEY;

  private final RabbitTemplate rabbitTemplate;

  private final CurrentDateTimeProvider currentDateTimeProvider = new CurrentDateTimeProvider();

  public SpecialMessagesSender(RabbitTemplate rabbitTemplate) {
    this.rabbitTemplate = rabbitTemplate;
  }

  @Scheduled(cron = "${special.messages.sender1.cron}")
  private void reportSpecialMessages() {
    specialMessages(rabbitTemplate, new SpecialMessagePropertiesFactory("sales", 1)
        .createMessageProperties());
    specialMessages(rabbitTemplate, new SpecialMessagePropertiesFactory("customer", 1)
        .createMessageProperties());
    specialMessages(rabbitTemplate, new SpecialMessagePropertiesFactory("sales", 2)
        .createMessageProperties());
    specialMessages(rabbitTemplate, new SpecialMessagePropertiesFactory("customer", 2)
        .createMessageProperties());
  }

  private void specialMessages(
      RabbitTemplate rabbitTemplate,
      MessageProperties messageProperties
  ) {
    Message message = new Jackson2JsonMessageConverter().toMessage(
        new SpecialMessage("Special Message ", currentDateTimeProvider.getCurrentDateTime()),
        messageProperties
    );    
    LOGGER.info("Sending following Special Message with the header 'from': {} and 'pricingModel': {}.",
        messageProperties.getHeader("from"), messageProperties.getHeader("pricingModel"));
    rabbitTemplate.convertAndSend(SPECIAL_MESSAGE_EXCHANGE_NAME_1, SPECIAL_MESSAGES_ROUTING_KEY, message);

  }

}
