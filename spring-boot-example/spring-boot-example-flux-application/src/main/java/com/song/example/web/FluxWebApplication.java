package com.song.example.web;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(
   scanBasePackages = { "com.song.example" }
)
public class FluxWebApplication {
    public static void main(String[] args) {
        SpringApplication.run(FluxWebApplication.class, args);
    }
}
