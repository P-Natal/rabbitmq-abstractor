package com.natal.rabbitmq.abstractor.publisher;

import com.natal.rabbitmq.abstractor.configuration.AmqpChannel;
import com.natal.rabbitmq.abstractor.configuration.AmqpConnection;
import com.rabbitmq.client.Channel;
import org.springframework.beans.factory.annotation.Autowired;

public abstract class AmqpPublisher implements Publisher{

    private Channel channel;
    private String messageBody;

    @Autowired
    private AmqpPublisherService amqpPublisherService;

    public AmqpPublisher(@Autowired AmqpConnection amqpConnection) {
        this.channel = new AmqpChannel(
                amqpConnection.getPublisherInstance().getConnection()
        ).getChannel();
    }

    @Override
    public final void publish(String messageBody) {
        this.messageBody = messageBody;
         amqpPublisherService.publish(this);
    }

    @Override
    public final String getMessageBody() {
        return messageBody;
    }

    @Override
    public String getRoutingKey() {
        return DEFAULT_ROUTING_KEY;
    }

    @Override
    public final Channel getChannel() {
        return channel;
    }
}
