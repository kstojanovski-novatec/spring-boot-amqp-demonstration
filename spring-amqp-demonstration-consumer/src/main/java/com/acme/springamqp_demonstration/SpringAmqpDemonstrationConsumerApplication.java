package com.acme.springamqp_demonstration;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
public class SpringAmqpDemonstrationConsumerApplication {

  public static void main(String[] args) {
    SpringApplication.run(SpringAmqpDemonstrationConsumerApplication.class, args);
  }

}
