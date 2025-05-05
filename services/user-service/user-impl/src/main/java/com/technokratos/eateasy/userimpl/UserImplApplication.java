package com.technokratos.eateasy.userimpl;


import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
@EntityScan("com.technokratos.eateasy.userdb.model")
@EnableJpaRepositories("com.technokratos.eateasy.userdb.repository")
@ComponentScan("com.technokratos.eateasy")
public class UserImplApplication {

    public static void main(String[] args) {
        SpringApplication.run(UserImplApplication.class, args);
    }

}
