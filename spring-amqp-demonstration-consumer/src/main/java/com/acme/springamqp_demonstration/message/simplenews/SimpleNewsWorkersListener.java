package com.acme.springamqp_demonstration.message.simplenews;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Service;

@Service
public class SimpleNewsWorkersListener {

  @Bean
  public SimpleNewsWorker receiveSimpleNews1Consumer1() {
    return new SimpleNewsWorker(1);
  }

  @Bean
  public SimpleNewsWorker receiveSimpleNews1Consumer2() {
    return new SimpleNewsWorker(2);
  }


}
