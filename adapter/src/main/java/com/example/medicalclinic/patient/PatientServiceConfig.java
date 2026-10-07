package com.example.medicalclinic.patient;

import com.example.medicalclinic.PatientJpaRepositoryPort;
import com.example.medicalclinic.PatientService;
import com.example.medicalclinic.UserJpaRepositoryPort;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class PatientServiceConfig {

    @Bean
    public PatientService patientService(PatientJpaRepositoryPort patientJpaRepositoryPort,
                                         UserJpaRepositoryPort userJpaRepositoryPort) {
        return new PatientService(patientJpaRepositoryPort, userJpaRepositoryPort);
    }
}
