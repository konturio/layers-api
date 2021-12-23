package io.kontur.layers;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan("io.kontur.layers.repository")
public class LayersApiApplication {

    public static void main(String[] args) {
        SpringApplication.run(LayersApiApplication.class, args);
    }

}
