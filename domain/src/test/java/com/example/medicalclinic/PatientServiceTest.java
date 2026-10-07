package com.example.medicalclinic;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

class PatientServiceTest {

    private PatientJpaRepositoryPort patientJpaRepositoryPort;
    private UserJpaRepositoryPort userJpaRepositoryPort;
    private PatientService patientService;

    @BeforeEach
    void setup() {
        this.patientJpaRepositoryPort = Mockito.mock(PatientJpaRepositoryPort.class);
        this.userJpaRepositoryPort = Mockito.mock(UserJpaRepositoryPort.class);
        this.patientService = new PatientService(patientJpaRepositoryPort, userJpaRepositoryPort);
    }

    @Test
    void getAllPatients_DataCorrect_ReturnPatients() {
        User user = User.builder().id(1L).email("email").password("password").build();
        Patient patient = Patient.builder().id(1L).idCardNo("ABC123456").firstName("firstName")
                .lastName("lastName").phoneNumber("1234565789").birthday(LocalDate.of(2000, 1, 1)).user(user).build();
        User user2 = User.builder().id(2L).email("email2").password("pass2").build();
        Patient patient2 = Patient.builder().id(2L).idCardNo("DEF1234567").firstName("firstName2")
                .lastName("lastName2").phoneNumber("987654321").birthday(LocalDate.of(2000, 2, 2)).user(user2).build();
        Pageable pageable = Pageable.builder().pageNumber(0).pageSize(10).build();
        Page<Patient> patientPage = Page.<Patient>builder()
                .content(List.of(patient, patient2))
                .pageNumber(0).pageSize(10).totalElements(2).totalPages(1)
                .build();
        when(patientJpaRepositoryPort.findAll(pageable)).thenReturn(patientPage);

        Page<Patient> result = patientService.getAllPatients(pageable);

        assertAll(
                () -> assertEquals(2, result.getContent().size()),
                () -> assertEquals(1L, result.getContent().get(0).getId()),
                () -> assertEquals("firstName", result.getContent().get(0).getFirstName()),
                () -> assertEquals(1L, result.getContent().get(0).getUser().getId()),
                () -> assertEquals(2L, result.getContent().get(1).getId()),
                () -> assertEquals("firstName2", result.getContent().get(1).getFirstName()),
                () -> assertEquals(2L, result.getContent().get(1).getUser().getId())
        );
    }

    @Test
    void addPatient_DataCorrect_ReturnPatient() {
        User user = User.builder().id(1L).email("email").password("pass").build();
        AddPatientCommand command = AddPatientCommand.builder()
                .idCardNo("ABC123456").firstName("firstName").lastName("lastName")
                .phoneNumber("123456789").birthday(LocalDate.of(1990, 1, 1)).userId(1L).build();
        Patient savedPatient = Patient.builder().id(1L).idCardNo("ABC123456").firstName("firstName")
                .lastName("lastName").phoneNumber("123456789").birthday(LocalDate.of(1990, 1, 1)).user(user).build();
        when(userJpaRepositoryPort.findById(1L)).thenReturn(Optional.of(user));
        when(patientJpaRepositoryPort.save(any(Patient.class))).thenReturn(savedPatient);

        Patient result = patientService.addPatient(command);

        assertAll(
                () -> assertEquals(1L, result.getId()),
                () -> assertEquals("firstName", result.getFirstName()),
                () -> assertEquals("123456789", result.getPhoneNumber()),
                () -> assertEquals(LocalDate.of(1990, 1, 1), result.getBirthday()),
                () -> assertEquals(1L, result.getUser().getId())
        );
    }

    @Test
    void getPatientByEmail_DataCorrect_ReturnPatient() {
        User user = User.builder().id(1L).email("email").password("pass").build();
        Patient patient = Patient.builder().id(1L).idCardNo("ABC123456").firstName("firstName")
                .lastName("lastName").phoneNumber("123456789").birthday(LocalDate.of(1990, 1, 1)).user(user).build();
        when(patientJpaRepositoryPort.findByUserEmail("email")).thenReturn(Optional.of(patient));

        Patient result = patientService.getPatientByEmail("email");

        assertAll(
                () -> assertEquals(1L, result.getId()),
                () -> assertEquals("firstName", result.getFirstName()),
                () -> assertEquals(1L, result.getUser().getId())
        );
    }

