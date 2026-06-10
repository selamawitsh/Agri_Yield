package com.agriyield.offtakerservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class OfftakerServiceApplication {
    public static void main(String[] args) {
        SpringApplication.run(OfftakerServiceApplication.class, args);
    }
}
