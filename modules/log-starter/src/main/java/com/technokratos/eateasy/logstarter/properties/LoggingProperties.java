package com.technokratos.eateasy.logstarter.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "app.logging")
@Data
public class LoggingProperties {
    private boolean enabled = true;
    private boolean logParameters = true;
    private boolean logReturnValues = true;
    private boolean logExecutionTime = true;
}