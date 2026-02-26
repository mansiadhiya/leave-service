package com.company.leave.config;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.QueueBuilder;
import org.springframework.amqp.rabbit.config.SimpleRabbitListenerContainerFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import lombok.extern.slf4j.Slf4j;

@Configuration
@Slf4j
public class RabbitMQConfig {

    @Value("${rabbitmq.queue.leave-events}")
    private String leaveEventsQueue;

    @Value("${rabbitmq.queue.leave-events-dlq}")
    private String leaveEventsDLQ;

    @Value("${rabbitmq.exchange.leave-events}")
    private String leaveEventsExchange;

    @Value("${rabbitmq.exchange.leave-events-dlx}")
    private String leaveEventsDLX;

    @Value("${rabbitmq.routing-key.leave-events}")
    private String leaveEventsRoutingKey;

    @Value("${rabbitmq.routing-key.leave-events-dlq}")
    private String leaveEventsDLQRoutingKey;

    @Bean
    public Queue leaveEventsQueue() {
        return QueueBuilder.durable(leaveEventsQueue)
                .deadLetterExchange(leaveEventsDLX)
                .deadLetterRoutingKey(leaveEventsDLQRoutingKey)
                .build();
    }

    @Bean
    public Queue leaveEventsDLQ() {
        return QueueBuilder.durable(leaveEventsDLQ).build();
    }

    @Bean
    public DirectExchange leaveEventsExchange() {
        return new DirectExchange(leaveEventsExchange);
    }

    @Bean
    public DirectExchange leaveEventsDLX() {
        return new DirectExchange(leaveEventsDLX);
    }

    @Bean
    public Binding leaveEventsBinding() {
        return BindingBuilder.bind(leaveEventsQueue())
                .to(leaveEventsExchange())
                .with(leaveEventsRoutingKey);
    }

    @Bean
    public Binding leaveEventsDLQBinding() {
        return BindingBuilder.bind(leaveEventsDLQ())
                .to(leaveEventsDLX())
                .with(leaveEventsDLQRoutingKey);
    }

    @Bean
    public Jackson2JsonMessageConverter messageConverter() {
        return new Jackson2JsonMessageConverter();
    }

    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory) {
        RabbitTemplate template = new RabbitTemplate(connectionFactory);
        template.setMessageConverter(messageConverter());
        return template;
    }

    @Bean
    public SimpleRabbitListenerContainerFactory rabbitListenerContainerFactory(ConnectionFactory connectionFactory) {
        SimpleRabbitListenerContainerFactory factory = new SimpleRabbitListenerContainerFactory();
        factory.setConnectionFactory(connectionFactory);
        factory.setMessageConverter(messageConverter());
        factory.setDefaultRequeueRejected(false);
        return factory;
    }
}
