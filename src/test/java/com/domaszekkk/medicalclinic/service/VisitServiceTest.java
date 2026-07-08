package com.domaszekkk.medicalclinic.service;

import com.domaszekkk.medicalclinic.dto.AddVisitCommand;
import com.domaszekkk.medicalclinic.dto.VisitDto;
import com.domaszekkk.medicalclinic.entity.Doctor;
import com.domaszekkk.medicalclinic.entity.Facility;
import com.domaszekkk.medicalclinic.entity.Patient;
import com.domaszekkk.medicalclinic.entity.Visit;
import com.domaszekkk.medicalclinic.mapper.VisitMapper;
import com.domaszekkk.medicalclinic.repository.DoctorJpaRepository;
import com.domaszekkk.medicalclinic.repository.FacilityJpaRepository;
import com.domaszekkk.medicalclinic.repository.PatientJpaRepository;
import com.domaszekkk.medicalclinic.repository.VisitJpaRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;
import org.mockito.Mockito;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

public class VisitServiceTest {

    private VisitJpaRepository visitJpaRepository;
    private DoctorJpaRepository doctorJpaRepository;
    private FacilityJpaRepository facilityJpaRepository;
    private PatientJpaRepository patientJpaRepository;
    private VisitMapper visitMapper;
    private VisitService visitService;

    @BeforeEach
    void setup() {
        this.visitJpaRepository = Mockito.mock(VisitJpaRepository.class);
        this.doctorJpaRepository = Mockito.mock(DoctorJpaRepository.class);
        this.facilityJpaRepository = Mockito.mock(FacilityJpaRepository.class);
        this.patientJpaRepository = Mockito.mock(PatientJpaRepository.class);
        this.visitMapper = Mappers.getMapper(VisitMapper.class);
        this.visitService = new VisitService(visitJpaRepository, doctorJpaRepository, facilityJpaRepository, patientJpaRepository, visitMapper);
    }

    @Test
    void addVisit_DataCorrect_ReturnVisit() {
        // given
        Facility facility = Facility.builder()
                .id(1L)
                .name("facilityName")
                .city("city")
                .zipCode("00-000")
                .street("street")
                .buildingNumber("1")
                .build();

        Doctor doctor = Doctor.builder()
                .id(1L)
                .email("email")
                .password("pass")
                .firstName("firstName")
                .lastName("lastName")
                .specialization("cardiology")
                .facilities(new ArrayList<>(List.of(facility)))
                .build();

        LocalDateTime start = LocalDateTime.now().plusDays(1).withHour(10).withMinute(0).withSecond(0).withNano(0);
        LocalDateTime end = start.plusMinutes(15);

        AddVisitCommand command = new AddVisitCommand();
        command.setStartDateTime(start);
        command.setEndDateTime(end);
        command.setFacilityId(1L);

        Visit savedVisit = Visit.builder()
                .id(1L)
                .startDateTime(start)
                .endDateTime(end)
                .doctor(doctor)
                .facility(facility)
                .build();

        when(doctorJpaRepository.findById(1L)).thenReturn(Optional.of(doctor));
        when(facilityJpaRepository.findById(1L)).thenReturn(Optional.of(facility));
        when(visitJpaRepository.findConflictingVisits(1L, start, end)).thenReturn(Collections.emptyList());
        when(visitJpaRepository.save(any(Visit.class))).thenReturn(savedVisit);

        // when
        VisitDto result = visitService.addVisit(1L, command);

        // then
        assertAll(
                () -> assertEquals(1L, result.getId()),
                () -> assertEquals(start, result.getStartDateTime()),
                () -> assertEquals(end, result.getEndDateTime()),
                () -> assertEquals(1L, result.getDoctorId()),
                () -> assertEquals(1L, result.getFacilityId()),
                () -> assertNull(result.getPatientId())
        );
    }

    @Test
    void registerPatientForVisit_DataCorrect_ReturnVisit() {
        // given
        Facility facility = Facility.builder()
                .id(1L)
                .name("facilityName")
                .city("city")
                .zipCode("00-000")
                .street("street")
                .buildingNumber("1")
                .build();

        Doctor doctor = Doctor.builder()
                .id(1L)
                .email("email")
                .password("pass")
                .firstName("firstName")
                .lastName("lastName")
                .specialization("cardiology")
                .build();

        LocalDateTime start = LocalDateTime.now().plusDays(1).withHour(10).withMinute(0).withSecond(0).withNano(0);
        LocalDateTime end = start.plusMinutes(15);

        Visit visit = Visit.builder()
                .id(1L)
                .startDateTime(start)
                .endDateTime(end)
                .doctor(doctor)
                .facility(facility)
                .build();

        Patient patient = Patient.builder()
                .id(1L)
                .idCardNo("ABC123456")
                .firstName("firstName")
                .lastName("lastName")
                .phoneNumber("123456789")
                .build();

        when(visitJpaRepository.findById(1L)).thenReturn(Optional.of(visit));
        when(patientJpaRepository.findById(1L)).thenReturn(Optional.of(patient));
        when(visitJpaRepository.save(any(Visit.class))).thenReturn(visit);

        // when
        VisitDto result = visitService.registerPatientForVisit(1L, 1L);

        // then
        assertAll(
                () -> assertEquals(1L, result.getId()),
                () -> assertEquals(start, result.getStartDateTime()),
                () -> assertEquals(end, result.getEndDateTime()),
                () -> assertEquals(1L, result.getDoctorId()),
                () -> assertEquals(1L, result.getFacilityId()),
                () -> assertEquals(1L, result.getPatientId())
        );
    }

