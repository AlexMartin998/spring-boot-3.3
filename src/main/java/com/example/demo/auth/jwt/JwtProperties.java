package com.example.demo.auth.jwt;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;


@Data // lombok to generate getters and setters
@ConfigurationProperties(prefix = "app.security.jwt")  // enable in MainClass @EnableConfigurationProperties <- Injectable
public class JwtProperties {

    // read from application.properties
    private String secret;
    private Long expiration; // in hours

}
