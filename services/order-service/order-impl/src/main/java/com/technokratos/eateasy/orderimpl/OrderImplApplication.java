package com.technokratos.eateasy.orderimpl;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableFeignClients
public class OrderImplApplication {
    public static void main(String[] args) {
        SpringApplication.run(OrderImplApplication.class, args);
    }
}
