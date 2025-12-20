package com.smartpos;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@EnableAsync
public class SmartPOSApplication {

    public static void main(String[] args) {
        SpringApplication.run(SmartPOSApplication.class, args);
    }
}
