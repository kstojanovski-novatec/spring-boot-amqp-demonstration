package com.acme.springamqp_demonstration.message.simplenews;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Service;

@PropertySource("classpath:simple-news.properties")
@Service
public class SimpleNewsListener {

  private static final Logger LOGGER = LoggerFactory.getLogger(SimpleNewsListener.class);

  @Bean
  public SimpleNewsWorker receiveSimpleNews1Consumer1() {
    return new SimpleNewsWorker(1);
  }

  @Bean
  public SimpleNewsWorker receiveSimpleNews1Consumer2() {
    return new SimpleNewsWorker(2);
  }

  @RabbitListener(queues = { "${simple.news.queue.name.2}" })
  public void receiveSimpleNews2(String message) {
    LOGGER.info("Received Simple News from Queue 2: " + message);
  }

}
