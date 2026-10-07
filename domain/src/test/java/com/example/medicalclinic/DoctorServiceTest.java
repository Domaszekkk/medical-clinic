package com.example.medicalclinic;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

class DoctorServiceTest {

    private DoctorJpaRepositoryPort doctorJpaRepositoryPort;
    private UserJpaRepositoryPort userJpaRepositoryPort;
    private FacilityJpaRepositoryPort facilityJpaRepositoryPort;
    private DoctorService doctorService;

    @BeforeEach
    void setup() {
        this.doctorJpaRepositoryPort = Mockito.mock(DoctorJpaRepositoryPort.class);
        this.userJpaRepositoryPort = Mockito.mock(UserJpaRepositoryPort.class);
        this.facilityJpaRepositoryPort = Mockito.mock(FacilityJpaRepositoryPort.class);
        this.doctorService = new DoctorService(doctorJpaRepositoryPort, userJpaRepositoryPort, facilityJpaRepositoryPort);
    }

    @Test
    void getDoctors_DataCorrect_ReturnDoctors() {
        User user = User.builder().id(1L).email("email").password("pass").build();
        User user2 = User.builder().id(2L).email("email2").password("pass2").build();
        Doctor doctor = Doctor.builder().id(1L).firstName("firstName").lastName("lastName")
                .specialization("cardiology").user(user).build();
        Doctor doctor2 = Doctor.builder().id(2L).firstName("firstName2").lastName("lastName2")
                .specialization("neurology").user(user2).build();
        Pageable pageable = Pageable.builder().pageNumber(0).pageSize(10).build();
        Page<Doctor> doctorPage = Page.<Doctor>builder()
                .content(List.of(doctor, doctor2))
                .pageNumber(0).pageSize(10).totalElements(2).totalPages(1)
                .build();
        when(doctorJpaRepositoryPort.findAll(null, pageable)).thenReturn(doctorPage);

        Page<Doctor> result = doctorService.getDoctors(null, pageable);

        assertAll(
                () -> assertEquals(2, result.getContent().size()),
                () -> assertEquals(1L, result.getContent().get(0).getId()),
                () -> assertEquals(1L, result.getContent().get(0).getUser().getId()),
                () -> assertEquals("cardiology", result.getContent().get(0).getSpecialization()),
                () -> assertEquals(2L, result.getContent().get(1).getId()),
                () -> assertEquals("neurology", result.getContent().get(1).getSpecialization())
        );
    }

    @Test
    void addDoctor_DataCorrect_ReturnDoctor() {
        User user = User.builder().id(1L).email("email").password("pass").build();
        AddDoctorCommand command = AddDoctorCommand.builder()
                .firstName("firstName").lastName("lastName").specialization("cardiology").userId(1L).build();
        Doctor savedDoctor = Doctor.builder().id(1L).firstName("firstName").lastName("lastName")
                .specialization("cardiology").user(user).build();
        when(userJpaRepositoryPort.findById(1L)).thenReturn(Optional.of(user));
        when(doctorJpaRepositoryPort.save(any(Doctor.class))).thenReturn(savedDoctor);

        Doctor result = doctorService.addDoctor(command);

        assertAll(
                () -> assertEquals(1L, result.getId()),
                () -> assertEquals(1L, result.getUser().getId()),
                () -> assertEquals("firstName", result.getFirstName()),
                () -> assertEquals("cardiology", result.getSpecialization())
        );
    }

    @Test
    void addDoctor_UserNotFound_ThrowsUserNotFoundException() {
        AddDoctorCommand command = AddDoctorCommand.builder().firstName("firstName").userId(1L).build();
        when(userJpaRepositoryPort.findById(1L)).thenReturn(Optional.empty());

        UserNotFoundException exception = Assertions.assertThrows(
                UserNotFoundException.class, () -> doctorService.addDoctor(command));

        assertAll(
                () -> assertEquals("User with id 1 not found", exception.getMessage()),
                () -> assertEquals(404, exception.getResponseCode())
        );
        verify(doctorJpaRepositoryPort, never()).save(any());
    }

    @Test
    void getDoctorById_DataCorrect_ReturnDoctor() {
        User user = User.builder().id(1L).email("email").password("pass").build();
        Doctor doctor = Doctor.builder().id(1L).firstName("firstName").lastName("lastName")
                .specialization("cardiology").user(user).build();
        when(doctorJpaRepositoryPort.findById(1L)).thenReturn(Optional.of(doctor));

        Doctor result = doctorService.getDoctorById(1L);

        assertAll(
                () -> assertEquals(1L, result.getId()),
                () -> assertEquals(1L, result.getUser().getId()),
                () -> assertEquals("firstName", result.getFirstName()),
                () -> assertEquals("cardiology", result.getSpecialization())
        );
    }

    @Test
    void updateDoctor_DataCorrect_ReturnUpdatedDoctor() {
        User user = User.builder().id(1L).email("email").password("pass").build();
        Doctor existingDoctor = Doctor.builder().id(1L).firstName("firstName").lastName("lastName")
                .specialization("cardiology").user(user).build();
        UpdateDoctorRequest request = UpdateDoctorRequest.builder()
                .firstName("updatedFirstName").lastName("updatedLastName").specialization("neurology").build();
        when(doctorJpaRepositoryPort.findById(1L)).thenReturn(Optional.of(existingDoctor));
        when(doctorJpaRepositoryPort.save(any(Doctor.class))).thenReturn(existingDoctor);

        Doctor result = doctorService.updateDoctor(1L, request);

        assertAll(
                () -> assertEquals(1L, result.getId()),
                () -> assertEquals("updatedFirstName", result.getFirstName()),
                () -> assertEquals("updatedLastName", result.getLastName()),
                () -> assertEquals("neurology", result.getSpecialization()),
                () -> assertEquals(1L, result.getUser().getId())
        );
    }

