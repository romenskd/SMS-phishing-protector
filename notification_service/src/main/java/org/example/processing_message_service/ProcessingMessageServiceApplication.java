package org.example.processing_message_service;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.kafka.annotation.EnableKafka;

@EnableKafka
@EnableFeignClients
@SpringBootApplication
public class ProcessingMessageServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(ProcessingMessageServiceApplication.class, args);
    }

}
