package com.example.medicalclinic.visit;

import com.example.medicalclinic.DoctorJpaRepositoryPort;
import com.example.medicalclinic.FacilityJpaRepositoryPort;
import com.example.medicalclinic.PatientJpaRepositoryPort;
import com.example.medicalclinic.VisitJpaRepositoryPort;
import com.example.medicalclinic.VisitService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class VisitServiceConfig {

    @Bean
    public VisitService visitService(VisitJpaRepositoryPort visitJpaRepositoryPort,
                                     DoctorJpaRepositoryPort doctorJpaRepositoryPort,
                                     FacilityJpaRepositoryPort facilityJpaRepositoryPort,
                                     PatientJpaRepositoryPort patientJpaRepositoryPort) {
        return new VisitService(visitJpaRepositoryPort, doctorJpaRepositoryPort, facilityJpaRepositoryPort, patientJpaRepositoryPort);
    }
}
