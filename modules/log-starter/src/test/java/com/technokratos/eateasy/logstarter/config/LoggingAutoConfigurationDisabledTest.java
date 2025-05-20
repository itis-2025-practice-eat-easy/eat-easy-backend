package com.technokratos.eateasy.logstarter.config;

import com.technokratos.eateasy.logstarter.autoconfigure.LoggingAutoConfiguration;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.ImportAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Configuration;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(
        classes = LoggingAutoConfigurationDisabledTest.TestConfig.class,
        properties = "app.logging.enabled=false"
)
@ImportAutoConfiguration(LoggingAutoConfiguration.class)
class LoggingAutoConfigurationDisabledTest {

    @Configuration
    static class TestConfig {
        // Пустая конфигурация
    }

    @Autowired
    private ApplicationContext context;

    @Test
    void loggingAspectShouldNotBePresent() {
        assertThat(context.containsBeanDefinition("loggingAspect")).isFalse();
    }
}
