package com.technokratos.eateasy.logstarter.autoconfigure;


import com.technokratos.eateasy.logstarter.properties.LoggingProperties;
import com.technokratos.eateasy.logstarter.aspect.LoggingAspect;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;

@Configuration
@EnableConfigurationProperties(LoggingProperties.class)
@ConditionalOnProperty(prefix = "app.logging", name = "enabled", havingValue = "true", matchIfMissing = true)
public class LoggingAutoConfiguration {
    @Bean
    @Lazy
    public LoggingAspect loggingAspect( LoggingProperties properties) {
        return new LoggingAspect(properties);
    }
}