package com.natal.rabbitmq.abstractor.consumer;

import org.springframework.stereotype.Service;

@Service
public interface ConsumerInitializer {
    void initialize(Consumer... consumers);
}
