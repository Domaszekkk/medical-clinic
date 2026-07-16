package com.domaszekkk.medicalclinic.service;

import com.domaszekkk.medicalclinic.dto.AddDoctorCommand;
import com.domaszekkk.medicalclinic.dto.DoctorDto;
import com.domaszekkk.medicalclinic.dto.UpdateDoctorRequest;
import com.domaszekkk.medicalclinic.entity.Doctor;
import com.domaszekkk.medicalclinic.entity.Facility;
import com.domaszekkk.medicalclinic.entity.User;
import com.domaszekkk.medicalclinic.exception.DoctorAlreadyAssignedToFacilityException;
import com.domaszekkk.medicalclinic.exception.DoctorNotFoundException;
import com.domaszekkk.medicalclinic.exception.FacilityNotFoundException;
import com.domaszekkk.medicalclinic.exception.UserNotFoundException;
import com.domaszekkk.medicalclinic.mapper.DoctorMapper;
import com.domaszekkk.medicalclinic.mapper.FacilityMapper;
import com.domaszekkk.medicalclinic.repository.DoctorJpaRepository;
import com.domaszekkk.medicalclinic.repository.FacilityJpaRepository;
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
import org.springframework.test.util.ReflectionTestUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class DoctorServiceTest {

    private DoctorJpaRepository doctorJpaRepository;
    private DoctorMapper doctorMapper;
    private FacilityJpaRepository facilityJpaRepository;
    private UserJpaRepository userJpaRepository;
    private DoctorService doctorService;

    @BeforeEach
    void setup() {
        this.doctorJpaRepository = Mockito.mock(DoctorJpaRepository.class);
        this.facilityJpaRepository = Mockito.mock(FacilityJpaRepository.class);
        this.userJpaRepository = Mockito.mock(UserJpaRepository.class);
        FacilityMapper facilityMapper = Mappers.getMapper(FacilityMapper.class);
        this.doctorMapper = Mappers.getMapper(DoctorMapper.class);
        ReflectionTestUtils.setField(doctorMapper, "facilityMapper", facilityMapper);
        this.doctorService = new DoctorService(doctorJpaRepository, doctorMapper, facilityJpaRepository, userJpaRepository);
    }

    @Test
    void getAllDoctors_DataCorrect_ReturnDoctors() {
        // given
        User user = User.builder()
                .id(1L)
                .email("email")
                .password("pass")
                .build();

        User user2 = User.builder()
                .id(2L)
                .email("email2")
                .password("pass2")
                .build();

        Doctor doctor = Doctor.builder()
                .id(1L)
                .firstName("firstName")
                .lastName("lastName")
                .specialization("cardiology")
                .user(user)
                .build();

        Doctor doctor2 = Doctor.builder()
                .id(2L)
                .firstName("firstName2")
                .lastName("lastName2")
                .specialization("neurology")
                .user(user2)
                .build();

        List<Doctor> doctors = List.of(doctor, doctor2);
        Pageable pageable = PageRequest.of(0, 10);
        Page<Doctor> doctorPage = new PageImpl<>(doctors, pageable, doctors.size());
        when(doctorJpaRepository.findAll(pageable)).thenReturn(doctorPage);

        // when
        Page<DoctorDto> result = doctorService.getAllDoctors(pageable);

        // then
        assertAll(
                () -> assertEquals(2, result.getContent().size()),
                () -> assertEquals(1L, result.getContent().get(0).getId()),
                () -> assertEquals(1L, result.getContent().get(0).getUserId()),
                () -> assertEquals("firstName", result.getContent().get(0).getFirstName()),
                () -> assertEquals("lastName", result.getContent().get(0).getLastName()),
                () -> assertEquals("cardiology", result.getContent().get(0).getSpecialization()),
                () -> assertEquals(2L, result.getContent().get(1).getId()),
                () -> assertEquals(2L, result.getContent().get(1).getUserId()),
                () -> assertEquals("firstName2", result.getContent().get(1).getFirstName()),
                () -> assertEquals("lastName2", result.getContent().get(1).getLastName()),
                () -> assertEquals("neurology", result.getContent().get(1).getSpecialization())
        );
    }

    @Test
    void addDoctor_DataCorrect_ReturnDoctor() {
        // given
        User user = User.builder()
                .id(1L)
                .email("email")
                .password("pass")
                .build();

        AddDoctorCommand command = new AddDoctorCommand();
        command.setFirstName("firstName");
        command.setLastName("lastName");
        command.setSpecialization("cardiology");
        command.setUserId(1L);

        Doctor savedDoctor = Doctor.builder()
                .id(1L)
                .firstName("firstName")
                .lastName("lastName")
                .specialization("cardiology")
                .user(user)
                .build();

        when(userJpaRepository.findById(1L)).thenReturn(Optional.of(user));
        when(doctorJpaRepository.save(any(Doctor.class))).thenReturn(savedDoctor);

        // when
        DoctorDto result = doctorService.addDoctor(command);

        // then
        assertAll(
                () -> assertEquals(1L, result.getId()),
                () -> assertEquals(1L, result.getUserId()),
                () -> assertEquals("firstName", result.getFirstName()),
                () -> assertEquals("lastName", result.getLastName()),
                () -> assertEquals("cardiology", result.getSpecialization())
        );
    }

    @Test
    void addDoctor_UserNotFound_ThrowsUserNotFoundException() {
        // given
        AddDoctorCommand command = new AddDoctorCommand();
        command.setFirstName("firstName");
        command.setUserId(1L);
        when(userJpaRepository.findById(1L)).thenReturn(Optional.empty());

        // when + then
        UserNotFoundException exception = Assertions.assertThrows(
                UserNotFoundException.class, () -> doctorService.addDoctor(command));

        assertAll(
                () -> assertEquals("User with id 1 not found", exception.getMessage()),
                () -> assertEquals(HttpStatus.NOT_FOUND, exception.getStatus())
        );
        verify(doctorJpaRepository, never()).save(any());
    }

    @Test
    void getDoctorById_DataCorrect_ReturnDoctor() {
        // given
        User user = User.builder()
                .id(1L)
                .email("email")
                .password("pass")
                .build();

        Doctor doctor = Doctor.builder()
                .id(1L)
                .firstName("firstName")
                .lastName("lastName")
                .specialization("cardiology")
                .user(user)
                .build();

        when(doctorJpaRepository.findById(1L)).thenReturn(Optional.of(doctor));

        // when
        DoctorDto result = doctorService.getDoctorById(1L);

        // then
        assertAll(
                () -> assertEquals(1L, result.getId()),
                () -> assertEquals(1L, result.getUserId()),
                () -> assertEquals("firstName", result.getFirstName()),
                () -> assertEquals("lastName", result.getLastName()),
                () -> assertEquals("cardiology", result.getSpecialization())
        );
    }

    @Test
    void updateDoctor_DataCorrect_ReturnUpdatedDoctor() {
        // given
        User user = User.builder()
                .id(1L)
                .email("email")
                .password("pass")
                .build();

        Doctor existingDoctor = Doctor.builder()
                .id(1L)
                .firstName("firstName")
                .lastName("lastName")
                .specialization("cardiology")
                .user(user)
                .build();

        UpdateDoctorRequest request = new UpdateDoctorRequest();
        request.setFirstName("updatedFirstName");
        request.setLastName("updatedLastName");
        request.setSpecialization("neurology");

        when(doctorJpaRepository.findById(1L)).thenReturn(Optional.of(existingDoctor));
        when(doctorJpaRepository.save(any(Doctor.class))).thenReturn(existingDoctor);

        // when
        DoctorDto result = doctorService.updateDoctor(1L, request);

        // then
        assertAll(
                () -> assertEquals(1L, result.getId()),
                () -> assertEquals("updatedFirstName", result.getFirstName()),
                () -> assertEquals("updatedLastName", result.getLastName()),
                () -> assertEquals("neurology", result.getSpecialization()),
                () -> assertEquals(1L, existingDoctor.getUser().getId())
        );
    }

    @Test
    void assignDoctorToFacility_DataCorrect_ReturnDoctorWithFacility() {
        // given
        Facility facility = Facility.builder()
                .id(1L)
                .name("facilityName")
                .city("city")
                .zipCode("00-000")
                .street("street")
                .buildingNumber("1")
                .build();

        User user = User.builder()
                .id(1L)
                .email("email")
                .password("pass")
                .build();

        Doctor doctor = Doctor.builder()
                .id(1L)
                .firstName("firstName")
                .lastName("lastName")
                .specialization("cardiology")
                .user(user)
                .facilities(new ArrayList<>())
                .build();

        when(doctorJpaRepository.findById(1L)).thenReturn(Optional.of(doctor));
        when(facilityJpaRepository.findById(1L)).thenReturn(Optional.of(facility));
        when(doctorJpaRepository.save(any(Doctor.class))).thenReturn(doctor);

        // when
        DoctorDto result = doctorService.assignDoctorToFacility(1L, 1L);

        // then
        assertAll(
                () -> assertEquals(1L, result.getId()),
                () -> assertEquals("firstName", result.getFirstName()),
                () -> assertEquals("lastName", result.getLastName()),
                () -> assertEquals("cardiology", result.getSpecialization()),
                () -> assertEquals(1, result.getFacilities().size()),
                () -> assertEquals(1L, result.getFacilities().get(0).getId()),
                () -> assertEquals("facilityName", result.getFacilities().get(0).getName())
        );
    }

    @Test
    void getDoctorById_DoctorNotFound_ThrowsDoctorNotFoundException() {
        // given
        when(doctorJpaRepository.findById(1L)).thenReturn(Optional.empty());

        // when
        DoctorNotFoundException exception = Assertions.assertThrows(
                DoctorNotFoundException.class, () -> doctorService.getDoctorById(1L));
        // then
        assertAll(
                () -> assertEquals("Doctor with id 1 not found", exception.getMessage()),
                () -> assertEquals(HttpStatus.NOT_FOUND, exception.getStatus())
        );
    }

    @Test
    void updateDoctor_DoctorNotFound_ThrowsDoctorNotFoundException() {
        // given
        UpdateDoctorRequest request = new UpdateDoctorRequest();
        request.setFirstName("updatedFirstName");
        when(doctorJpaRepository.findById(1L)).thenReturn(Optional.empty());

        // when
        DoctorNotFoundException exception = Assertions.assertThrows(
                DoctorNotFoundException.class, () -> doctorService.updateDoctor(1L, request));

        // then
        assertAll(
                () -> assertEquals("Doctor with id 1 not found", exception.getMessage()),
                () -> assertEquals(HttpStatus.NOT_FOUND, exception.getStatus())
        );
    }

    @Test
    void assignDoctorToFacility_DoctorNotFound_ThrowsDoctorNotFoundException() {
        // given
        when(doctorJpaRepository.findById(1L)).thenReturn(Optional.empty());

        // when
        DoctorNotFoundException exception = Assertions.assertThrows(
                DoctorNotFoundException.class, () -> doctorService.assignDoctorToFacility(1L, 1L));

        // then
        assertAll(
                () -> assertEquals("Doctor with id 1 not found", exception.getMessage()),
                () -> assertEquals(HttpStatus.NOT_FOUND, exception.getStatus())
        );
    }

    @Test
    void assignDoctorToFacility_FacilityNotFound_ThrowsFacilityNotFoundException() {
        // given
        Doctor doctor = Doctor.builder()
                .id(1L)
                .facilities(new ArrayList<>())
                .build();

        when(doctorJpaRepository.findById(1L)).thenReturn(Optional.of(doctor));
        when(facilityJpaRepository.findById(1L)).thenReturn(Optional.empty());

        // when
        FacilityNotFoundException exception = Assertions.assertThrows(
                FacilityNotFoundException.class, () -> doctorService.assignDoctorToFacility(1L, 1L));

        // then
        assertAll(
                () -> assertEquals("Facility with id 1 not found", exception.getMessage()),
                () -> assertEquals(HttpStatus.NOT_FOUND, exception.getStatus())
        );
    }

    @Test
    void assignDoctorToFacility_AlreadyAssigned_ThrowsDoctorAlreadyAssignedToFacilityException() {
        // given
        Facility facility = Facility.builder()
                .id(1L)
                .build();

        Doctor doctor = Doctor.builder()
                .id(1L)
                .facilities(new ArrayList<>(List.of(facility)))
                .build();

        when(doctorJpaRepository.findById(1L)).thenReturn(Optional.of(doctor));
        when(facilityJpaRepository.findById(1L)).thenReturn(Optional.of(facility));

        // when
        DoctorAlreadyAssignedToFacilityException exception = Assertions.assertThrows(
                DoctorAlreadyAssignedToFacilityException.class, () -> doctorService.assignDoctorToFacility(1L, 1L));

        // then
        assertAll(
                () -> assertEquals("Doctor with id 1 is already assigned to facility with id 1", exception.getMessage()),
                () -> assertEquals(HttpStatus.CONFLICT, exception.getStatus())
        );
    }

    @Test
    void deleteDoctor_DataCorrect_DeletesDoctor() {
        // given
        Long id = 1L;
        when(doctorJpaRepository.existsById(id)).thenReturn(true);

        // when
        doctorService.deleteDoctor(id);

        // then
        verify(doctorJpaRepository).existsById(id);
        verify(doctorJpaRepository).deleteById(id);
        verifyNoMoreInteractions(doctorJpaRepository);
    }

    @Test
    void deleteDoctor_DoctorNotFound_ThrowsDoctorNotFoundException() {
        // given
        Long id = 1L;
        when(doctorJpaRepository.existsById(id)).thenReturn(false);

        // when + then
        DoctorNotFoundException exception = Assertions.assertThrows(
                DoctorNotFoundException.class, () -> doctorService.deleteDoctor(id));

        assertAll(
                () -> assertEquals("Doctor with id 1 not found", exception.getMessage()),
                () -> assertEquals(HttpStatus.NOT_FOUND, exception.getStatus())
        );
        verify(doctorJpaRepository, never()).deleteById(any());
    }
}