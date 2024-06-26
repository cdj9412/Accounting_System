package com.sparta;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class StreamingApplication {
    public static void main(String[] args) {
        SpringApplication.run(StreamingApplication.class, args);
        System.out.println("Streaming Application started");
        System.out.println("test");
    }
}
