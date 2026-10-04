package com.example.medicalclinic;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class FacilityServiceConfig {

    @Bean
    public FacilityService facilityService(FacilityJpaRepositoryPort facilityJpaRepositoryPort) {
        return new FacilityService(facilityJpaRepositoryPort);
    }
}
