package com.example.medicalclinic.doctor;

import com.example.medicalclinic.DoctorJpaRepositoryPort;
import com.example.medicalclinic.DoctorService;
import com.example.medicalclinic.FacilityJpaRepositoryPort;
import com.example.medicalclinic.UserJpaRepositoryPort;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class DoctorServiceConfig {

    @Bean
    public DoctorService doctorService(DoctorJpaRepositoryPort doctorJpaRepositoryPort,
                                       UserJpaRepositoryPort userJpaRepositoryPort,
                                       FacilityJpaRepositoryPort facilityJpaRepositoryPort) {
        return new DoctorService(doctorJpaRepositoryPort, userJpaRepositoryPort, facilityJpaRepositoryPort);
    }
}
