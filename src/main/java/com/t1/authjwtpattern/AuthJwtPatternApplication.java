package com.t1.authjwtpattern;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.servlet.UserDetailsServiceAutoConfiguration;

@SpringBootApplication(
        exclude = {
                UserDetailsServiceAutoConfiguration.class
        }
)
public class AuthJwtPatternApplication {

    public static void main(String[] args) {
        SpringApplication.run(AuthJwtPatternApplication.class, args);
    }

}
