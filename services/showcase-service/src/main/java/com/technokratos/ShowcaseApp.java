package com.technokratos;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.ConfigurableApplicationContext;

@SpringBootApplication
@EnableFeignClients //буквально: "этот сервис будет слать запросы другим"
public class ShowcaseApp {
    public static void main(String[] args) {
        SpringApplication.run(ShowcaseApp.class, args);
    }
}