    @Test
    void getPatientVisits_DataCorrect_ReturnVisits() {
        // given
        Facility facility = Facility.builder()
                .id(1L)
                .name("facilityName")
                .city("city")
                .zipCode("00-000")
                .street("street")
                .buildingNumber("1")
                .build();

        Doctor doctor = Doctor.builder()
                .id(1L)
                .email("email")
                .password("pass")
                .firstName("firstName")
                .lastName("lastName")
                .specialization("cardiology")
                .build();

        Patient patient = Patient.builder()
                .id(1L)
                .idCardNo("ABC123456")
                .firstName("firstName")
                .lastName("lastName")
                .phoneNumber("123456789")
                .build();

        LocalDateTime start = LocalDateTime.now().plusDays(1).withHour(10).withMinute(0).withSecond(0).withNano(0);
        LocalDateTime end = start.plusMinutes(15);
        LocalDateTime start2 = start.plusDays(1);
        LocalDateTime end2 = start2.plusMinutes(15);

        Visit visit = Visit.builder()
                .id(1L)
                .startDateTime(start)
                .endDateTime(end)
                .doctor(doctor)
                .facility(facility)
                .patient(patient)
                .build();

        Visit visit2 = Visit.builder()
                .id(2L)
                .startDateTime(start2)
                .endDateTime(end2)
                .doctor(doctor)
                .facility(facility)
                .patient(patient)
                .build();

        List<Visit> visits = List.of(visit, visit2);
        Pageable pageable = PageRequest.of(0, 10);
        Page<Visit> visitPage = new PageImpl<>(visits, pageable, visits.size());
        when(visitJpaRepository.findByPatientId(1L, pageable)).thenReturn(visitPage);

        // when
        Page<VisitDto> result = visitService.getPatientVisits(1L, pageable);

        // then
        assertAll(
                () -> assertEquals(2, result.getContent().size()),
                () -> assertEquals(1L, result.getContent().get(0).getId()),
                () -> assertEquals(start, result.getContent().get(0).getStartDateTime()),
                () -> assertEquals(end, result.getContent().get(0).getEndDateTime()),
                () -> assertEquals(1L, result.getContent().get(0).getDoctorId()),
                () -> assertEquals(1L, result.getContent().get(0).getFacilityId()),
                () -> assertEquals(1L, result.getContent().get(0).getPatientId()),
                () -> assertEquals(2L, result.getContent().get(1).getId()),
                () -> assertEquals(start2, result.getContent().get(1).getStartDateTime()),
                () -> assertEquals(end2, result.getContent().get(1).getEndDateTime()),
                () -> assertEquals(1L, result.getContent().get(1).getDoctorId()),
                () -> assertEquals(1L, result.getContent().get(1).getFacilityId()),
                () -> assertEquals(1L, result.getContent().get(1).getPatientId())
        );
    }

    @Test
    void getAvailableVisits_DataCorrect_ReturnVisits() {
        // given
        Facility facility = Facility.builder()
                .id(1L)
                .name("facilityName")
                .city("city")
                .zipCode("00-000")
                .street("street")
                .buildingNumber("1")
                .build();

        Doctor doctor = Doctor.builder()
                .id(1L)
                .email("email")
                .password("pass")
                .firstName("firstName")
                .lastName("lastName")
                .specialization("cardiology")
                .build();

        LocalDateTime start = LocalDateTime.now().plusDays(1).withHour(10).withMinute(0).withSecond(0).withNano(0);
        LocalDateTime end = start.plusMinutes(15);
        LocalDateTime start2 = start.plusDays(1);
        LocalDateTime end2 = start2.plusMinutes(15);

        Visit visit = Visit.builder()
                .id(1L)
                .startDateTime(start)
                .endDateTime(end)
                .doctor(doctor)
                .facility(facility)
                .build();

        Visit visit2 = Visit.builder()
                .id(2L)
                .startDateTime(start2)
                .endDateTime(end2)
                .doctor(doctor)
                .facility(facility)
                .build();

        List<Visit> visits = List.of(visit, visit2);
        Pageable pageable = PageRequest.of(0, 10);
        Page<Visit> visitPage = new PageImpl<>(visits, pageable, visits.size());
        when(visitJpaRepository.findByPatientIsNull(pageable)).thenReturn(visitPage);

        // when
        Page<VisitDto> result = visitService.getAvailableVisits(pageable);

        // then
        assertAll(
                () -> assertEquals(2, result.getContent().size()),
                () -> assertEquals(1L, result.getContent().get(0).getId()),
                () -> assertEquals(start, result.getContent().get(0).getStartDateTime()),
                () -> assertEquals(end, result.getContent().get(0).getEndDateTime()),
                () -> assertEquals(1L, result.getContent().get(0).getDoctorId()),
                () -> assertEquals(1L, result.getContent().get(0).getFacilityId()),
                () -> assertNull(result.getContent().get(0).getPatientId()),
                () -> assertEquals(2L, result.getContent().get(1).getId()),
                () -> assertEquals(start2, result.getContent().get(1).getStartDateTime()),
                () -> assertEquals(end2, result.getContent().get(1).getEndDateTime()),
                () -> assertEquals(1L, result.getContent().get(1).getDoctorId()),
                () -> assertEquals(1L, result.getContent().get(1).getFacilityId()),
                () -> assertNull(result.getContent().get(1).getPatientId())
        );
    }
}