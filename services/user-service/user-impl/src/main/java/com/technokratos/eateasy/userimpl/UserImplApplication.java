package com.technokratos.eateasy.userimpl;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
@EntityScan("com.technokratos.eateasy.userimpl.model")
@ComponentScan({
        "com.technokratos.eateasy.userimpl", // Основной пакет модуля
        "com.technokratos.eateasy.userapi"   // Если нужно явно подключить API
})
@EnableJpaRepositories("com.technokratos.eateasy.userimpl.repository")
@EnableAspectJAutoProxy
public class UserImplApplication {

    public static void main(String[] args) {
        SpringApplication.run(UserImplApplication.class, args);
    }

}
