package com.acme.springamqp_demonstration.message;

import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.SimpleMessageConverter;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;

@TestConfiguration
public class MessageConverterBeans {
  @Bean
  public Jackson2JsonMessageConverter jackson2Converter() {
    return new Jackson2JsonMessageConverter();
  }

  @Bean
  public SimpleMessageConverter simpleMessageConverter() {
    return new SimpleMessageConverter();
  }
}
