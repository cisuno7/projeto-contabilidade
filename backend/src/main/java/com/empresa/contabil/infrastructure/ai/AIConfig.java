package com.empresa.contabil.infrastructure.ai;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "ai.service")
@Getter
@Setter
public class AIConfig {
    private String apiKey;
    private String baseUrl;
    private Integer timeout;
    private String model;
}
