package io.kontur.layers.config;

import org.mybatis.spring.boot.autoconfigure.ConfigurationCustomizer;
import org.springframework.context.annotation.Bean;

@org.springframework.context.annotation.Configuration
public class MyBatisConfig {

    @Bean
    public ConfigurationCustomizer typeHandlerCustomizer() {
    return configuration -> {
        // Удалено — теперь используем только typeHandler=... в XML
    };
}


}
