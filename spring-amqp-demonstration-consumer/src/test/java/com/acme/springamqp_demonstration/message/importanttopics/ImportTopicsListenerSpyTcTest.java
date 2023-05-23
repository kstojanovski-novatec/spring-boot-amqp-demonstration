package com.acme.springamqp.testingissue.differenttypes;

import com.acme.springamqp.testingissue.RabbitMqTestContainer;
import com.acme.springamqp.testingissue.RabbitTemplateTestBeans;
import com.acme.springamqp.testingissue.config.DefaultContainerFactoryConfig;
import com.acme.springamqp.testingissue.config.MessageConverterBeans;
import com.acme.springamqp.testingissue.differenttypes.config.DifferentTypesConfig;
import com.acme.springamqp.testingissue.differenttypes.model.SimpleMessage;
import org.junit.jupiter.api.Test;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.rabbit.test.RabbitListenerTest;
import org.springframework.amqp.rabbit.test.RabbitListenerTestHarness;
import org.springframework.amqp.rabbit.test.mockito.LatchCountDownAndCallRealMethodAnswer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.willAnswer;
import static org.mockito.Mockito.verify;

@SpringJUnitConfig
@DirtiesContext
@PropertySource("classpath:different-types.properties")
@ContextConfiguration(
		classes = {
				MessageConverterBeans.class,
				RabbitTemplateTestBeans.class,
				DefaultContainerFactoryConfig.class,
				DifferentTypesConfig.class,
				DifferentTypesDistributorListener.class,
				DifferentTypesDistributorListenerSpyTcTest.Config.class
		}
)
public class DifferentTypesDistributorListenerSpyTcTest extends RabbitMqTestContainer {

	@Autowired
	private RabbitTemplate jsonRabbitTemplate;

	@Autowired
	private RabbitListenerTestHarness harness;

	@Value("${different.types.queue.name.message-types}")
	private String DIFFERENT_TYPES_QUEUE_NAME;

	@Test
	public void testingMultiHandler() throws Exception {
		DifferentTypesDistributorListener differentTypesDistributorListener = this.harness.getSpy("message-types");
		assertThat(differentTypesDistributorListener).isNotNull();

		LatchCountDownAndCallRealMethodAnswer answer = this.harness.getLatchAnswerFor("message-types", 4);
		willAnswer(answer).given(differentTypesDistributorListener).receiveGeneralTopicsString(anyString());
		willAnswer(answer).given(differentTypesDistributorListener).receiveGeneralTopics(any());

		String bar = "bar";
		SimpleMessage sm1 = new SimpleMessage(bar);
		String baz = "baz";
		SimpleMessage sm2 = new SimpleMessage(baz);
		this.jsonRabbitTemplate.convertAndSend(DIFFERENT_TYPES_QUEUE_NAME, bar);
		this.jsonRabbitTemplate.convertAndSend(DIFFERENT_TYPES_QUEUE_NAME, baz);
		this.jsonRabbitTemplate.convertAndSend(DIFFERENT_TYPES_QUEUE_NAME, sm1);
		this.jsonRabbitTemplate.convertAndSend(DIFFERENT_TYPES_QUEUE_NAME, sm2);

		assertThat(answer.await(10)).isTrue();
		verify(differentTypesDistributorListener).receiveGeneralTopicsString(bar);
		verify(differentTypesDistributorListener).receiveGeneralTopicsString(baz);
		verify(differentTypesDistributorListener).receiveGeneralTopics(sm1);
		verify(differentTypesDistributorListener).receiveGeneralTopics(sm2);
	}

	@Configuration
	@RabbitListenerTest
	public static class Config {

		@Bean
		public DifferentTypesDistributorListener differentTypesDistributorListener() {
			return new DifferentTypesDistributorListener();
		}

	}

}
