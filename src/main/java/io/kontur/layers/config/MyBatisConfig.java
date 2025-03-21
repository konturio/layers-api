package io.kontur.layers.config;

import io.kontur.layers.util.ListToJsonTypeHandler;
import org.apache.ibatis.type.TypeHandlerRegistry;
import org.mybatis.spring.boot.autoconfigure.ConfigurationCustomizer;
import org.springframework.context.annotation.Bean;

import java.util.List;

@org.springframework.context.annotation.Configuration
public class MyBatisConfig {

    @Bean
    public ConfigurationCustomizer typeHandlerCustomizer() {
        return (org.apache.ibatis.session.Configuration config) -> {
            TypeHandlerRegistry registry = config.getTypeHandlerRegistry();
            registry.register(ListToJsonTypeHandler.class);
        };
    }
}
