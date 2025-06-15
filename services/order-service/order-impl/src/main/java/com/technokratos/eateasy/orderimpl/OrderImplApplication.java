package com.technokratos.eateasy.orderimpl;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@EnableFeignClients
@ComponentScan({
        "com.technokratos.eateasy.common.exceptionhandler",
        "com.technokratos.eateasy.orderimpl",
        "com.technokratos.eateasy.orderapi"
})
public class OrderImplApplication {
    public static void main(String[] args) {
        SpringApplication.run(OrderImplApplication.class, args);
    }
}
