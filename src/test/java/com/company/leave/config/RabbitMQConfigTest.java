package com.company.leave.config;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.test.util.ReflectionTestUtils;

import static org.mockito.Mockito.*;

class RabbitMQConfigTest {

    private final RabbitMQConfig config = new RabbitMQConfig();

    @Test
    void leaveEventsQueue_CreatesQueue() {
        ReflectionTestUtils.setField(config, "leaveEventsQueue", "leave-events");
        ReflectionTestUtils.setField(config, "leaveEventsDLX", "leave-events-dlx");
        ReflectionTestUtils.setField(config, "leaveEventsDLQRoutingKey", "leave-events-dlq");
        
        Queue queue = config.leaveEventsQueue();
        
        assertNotNull(queue);
        assertEquals("leave-events", queue.getName());
    }

    @Test
    void leaveEventsDLQ_CreatesDeadLetterQueue() {
        ReflectionTestUtils.setField(config, "leaveEventsDLQ", "leave-events-dlq");
        
        Queue queue = config.leaveEventsDLQ();
        
        assertNotNull(queue);
        assertEquals("leave-events-dlq", queue.getName());
    }

    @Test
    void leaveEventsExchange_CreatesExchange() {
        ReflectionTestUtils.setField(config, "leaveEventsExchange", "leave-exchange");
        
        DirectExchange exchange = config.leaveEventsExchange();
        
        assertNotNull(exchange);
        assertEquals("leave-exchange", exchange.getName());
    }

    @Test
    void leaveEventsDLX_CreatesDeadLetterExchange() {
        ReflectionTestUtils.setField(config, "leaveEventsDLX", "leave-dlx");
        
        DirectExchange exchange = config.leaveEventsDLX();
        
        assertNotNull(exchange);
        assertEquals("leave-dlx", exchange.getName());
    }

    @Test
    void leaveEventsBinding_CreatesBinding() {
        ReflectionTestUtils.setField(config, "leaveEventsQueue", "leave-events");
        ReflectionTestUtils.setField(config, "leaveEventsExchange", "leave-exchange");
        ReflectionTestUtils.setField(config, "leaveEventsRoutingKey", "leave-key");
        ReflectionTestUtils.setField(config, "leaveEventsDLX", "leave-dlx");
        ReflectionTestUtils.setField(config, "leaveEventsDLQRoutingKey", "leave-dlq-key");
        
        Binding binding = config.leaveEventsBinding();
        
        assertNotNull(binding);
    }

    @Test
    void messageConverter_CreatesConverter() {
        assertNotNull(config.messageConverter());
    }

    @Test
    void rabbitTemplate_CreatesTemplate() {
        ConnectionFactory connectionFactory = mock(ConnectionFactory.class);
        
        RabbitTemplate template = config.rabbitTemplate(connectionFactory);
        
        assertNotNull(template);
    }

    @Test
    void rabbitListenerContainerFactory_CreatesFactory() {
        ConnectionFactory connectionFactory = mock(ConnectionFactory.class);
        
        assertNotNull(config.rabbitListenerContainerFactory(connectionFactory));
    }
}
