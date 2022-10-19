package com.exampletenpo.calculate.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.web.cors.CorsConfiguration;

@Data
@ConfigurationProperties(prefix = "application", ignoreUnknownFields = true)
public class ApplicationProperties {
    private final CorsConfiguration cors = new CorsConfiguration();

    @Data
    public static class ClientApp {
        private String name = "TenpoCalular";
    }
}
