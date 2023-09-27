package com.example.configs;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.listener.SimpleMessageListenerContainer;
import org.springframework.amqp.rabbit.listener.adapter.MessageListenerAdapter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.example.rabbit.Receiver;

//this configuration creates a connection with an existing rabbitmq server
//only works if a rabbitmq server is running on this PC
@Configuration
public class RabbitConfig {
	// name for queue and topic
	public static final String topicExchangeName = "spring-boot-exchange";
	public static final String queueName = "spring-boot";


	@Bean
	Queue queue() {
		return new Queue(queueName, false);
	}

	@Bean
	TopicExchange exchange() {
		return new TopicExchange(topicExchangeName);
	}

	@Bean
	Binding binding(Queue queue, TopicExchange exchange) {
		return BindingBuilder.bind(queue).to(exchange).with("foo.bar.#");
	}
	
	@Bean
	ConnectionFactory connectionFactory(){
	    CachingConnectionFactory connectionFactory =new CachingConnectionFactory() ;
	    connectionFactory.setCacheMode(CachingConnectionFactory.CacheMode.CHANNEL);
	    connectionFactory.setHost("localhost");
	    connectionFactory.setVirtualHost("/");
	    connectionFactory.setPort(5672);
	    connectionFactory.setUsername("backendUser");
	    connectionFactory.setPassword("backendPass");
	    return connectionFactory;
	}

	@Bean
	SimpleMessageListenerContainer container(ConnectionFactory connectionFactory,
			MessageListenerAdapter listenerAdapter) {
		SimpleMessageListenerContainer container = new SimpleMessageListenerContainer();
		container.setConnectionFactory(connectionFactory);
		container.setQueueNames(queueName);
		container.setMessageListener(listenerAdapter);
		return container;
	}

	// adapter to be used by the receiver class
	@Bean
	MessageListenerAdapter listenerAdapter(Receiver receiver) {
		return new MessageListenerAdapter(receiver, "receiveMessage");
	}
}
