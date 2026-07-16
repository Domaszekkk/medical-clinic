package com.domaszekkk.medicalclinic.service;

import com.domaszekkk.medicalclinic.dto.AddPatientCommand;
import com.domaszekkk.medicalclinic.dto.PatientDto;
import com.domaszekkk.medicalclinic.dto.UpdatePatientRequest;
import com.domaszekkk.medicalclinic.entity.Patient;
import com.domaszekkk.medicalclinic.entity.User;
import com.domaszekkk.medicalclinic.exception.PatientNotFoundException;
import com.domaszekkk.medicalclinic.exception.UserNotFoundException;
import com.domaszekkk.medicalclinic.mapper.PatientMapper;
import com.domaszekkk.medicalclinic.repository.PatientJpaRepository;
import com.domaszekkk.medicalclinic.repository.UserJpaRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;
import org.mockito.Mockito;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

public class PatientServiceTest {
    private PatientJpaRepository patientJpaRepository;
    private UserJpaRepository userJpaRepository;
    private PatientMapper patientMapper;
    private PatientService patientService;

    @BeforeEach
    void setup() {
        this.patientJpaRepository = Mockito.mock(PatientJpaRepository.class);
        this.userJpaRepository = Mockito.mock(UserJpaRepository.class);
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
        assertAll(
                () -> assertEquals(2, result.getContent().size()),
                () -> assertEquals(1L, result.getContent().get(0).getId()),
                () -> assertEquals("firstName", result.getContent().get(0).getFirstName()),
                () -> assertEquals("lastName", result.getContent().get(0).getLastName()),
                () -> assertEquals("1234565789", result.getContent().get(0).getPhoneNumber()),
                () -> assertEquals(LocalDate.of(2000, 1, 1), result.getContent().get(0).getBirthday()),
                () -> assertEquals(1L, result.getContent().get(0).getUserId()),
                () -> assertEquals(2L, result.getContent().get(1).getId()),
                () -> assertEquals("firstName2", result.getContent().get(1).getFirstName()),
                () -> assertEquals("lastName2", result.getContent().get(1).getLastName()),
                () -> assertEquals("987654321", result.getContent().get(1).getPhoneNumber()),
                () -> assertEquals(LocalDate.of(2000, 2, 2), result.getContent().get(1).getBirthday()),
                () -> assertEquals(2L, result.getContent().get(1).getUserId())
        );
    }

    @Test
    void addPatient_DataCorrect_ReturnPatient() {
        //given
        User user = User.builder()
                .id(1L)
                .email("email")
                .password("pass")
                .build();

        AddPatientCommand command = new AddPatientCommand();
        command.setIdCardNo("ABC123456");
        command.setFirstName("firstName");
        command.setLastName("lastName");
        command.setPhoneNumber("123456789");
        command.setBirthday(LocalDate.of(1990, 1, 1));
        command.setUserId(1L);

        Patient savedPatient = Patient.builder()
                .id(1L)
                .idCardNo("ABC123456")
                .firstName("firstName")
                .lastName("lastName")
                .phoneNumber("123456789")
                .birthday(LocalDate.of(1990, 1, 1))
                .user(user)
                .build();

        when(userJpaRepository.findById(1L)).thenReturn(Optional.of(user));
        when(patientJpaRepository.save(any(Patient.class))).thenReturn(savedPatient);

        //when
        PatientDto result = patientService.addPatient(command);

        //then
        assertAll(
                () -> assertEquals(1L, result.getId()),
                () -> assertEquals("firstName", result.getFirstName()),
                () -> assertEquals("lastName", result.getLastName()),
                () -> assertEquals("123456789", result.getPhoneNumber()),
                () -> assertEquals(LocalDate.of(1990, 1, 1), result.getBirthday()),
                () -> assertEquals(1L, result.getUserId())
        );
    }

    @Test
    void getPatientByEmail_DataCorrect_ReturnPatient() {
        // given
        User user = User.builder()
                .id(1L)
                .email("email")
                .password("pass")
                .build();
        Patient patient = Patient.builder()
                .id(1L)
                .idCardNo("ABC123456")
                .firstName("firstName")
                .lastName("lastName")
                .phoneNumber("123456789")
                .birthday(LocalDate.of(1990, 1, 1))
                .user(user)
                .build();

        when(patientJpaRepository.findByUserEmail("email")).thenReturn(Optional.of(patient));

        // when
        PatientDto result = patientService.getPatientByEmail("email");

        // then
        assertAll(
                () -> assertEquals(1L, result.getId()),
                () -> assertEquals("firstName", result.getFirstName()),
                () -> assertEquals("lastName", result.getLastName()),
                () -> assertEquals("123456789", result.getPhoneNumber()),
                () -> assertEquals(LocalDate.of(1990, 1, 1), result.getBirthday()),
                () -> assertEquals(1L, result.getUserId())
        );
    }

