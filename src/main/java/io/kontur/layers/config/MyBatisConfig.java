package io.kontur.layers.config;

import io.kontur.layers.util.ListToJsonTypeHandler;
import org.apache.ibatis.session.Configuration;
import org.mybatis.spring.boot.autoconfigure.ConfigurationCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration as SpringConfiguration;

import java.util.List;

@Configuration
public class MyBatisConfig {

    @Bean
    public ConfigurationCustomizer typeHandlerCustomizer() {
        return configuration -> configuration
                .getTypeHandlerRegistry()
                .register(List.class, ListToJsonTypeHandler.class);
    }
}
