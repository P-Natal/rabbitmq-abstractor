package com.natal.rabbitmq.abstractor.consumer;

import com.natal.rabbitmq.abstractor.configuration.AmqpChannel;
import com.natal.rabbitmq.abstractor.configuration.AmqpConnection;
import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.DefaultConsumer;
import com.rabbitmq.client.Envelope;
import com.rabbitmq.client.ShutdownSignalException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

public abstract class AmqpConsumer extends DefaultConsumer implements Consumer {

    private Logger log = LoggerFactory.getLogger(AmqpConsumer.class);

    @Autowired
    private ConsumerInitializer consumerInitializer;

    @PostConstruct
    public void init(){
        consumerInitializer.initialize(this);
    }

    public AmqpConsumer(@Autowired AmqpConnection amqpConnection) {
        super(new AmqpChannel(
                amqpConnection.getConsumerInstance().getConnection()
        ).getChannel());
    }

    public abstract void  callback(String message);

    @Override
    public void handleDelivery(String consumerTag,
                               Envelope envelope,
                               AMQP.BasicProperties properties,
                               byte[] body) throws IOException {
        try{
            getChannel().basicAck(envelope.getDeliveryTag(), false);
            String message = new String(body, StandardCharsets.UTF_8);
            log.info("Message received: " + message);
            callback(message);
            log.info("Message processed.");
        } catch (Exception e){
            log.error("Error while reading received message ", e);
        }
    }

    @Override
    public final void handleShutdownSignal(String consumerTag, ShutdownSignalException sig) {
        String template = "AMQP Connection Channel Closed. ConsumerTag: %s, %s";
        log.warn(String.format(template, consumerTag, sig.getReason()));
    }

    @Override
    public boolean hasToCreateTopic() {
        return true;
    }

    @Override
    public DefaultConsumer getConsumer() {
        return this;
    }

    @Override
    public String getRoutingKey() {
        return DEFAULT_ROUTING_KEY;
    }
}
