package com.example.demo;

import com.example.demo.auth.jwt.JwtProperties;
import org.modelmapper.ModelMapper;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
@EnableConfigurationProperties({JwtProperties.class})
public class Demo1Application {

    @Bean
    public ModelMapper modelMapper() {
        // register as a @Bean to inject
        return new ModelMapper();
    }

    public static void main(String[] args) {
        SpringApplication.run(Demo1Application.class, args);
    }

}
