package io.kontur.layers.config;

import io.kontur.layers.util.ListToJsonTypeHandler;
import org.mybatis.spring.boot.autoconfigure.ConfigurationCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class MyBatisConfig {

    @Bean
    public ConfigurationCustomizer typeHandlerCustomizer() {
        return (org.apache.ibatis.session.Configuration config) -> config
                .getTypeHandlerRegistry()
                .register(List.class, ListToJsonTypeHandler.class);
    }
}
