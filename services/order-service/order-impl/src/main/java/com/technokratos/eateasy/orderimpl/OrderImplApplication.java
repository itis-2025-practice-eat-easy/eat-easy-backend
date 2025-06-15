package com.technokratos.eateasy.orderimpl;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
@EntityScan("com.technokratos.eateasy.userimpl.model")
@ComponentScan({
        "com.technokratos.eateasy.orderimpl",
        "com.technokratos.eateasy.orderapi"
})
@EnableJpaRepositories("com.technokratos.eateasy.orderimpl.repository")
@EnableAspectJAutoProxy
@EnableFeignClients
public class OrderImplApplication {
    public static void main(String[] args) {
        SpringApplication.run(OrderImplApplication.class, args);
    }
}
