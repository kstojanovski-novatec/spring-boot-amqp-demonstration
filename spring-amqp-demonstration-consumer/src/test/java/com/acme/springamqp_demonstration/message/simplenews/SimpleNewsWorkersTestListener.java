package com.acme.springamqp_demonstration.message.simplenews;

import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Service;

@Service
public class SimpleNewsWorkersTestListener {

  @Bean
  public SimpleNewsWorkerTest receiveSimpleNews1Consumer1() {
    return new SimpleNewsWorkerTest(1);
  }

  @Bean
  public SimpleNewsWorkerTest receiveSimpleNews1Consumer2() {
    return new SimpleNewsWorkerTest(2);
  }


}
