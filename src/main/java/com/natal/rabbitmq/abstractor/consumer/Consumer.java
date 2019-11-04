package com.natal.rabbitmq.abstractor.consumer;

import com.rabbitmq.client.DefaultConsumer;

public interface Consumer {
    String DEFAULT_ROUTING_KEY = "#";
    String getTopicName();
    String getQueueName();
    String getRoutingKey();
    DefaultConsumer getConsumer();
    boolean hasToCreateTopic();
}
