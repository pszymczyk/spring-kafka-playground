package com.pszymczyk.playground.app2.server;

import com.pszymczyk.application.services.OrderService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.handler.annotation.SendTo;

@SpringBootApplication
public class App2Server {

    private final Logger logger = LoggerFactory.getLogger(App2Server.class);

    public static void main(String[] args) {
        SpringApplication.run(App2Server.class, args);
    }

    @KafkaListener(id = "app2-server", topics = "app2-requests")
    @SendTo
    public String listen(String in) {
        logger.info("Server received {}", in);
        return "PONG";
    }
}