    @Test
    void updatePatient_DataCorrect_ReturnUpdatedPatient() {
        // given
        User user = User.builder()
                .id(1L)
                .email("email")
                .password("pass")
                .build();
        Patient existingPatient = Patient.builder()
                .id(1L)
                .idCardNo("ABC123456")
                .firstName("firstName")
                .lastName("lastName")
                .phoneNumber("123456789")
                .birthday(LocalDate.of(1990, 1, 1))
                .user(user)
                .build();

        UpdatePatientRequest request = new UpdatePatientRequest();
        request.setIdCardNo("ZZZ999999");
        request.setFirstName("updatedFirstName");
        request.setLastName("updatedLastName");
        request.setPhoneNumber("111222333");
        request.setBirthday(LocalDate.of(1991, 2, 2));

        when(patientJpaRepository.findByUserEmail("email")).thenReturn(Optional.of(existingPatient));
        when(patientJpaRepository.save(any(Patient.class))).thenReturn(existingPatient);

        // when
        PatientDto result = patientService.updatePatient("email", request);

        // then
        assertAll(
                () -> assertEquals(1L, result.getId()),
                () -> assertEquals("updatedFirstName", result.getFirstName()),
                () -> assertEquals("updatedLastName", result.getLastName()),
                () -> assertEquals("111222333", result.getPhoneNumber()),
                () -> assertEquals(LocalDate.of(1991, 2, 2), result.getBirthday()),
                () -> assertEquals(1L, result.getUserId())
        );
    }

    @Test
    void addPatient_UserNotFound_ThrowsUserNotFoundException() {
        //given
        AddPatientCommand command = new AddPatientCommand();
        command.setIdCardNo("ABC123456");
        command.setFirstName("firstName");
        command.setLastName("lastName");
        command.setPhoneNumber("123456789");
        command.setBirthday(LocalDate.of(2000, 1 ,1));
        command.setUserId(1L);

        when(userJpaRepository.findById(1L)).thenReturn(Optional.empty());

        //when + then
        UserNotFoundException exception = Assertions.assertThrows(
                UserNotFoundException.class, () -> patientService.addPatient(command));

        assertAll(
                () -> assertEquals("User with id 1 not found", exception.getMessage()),
                () -> assertEquals(HttpStatus.NOT_FOUND, exception.getStatus())
        );
    }

    @Test
    void getPatientByEmail_PatientNotFound_ThrowsPatientNotFoundException() {
        //given
        when(patientJpaRepository.findByUserEmail("email")).thenReturn(Optional.empty());

        //when
        PatientNotFoundException exception = Assertions.assertThrows(
                PatientNotFoundException.class,() -> patientService.getPatientByEmail("email"));
        //then
        assertAll(
                () -> assertEquals("Patient with email email not found", exception.getMessage()),
                () -> assertEquals(HttpStatus.NOT_FOUND, exception.getStatus())
        );
    }

    @Test
    void updatePatient_PatientNotFound_ThrowsPatientNotFoundException() {
        // given
        UpdatePatientRequest request = new UpdatePatientRequest();
        request.setFirstName("updatedFirstName");
        when(patientJpaRepository.findByUserEmail("email")).thenReturn(Optional.empty());

        // when
        PatientNotFoundException exception = Assertions.assertThrows(
                PatientNotFoundException.class, () -> patientService.updatePatient("email", request));

        // then
        assertAll(
                () -> assertEquals("Patient with email email not found", exception.getMessage()),
                () -> assertEquals(HttpStatus.NOT_FOUND, exception.getStatus())
        );
    }

    @Test
    void updatePassword_PatientNotFound_ThrowsPatientNotFoundException() {
        // given
        when(patientJpaRepository.findByUserEmail("email")).thenReturn(Optional.empty());

        // when
        PatientNotFoundException exception = Assertions.assertThrows(
                PatientNotFoundException.class, () -> patientService.updatePassword("email", "newPassword"));

        // then
        assertAll(
                () -> assertEquals("Patient with email email not found", exception.getMessage()),
                () -> assertEquals(HttpStatus.NOT_FOUND, exception.getStatus())
        );
    }

    @Test
    void updatePassword_DataCorrect_UpdatesPassword() {
        //given
        User user = User.builder()
                .id(1L)
                .email("email")
                .password("oldPassword")
                .build();
        Patient patient = Patient.builder()
                .user(user)
                .build();
        when(patientJpaRepository.findByUserEmail("email")).thenReturn(Optional.of(patient));

        //when
        patientService.updatePassword("email", "newPassword");

        //then
        assertEquals("newPassword", patient.getUser().getPassword());
        verify(patientJpaRepository).findByUserEmail("email");
        verify(patientJpaRepository).save(patient);
        verifyNoMoreInteractions(patientJpaRepository);
    }

    @Test
    void deletePatientByEmail_DataCorrect_DeletesPatient() {
        // given
        String email = "email";

        // when
        patientService.deletePatientByEmail(email);

        // then
        verify(patientJpaRepository).deleteByUserEmail(email);
        verifyNoMoreInteractions(patientJpaRepository);
    }
}