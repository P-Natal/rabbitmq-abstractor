package com.natal.rabbitmq.abstractor.publisher;

import com.natal.rabbitmq.abstractor.configuration.AmqpConnection;
import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.Connection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.io.IOException;

@Service
public class AmqpPublisherService {

    private static final Logger log = LoggerFactory.getLogger(AmqpPublisherService.class);
    private static final String APPLICATION_JSON_CONTENT_TYPE = "application/json";
    private Connection publisherConnection;
    private static final int DELIVERY_MODE_PERSISTENT = 2;

    @Autowired
    private AmqpConnection amqpConnection;

    @PostConstruct
    public void init() {
        publisherConnection = amqpConnection.getPublisherInstance().getConnection();
    }

    @PreDestroy
    public void destroy() {
        log.info("Closing connections between publishers and Broker");
        try {
            if(publisherConnection.isOpen()) {
                publisherConnection.close();
            }
        } catch (Exception e) {
           log.error("Error while closing connection with Broker ", e);
        }
    }

    void publish(Publisher publisher) {
        byte[] messageBody = null;

        if(!StringUtils.isEmpty(publisher.getMessageBody())) {
            messageBody = publisher.getMessageBody().getBytes();
        }

        try {
            publisher.getChannel().basicPublish(publisher.getTopicName(), publisher.getRoutingKey(),
                    new AMQP.BasicProperties.Builder()
                            .contentType(APPLICATION_JSON_CONTENT_TYPE)
                            .deliveryMode(DELIVERY_MODE_PERSISTENT)
                            .build(),
                            messageBody
                    );
            log.info(String.format("Message published: [%s]", publisher.getMessageBody()));
        } catch (IOException e) {
            log.error(String.format("Failed to publish message [%s]", publisher.getMessageBody()), e);
        }
    }
}
