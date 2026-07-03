package com.domaszekkk.medicalclinic.service;

import com.domaszekkk.medicalclinic.dto.PatientDto;
import com.domaszekkk.medicalclinic.entity.Patient;
import com.domaszekkk.medicalclinic.entity.User;
import com.domaszekkk.medicalclinic.mapper.PatientMapper;
import com.domaszekkk.medicalclinic.repository.PatientJpaRepository;
import com.domaszekkk.medicalclinic.repository.UserJpaRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;
import org.mockito.Mockito;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;
import java.util.List;

import static org.mockito.Mockito.when;

public class PatientServiceTest {
    private PatientJpaRepository patientJpaRepository;
    private UserJpaRepository userJpaRepository;
    private PatientMapper patientMapper;
    private PatientService patientService;

    @BeforeEach
    void setup() {
        this.patientJpaRepository = Mockito.mock(PatientJpaRepository.class);
        this.userJpaRepository = Mockito.mock(UserJpaRepository.class)
        this.patientMapper = Mappers.getMapper(PatientMapper.class);
        this.patientService = new PatientService(patientJpaRepository, patientMapper, userJpaRepository);
    }

//    nazwaMetodyKtoraTestuje_StanKtóryTestuje_CoPowinnoSieStac

    @Test
    void getAllPatient_DataCorrect_ReturnPatients() {
        //given - sekcja podczas której tworzę wszytskie dane potrzebne do
        //przeprowadzenia testu oraz ustalam co mają zwrócić mocki - przygotowanie do wykonania testu

        User user = User.builder()
                .id(1L)
                .email("email")
                .password("password")
                .build();
        Patient patient = Patient.builder()
                .id(1L)
                .idCardNo("ABC123456")
                .firstName("firstName")
                .lastName("lastName")
                .phoneNumber("1234565789")
                .birthday(LocalDate.of(2000, 1, 1))
                .user(user)
                .build();
        User user2 = User.builder()
                .id(2L)
                .email("email2")
                .password("pass2")
                .build();
        Patient patient2 = Patient.builder()
                .id(2L)
                .idCardNo("DEF1234567")
                .firstName("firstName2")
                .lastName("lastName2")
                .phoneNumber("987654321")
                .birthday(LocalDate.of(2000, 2, 2))
                .user(user2)
                .build();

        List<Patient> patients = List.of(patient, patient2);
        Pageable pageable = PageRequest.of(0, 10);
        Page<Patient> patientPage = new PageImpl<>(patients, pageable, patients.size());
        when(patientJpaRepository.findAll(pageable)).thenReturn(patientPage);

        //when - sekcja w której wykonuje mój test -> bedzie tutaj po prostu
        //wywołanie metody która testuje w tym przypadku GetAllPatients
        Page<PatientDto> result = patientService.getAllPatients(pageable);

        //then - sekcja która służy sprawdzeniu czy rezultat wykonania testu
    }
}