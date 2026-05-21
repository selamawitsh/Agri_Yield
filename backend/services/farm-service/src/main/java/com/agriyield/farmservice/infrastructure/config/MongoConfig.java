package com.agriyield.farmservice.infrastructure.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.config.EnableMongoAuditing;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

@Configuration
@EnableMongoAuditing
@EnableMongoRepositories(
    basePackages = "com.agriyield.farmservice.infrastructure.adapter.outgoing.mongodb"
)
public class MongoConfig {
}
