package com.natal.rabbitmq.abstractor.consumer;

import com.natal.rabbitmq.abstractor.configuration.AmqpConnection;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PreDestroy;
import java.io.IOException;

@Service
public class ConsumerInitializationService implements ConsumerInitializer{

    private Logger log = LoggerFactory.getLogger(ConsumerInitializationService.class);
    private static final String TOPIC_EXCHANGE_TYPE = "topic";

    @Autowired
    private AmqpConnection amqpConnection;

    @PreDestroy
    public void destroy() {
        log.info("Closing AMQP Broker Connection");
        try {
            Connection consumerConnection = amqpConnection.getConsumerInstance().getConnection();
            if(consumerConnection.isOpen()) {
                consumerConnection.close();
            }
        } catch (Exception e) {
            log.error("Error while closing AMQP Broker connection ", e);
        }
    }

    public void initialize(Consumer... consumers) {
        try {
            log.info("Registering consumer");
            for(Consumer consumer : consumers) {
                start(consumer);
            }
            log.info("Consumers registered");
        } catch (Exception e) {
            log.error("Error while binding queues and exchanges");
            throw e;
        }
    }

    private void start(Consumer consumer) {
        boolean autoAck = false;
        final boolean durable = true;
        final boolean exclusive = false;
        final boolean autoDelete = false;
        final String topic = consumer.getTopicName();
        final String queue = consumer.getQueueName();
        final String routingKey = consumer.getRoutingKey();
        try {
            Channel channel = consumer.getConsumer().getChannel();
            log.info("Consumer to be registered: " + consumer.getConsumer().getClass().getCanonicalName());

            if(consumer.hasToCreateTopic()) {
                channel.exchangeDeclare(topic, TOPIC_EXCHANGE_TYPE, durable);
            } else {
                channel.exchangeDeclarePassive(topic);
            }

            channel.queueDeclare(queue, durable, exclusive, autoDelete, null);
            channel.queueBind(queue, topic, routingKey);
            channel.basicConsume(queue, autoAck, consumer.getConsumer());

        } catch (IOException e) {
            log.error("Error while binding queues and exchanges ", e);
        }
    }

}
