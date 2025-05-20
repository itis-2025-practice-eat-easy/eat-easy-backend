package com.technokratos.eateasy.logstarter.presence;

import com.technokratos.eateasy.logstarter.autoconfigure.LoggingAutoConfiguration;
import com.technokratos.eateasy.logstarter.aspect.LoggingAspect;
import com.technokratos.eateasy.logstarter.properties.LoggingProperties;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.ImportAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Configuration;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(classes = LoggingBeansPresenceTest.TestConfig.class,
        properties = {
                "app.logging.enabled=true",
                "app.logging.log-parameters=true",
                "app.logging.log-return-values=true",
                "app.logging.log-execution-time=true"
        })
@ImportAutoConfiguration(LoggingAutoConfiguration.class)
class LoggingBeansPresenceTest {

    @Configuration
    static class TestConfig {
        // Пустая конфигурация: нужна только для запуска контекста
    }

    @Autowired
    private ApplicationContext context;

    @Test
    void loggingPropertiesBeanShouldBePresent() {
        assertThat(context.getBean(LoggingProperties.class)).isNotNull();
    }

    @Test
    void loggingAspectBeanShouldBePresent() {
        assertThat(context.getBean(LoggingAspect.class)).isNotNull();
    }

    @Test
    void loggingAutoConfigurationShouldBePresent() {
        assertThat(context.getBean(LoggingAutoConfiguration.class)).isNotNull();
    }
}