    @Test
    void assignDoctorToFacility_DataCorrect_ReturnDoctorWithFacility() {
        Facility facility = Facility.builder().id(1L).name("facilityName").city("city")
                .zipCode("00-000").street("street").buildingNumber("1").build();
        User user = User.builder().id(1L).email("email").password("pass").build();
        Doctor doctor = Doctor.builder().id(1L).firstName("firstName").lastName("lastName")
                .specialization("cardiology").user(user).facilities(new ArrayList<>()).build();
        when(doctorJpaRepositoryPort.findById(1L)).thenReturn(Optional.of(doctor));
        when(facilityJpaRepositoryPort.findById(1L)).thenReturn(Optional.of(facility));
        when(doctorJpaRepositoryPort.save(any(Doctor.class))).thenReturn(doctor);

        Doctor result = doctorService.assignDoctorToFacility(1L, 1L);

        assertAll(
                () -> assertEquals(1L, result.getId()),
                () -> assertEquals("firstName", result.getFirstName()),
                () -> assertEquals(1, result.getFacilities().size()),
                () -> assertEquals(1L, result.getFacilities().get(0).getId()),
                () -> assertEquals("facilityName", result.getFacilities().get(0).getName())
        );
    }

    @Test
    void getDoctorById_DoctorNotFound_ThrowsDoctorNotFoundException() {
        when(doctorJpaRepositoryPort.findById(1L)).thenReturn(Optional.empty());

        DoctorNotFoundException exception = Assertions.assertThrows(
                DoctorNotFoundException.class, () -> doctorService.getDoctorById(1L));

        assertAll(
                () -> assertEquals("Doctor with id 1 not found", exception.getMessage()),
                () -> assertEquals(404, exception.getResponseCode())
        );
    }

    @Test
    void updateDoctor_DoctorNotFound_ThrowsDoctorNotFoundException() {
        UpdateDoctorRequest request = UpdateDoctorRequest.builder().firstName("updatedFirstName").build();
        when(doctorJpaRepositoryPort.findById(1L)).thenReturn(Optional.empty());

        DoctorNotFoundException exception = Assertions.assertThrows(
                DoctorNotFoundException.class, () -> doctorService.updateDoctor(1L, request));

        assertAll(
                () -> assertEquals("Doctor with id 1 not found", exception.getMessage()),
                () -> assertEquals(404, exception.getResponseCode())
        );
    }

    @Test
    void assignDoctorToFacility_DoctorNotFound_ThrowsDoctorNotFoundException() {
        when(doctorJpaRepositoryPort.findById(1L)).thenReturn(Optional.empty());

        DoctorNotFoundException exception = Assertions.assertThrows(
                DoctorNotFoundException.class, () -> doctorService.assignDoctorToFacility(1L, 1L));

        assertAll(
                () -> assertEquals("Doctor with id 1 not found", exception.getMessage()),
                () -> assertEquals(404, exception.getResponseCode())
        );
    }

    @Test
    void assignDoctorToFacility_FacilityNotFound_ThrowsFacilityNotFoundException() {
        Doctor doctor = Doctor.builder().id(1L).facilities(new ArrayList<>()).build();
        when(doctorJpaRepositoryPort.findById(1L)).thenReturn(Optional.of(doctor));
        when(facilityJpaRepositoryPort.findById(1L)).thenReturn(Optional.empty());

        FacilityNotFoundException exception = Assertions.assertThrows(
                FacilityNotFoundException.class, () -> doctorService.assignDoctorToFacility(1L, 1L));

        assertAll(
                () -> assertEquals("Facility with id 1 not found", exception.getMessage()),
                () -> assertEquals(404, exception.getResponseCode())
        );
    }

    @Test
    void assignDoctorToFacility_AlreadyAssigned_ThrowsDoctorAlreadyAssignedToFacilityException() {
        Facility facility = Facility.builder().id(1L).build();
        Doctor doctor = Doctor.builder().id(1L).facilities(new ArrayList<>(List.of(facility))).build();
        when(doctorJpaRepositoryPort.findById(1L)).thenReturn(Optional.of(doctor));
        when(facilityJpaRepositoryPort.findById(1L)).thenReturn(Optional.of(facility));

        DoctorAlreadyAssignedToFacilityException exception = Assertions.assertThrows(
                DoctorAlreadyAssignedToFacilityException.class, () -> doctorService.assignDoctorToFacility(1L, 1L));

        assertAll(
                () -> assertEquals("Doctor with id 1 is already assigned to facility with id 1", exception.getMessage()),
                () -> assertEquals(409, exception.getResponseCode())
        );
    }

    @Test
    void deleteDoctor_DataCorrect_DeletesDoctor() {
        when(doctorJpaRepositoryPort.existsById(1L)).thenReturn(true);

        doctorService.deleteDoctor(1L);

        verify(doctorJpaRepositoryPort).existsById(1L);
        verify(doctorJpaRepositoryPort).deleteById(1L);
        verifyNoMoreInteractions(doctorJpaRepositoryPort);
    }

    @Test
    void deleteDoctor_DoctorNotFound_ThrowsDoctorNotFoundException() {
        when(doctorJpaRepositoryPort.existsById(1L)).thenReturn(false);

        DoctorNotFoundException exception = Assertions.assertThrows(
                DoctorNotFoundException.class, () -> doctorService.deleteDoctor(1L));

        assertAll(
                () -> assertEquals("Doctor with id 1 not found", exception.getMessage()),
                () -> assertEquals(404, exception.getResponseCode())
        );
        verify(doctorJpaRepositoryPort, never()).deleteById(any());
    }
}
