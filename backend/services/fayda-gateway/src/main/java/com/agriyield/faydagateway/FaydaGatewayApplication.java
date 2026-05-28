package com.agriyield.faydagateway;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@SpringBootApplication
@EnableDiscoveryClient
public class FaydaGatewayApplication {
    public static void main(String[] args) {
        SpringApplication.run(FaydaGatewayApplication.class, args);
    }
}
