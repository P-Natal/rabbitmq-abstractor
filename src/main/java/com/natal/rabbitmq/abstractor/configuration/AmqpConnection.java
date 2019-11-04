package com.natal.rabbitmq.abstractor.configuration;

import com.rabbitmq.client.Address;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

@Component
public final class AmqpConnection {

    private static final Logger log = LoggerFactory.getLogger(AmqpConnection.class);
    private static String RABBITMQ_USERNAME;
    private static String RABBITMQ_PASSWORD;
    private static String RABBITMQ_VHOST;
    private static String RABBITMQ_ADDRESSES;
    private static String RABBITMQ_HOST;
    private static int RABBITMQ_PORT;

    private static AmqpConnection CONSUMER_INSTANCE;
    private static AmqpConnection PUBLISHER_INSTANCE;

    private Connection connection;
    private ConnectionFactory connectionFactory;
    private Address[] addresses;

    @Autowired
    private AmqpConnection(@Value("${spring.rabbitmq.username}") String rabbitmqUsername,
                           @Value("${spring.rabbitmq.password}") String rabbitmqPassword,
                           @Value("${spring.rabbitmq.vhost}") String rabbitmqVirtualHost,
                           @Value("${spring.rabbitmq.addresses}") String rabbitmqAddresses,
                           @Value("${spring.rabbitmq.host}") String rabbitmqHost,
                           @Value("${spring.rabbitmq.port}") int rabbitmqPort) {
        RABBITMQ_USERNAME = rabbitmqUsername;
        RABBITMQ_PASSWORD = rabbitmqPassword;
        RABBITMQ_VHOST = rabbitmqVirtualHost;
        RABBITMQ_ADDRESSES = rabbitmqAddresses;
        RABBITMQ_HOST = rabbitmqHost;
        RABBITMQ_PORT = rabbitmqPort;
    }

    public AmqpConnection() {
        try {
            connectionFactory = new ConnectionFactory();
            connectionFactory.setUsername(RABBITMQ_USERNAME);
            connectionFactory.setPassword(RABBITMQ_PASSWORD);
            connectionFactory.setVirtualHost(RABBITMQ_VHOST);
            connectionFactory.setHost(RABBITMQ_HOST);
            connectionFactory.setPort(RABBITMQ_PORT);
            addresses = getAddresses(RABBITMQ_ADDRESSES);
            log.info("Creating connection with AMQP Service");
            connection = connectionFactory.newConnection(addresses);
        } catch (TimeoutException | IOException e) {
            log.error("Error creating AMQP Connection. ", e);
        }
    }

    private Address[] getAddresses(String rabbitmqAddresses) {
        String[] rabbitMQAddresses = rabbitmqAddresses.split(",");
        if(rabbitMQAddresses.length > 1) {
            return Address.parseAddresses(rabbitmqAddresses);
        } else {
            return new Address[]{Address.parseAddress(rabbitmqAddresses)};
        }
    }

    public synchronized AmqpConnection getConsumerInstance() {
        if (CONSUMER_INSTANCE == null || !CONSUMER_INSTANCE.getConnection().isOpen()){
            try {
                CONSUMER_INSTANCE = new AmqpConnection();
            } catch (Exception e) {
                log.error("Nao foi possivel recuperar conexao com broker", e);
            }
        }
        return CONSUMER_INSTANCE;
    }

    public synchronized AmqpConnection getPublisherInstance() {
        if (PUBLISHER_INSTANCE == null || !PUBLISHER_INSTANCE.getConnection().isOpen()){
            try {
                PUBLISHER_INSTANCE = new AmqpConnection();
            } catch (Exception e) {
                log.error("Nao foi possivel recuperar conexao com broker", e);
            }
        }
        return PUBLISHER_INSTANCE;
    }

    public Connection getConnection() {
        return connection;
    }

}