    @Test
    void updatePatient_DataCorrect_ReturnUpdatedPatient() {
        User user = User.builder().id(1L).email("email").password("pass").build();
        Patient existingPatient = Patient.builder().id(1L).idCardNo("ABC123456").firstName("firstName")
                .lastName("lastName").phoneNumber("123456789").birthday(LocalDate.of(1990, 1, 1)).user(user).build();
        UpdatePatientRequest request = UpdatePatientRequest.builder()
                .idCardNo("ZZZ999999").firstName("updatedFirstName").lastName("updatedLastName")
                .phoneNumber("111222333").birthday(LocalDate.of(1991, 2, 2)).build();
        when(patientJpaRepositoryPort.findByUserEmail("email")).thenReturn(Optional.of(existingPatient));
        when(patientJpaRepositoryPort.save(any(Patient.class))).thenReturn(existingPatient);

        Patient result = patientService.updatePatient("email", request);

        assertAll(
                () -> assertEquals(1L, result.getId()),
                () -> assertEquals("updatedFirstName", result.getFirstName()),
                () -> assertEquals("updatedLastName", result.getLastName()),
                () -> assertEquals("111222333", result.getPhoneNumber()),
                () -> assertEquals(LocalDate.of(1991, 2, 2), result.getBirthday()),
                () -> assertEquals(1L, result.getUser().getId())
        );
    }

    @Test
    void addPatient_UserNotFound_ThrowsUserNotFoundException() {
        AddPatientCommand command = AddPatientCommand.builder()
                .idCardNo("ABC123456").firstName("firstName").userId(1L).build();
        when(userJpaRepositoryPort.findById(1L)).thenReturn(Optional.empty());

        UserNotFoundException exception = Assertions.assertThrows(
                UserNotFoundException.class, () -> patientService.addPatient(command));

        assertAll(
                () -> assertEquals("User with id 1 not found", exception.getMessage()),
                () -> assertEquals(404, exception.getResponseCode())
        );
    }

    @Test
    void getPatientByEmail_PatientNotFound_ThrowsPatientNotFoundException() {
        when(patientJpaRepositoryPort.findByUserEmail("email")).thenReturn(Optional.empty());

        PatientNotFoundException exception = Assertions.assertThrows(
                PatientNotFoundException.class, () -> patientService.getPatientByEmail("email"));

        assertAll(
                () -> assertEquals("Patient with email email not found", exception.getMessage()),
                () -> assertEquals(404, exception.getResponseCode())
        );
    }

    @Test
    void updatePatient_PatientNotFound_ThrowsPatientNotFoundException() {
        UpdatePatientRequest request = UpdatePatientRequest.builder().firstName("updatedFirstName").build();
        when(patientJpaRepositoryPort.findByUserEmail("email")).thenReturn(Optional.empty());

        PatientNotFoundException exception = Assertions.assertThrows(
                PatientNotFoundException.class, () -> patientService.updatePatient("email", request));

        assertAll(
                () -> assertEquals("Patient with email email not found", exception.getMessage()),
                () -> assertEquals(404, exception.getResponseCode())
        );
    }

    @Test
    void updatePassword_PatientNotFound_ThrowsPatientNotFoundException() {
        when(patientJpaRepositoryPort.findByUserEmail("email")).thenReturn(Optional.empty());

        PatientNotFoundException exception = Assertions.assertThrows(
                PatientNotFoundException.class, () -> patientService.updatePassword("email", "newPassword"));

        assertAll(
                () -> assertEquals("Patient with email email not found", exception.getMessage()),
                () -> assertEquals(404, exception.getResponseCode())
        );
    }

    @Test
    void updatePassword_DataCorrect_UpdatesPassword() {
        User user = User.builder().id(1L).email("email").password("oldPassword").build();
        Patient patient = Patient.builder().user(user).build();
        when(patientJpaRepositoryPort.findByUserEmail("email")).thenReturn(Optional.of(patient));

        patientService.updatePassword("email", "newPassword");

        assertEquals("newPassword", patient.getUser().getPassword());
        verify(patientJpaRepositoryPort).findByUserEmail("email");
        verify(patientJpaRepositoryPort).save(patient);
        verifyNoMoreInteractions(patientJpaRepositoryPort);
    }

    @Test
    void deletePatientByEmail_DataCorrect_DeletesPatient() {
        patientService.deletePatientByEmail("email");

        verify(patientJpaRepositoryPort).deleteByUserEmail("email");
        verifyNoMoreInteractions(patientJpaRepositoryPort);
    }
}
