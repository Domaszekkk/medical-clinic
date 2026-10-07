package com.example.medicalclinic.user;

import com.example.medicalclinic.UserJpaRepositoryPort;
import com.example.medicalclinic.UserService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class UserServiceConfig {

    @Bean
    public UserService userService(UserJpaRepositoryPort userJpaRepositoryPort) {
        return new UserService(userJpaRepositoryPort);
    }
}
