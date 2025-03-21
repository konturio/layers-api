package io.kontur.layers.config;

import org.mybatis.spring.boot.autoconfigure.ConfigurationCustomizer;

@org.springframework.context.annotation.Configuration
public class MyBatisConfig {

    @Bean
    public ConfigurationCustomizer typeHandlerCustomizer() {
    return configuration -> {
        // Removed — now we only use typeHandler=... in XML
    };
}
}
