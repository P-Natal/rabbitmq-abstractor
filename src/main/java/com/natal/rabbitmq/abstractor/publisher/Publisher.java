package com.natal.rabbitmq.abstractor.publisher;

import com.rabbitmq.client.Channel;

public interface Publisher {
    String DEFAULT_ROUTING_KEY = "#";
    String getTopicName();
    String getRoutingKey();
    String getMessageBody();
    Channel getChannel();
    void publish(String message);
}
