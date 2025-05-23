package org.example.phishing_control_service;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.kafka.annotation.EnableKafka;

@EnableKafka
@EnableFeignClients
@EnableCaching
@SpringBootApplication
public class PhishingControlServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(PhishingControlServiceApplication.class, args);
    }

}
