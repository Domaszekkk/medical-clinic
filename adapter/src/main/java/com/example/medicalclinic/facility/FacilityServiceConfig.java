package com.example.medicalclinic.facility;

import com.example.medicalclinic.FacilityJpaRepositoryPort;
import com.example.medicalclinic.FacilityService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class FacilityServiceConfig {

    @Bean
    public FacilityService facilityService(FacilityJpaRepositoryPort facilityJpaRepositoryPort) {
        return new FacilityService(facilityJpaRepositoryPort);
    }
}
