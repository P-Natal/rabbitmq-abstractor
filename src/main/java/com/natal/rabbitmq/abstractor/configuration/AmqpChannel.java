package com.natal.rabbitmq.abstractor.configuration;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

public class AmqpChannel {

    private static final Logger log = LoggerFactory.getLogger(AmqpChannel.class);

    private Channel channel;

    public AmqpChannel(Connection connection){
        log.info("Creating AMQP Channel");
        try{
            channel = connection.createChannel();
        } catch (IOException e) {
            log.error("Error while creating AMQP Channel ", e);
        }
    }

    public Channel getChannel(){
        return channel;
    }

}
