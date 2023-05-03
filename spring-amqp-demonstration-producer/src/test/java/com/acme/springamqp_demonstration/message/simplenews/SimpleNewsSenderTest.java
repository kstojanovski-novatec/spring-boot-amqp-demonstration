package com.acme.springamqp_demonstration.message.simplenews;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.amqp.rabbit.core.RabbitTemplate;

import static org.assertj.core.api.AssertionsForClassTypes.assertThatCode;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.startsWith;

public class SimpleNewsSenderTest {

  private SimpleNewsSender simpleNewsSender;
  private RabbitTemplate rabbitTemplateMock;

  String simpleNewsExchangeName = "com.acme.simple-news.exchange";
  String simpleNewsRoutingKey = "";

  @BeforeEach
  public void setUp() {
    rabbitTemplateMock = Mockito.mock(RabbitTemplate.class);
    simpleNewsSender = new SimpleNewsSender(rabbitTemplateMock, simpleNewsExchangeName, simpleNewsRoutingKey);
  }

  @Test
  public void testBroadcast() {
    assertThatCode(() -> this.simpleNewsSender.sendSimpleNews()).doesNotThrowAnyException();
    Mockito.verify(this.rabbitTemplateMock)
        .convertAndSend(eq(simpleNewsRoutingKey), eq(simpleNewsExchangeName), startsWith("simple news "));
  }

}
