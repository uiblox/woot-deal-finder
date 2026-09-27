package com.example.wootdeals;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@ConfigurationPropertiesScan // finds @ConfigurationProperties records like WootProperties
@EnableCaching               // turns on @Cacheable
@EnableScheduling            // turns on @Scheduled
public class WootDealsApplication {

    public static void main(String[] args) {
        SpringApplication.run(WootDealsApplication.class, args);
    }
}
