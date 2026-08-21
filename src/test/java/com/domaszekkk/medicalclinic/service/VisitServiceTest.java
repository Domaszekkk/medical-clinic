package com.domaszekkk.medicalclinic.service;

import com.domaszekkk.medicalclinic.dto.AddVisitCommand;
import com.domaszekkk.medicalclinic.dto.VisitDto;
import com.domaszekkk.medicalclinic.dto.VisitScope;
import com.domaszekkk.medicalclinic.entity.Doctor;
import com.domaszekkk.medicalclinic.entity.Facility;
import com.domaszekkk.medicalclinic.entity.Patient;
import com.domaszekkk.medicalclinic.entity.Visit;
import com.domaszekkk.medicalclinic.exception.*;
import com.domaszekkk.medicalclinic.mapper.VisitMapper;
import com.domaszekkk.medicalclinic.repository.DoctorJpaRepository;
import com.domaszekkk.medicalclinic.repository.FacilityJpaRepository;
import com.domaszekkk.medicalclinic.repository.PatientJpaRepository;
import com.domaszekkk.medicalclinic.repository.VisitJpaRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;
import org.mockito.Mockito;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

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

    private Facility buildFacility() {
        return Facility.builder()
                .id(1L)
                .name("facilityName")
                .city("city")
                .zipCode("00-000")
                .street("street")
                .buildingNumber("1")
                .build();
    }

    private Doctor buildDoctor(Facility facility) {
        return Doctor.builder()
                .id(1L)
                .firstName("firstName")
                .lastName("lastName")
                .specialization("cardiology")
                .facilities(facility == null ? new ArrayList<>() : new ArrayList<>(List.of(facility)))
                .build();
    }

    private Patient buildPatient() {
        return Patient.builder()
                .id(1L)
                .idCardNo("ABC123456")
                .firstName("firstName")
                .lastName("lastName")
                .phoneNumber("123456789")
                .build();
    }

    @Test
    void addVisit_DataCorrect_ReturnVisit() {
        // given
        Facility facility = buildFacility();
        Doctor doctor = buildDoctor(facility);

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
        Facility facility = buildFacility();
        Doctor doctor = buildDoctor(null);

        LocalDateTime start = LocalDateTime.now().plusDays(1).withHour(10).withMinute(0).withSecond(0).withNano(0);
        LocalDateTime end = start.plusMinutes(15);

        Visit visit = Visit.builder()
                .id(1L)
                .startDateTime(start)
                .endDateTime(end)
                .doctor(doctor)
                .facility(facility)
                .build();

        Patient patient = buildPatient();

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
        Facility facility = buildFacility();
        Doctor doctor = buildDoctor(null);
        Patient patient = buildPatient();

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
        when(patientJpaRepository.existsById(1L)).thenReturn(true);
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
        Facility facility = buildFacility();
        Doctor doctor = buildDoctor(null);

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
        when(visitJpaRepository.findAll(any(Specification.class), eq(pageable))).thenReturn(visitPage);

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

    @Test
    void getDoctorAvailableVisits_DataCorrect_ReturnVisits() {
        // given
        Facility facility = buildFacility();
        Doctor doctor = buildDoctor(null);

        LocalDateTime start = LocalDateTime.now().plusDays(1).withHour(10).withMinute(0).withSecond(0).withNano(0);
        LocalDateTime end = start.plusMinutes(15);

        Visit visit = Visit.builder()
                .id(1L)
                .startDateTime(start)
                .endDateTime(end)
                .doctor(doctor)
                .facility(facility)
                .build();

        Pageable pageable = PageRequest.of(0, 10);
        Page<Visit> visitPage = new PageImpl<>(List.of(visit), pageable, 1);

        when(doctorJpaRepository.existsById(1L)).thenReturn(true);
        when(visitJpaRepository.findAll(any(Specification.class), eq(pageable))).thenReturn(visitPage);

        // when
        Page<VisitDto> result = visitService.getDoctorAvailableVisits(1L, pageable);

        // then
        assertAll(
                () -> assertEquals(1, result.getContent().size()),
                () -> assertEquals(1L, result.getContent().get(0).getId()),
                () -> assertEquals(start, result.getContent().get(0).getStartDateTime()),
                () -> assertEquals(end, result.getContent().get(0).getEndDateTime()),
                () -> assertEquals(1L, result.getContent().get(0).getDoctorId()),
                () -> assertEquals(1L, result.getContent().get(0).getFacilityId()),
                () -> assertNull(result.getContent().get(0).getPatientId())
        );
    }

    @Test
    void getDoctorAvailableVisits_DoctorNotFound_ThrowsDoctorNotFoundException() {
        // given
        Pageable pageable = PageRequest.of(0, 10);
        when(doctorJpaRepository.existsById(1L)).thenReturn(false);

        // when
        DoctorNotFoundException exception = Assertions.assertThrows(
                DoctorNotFoundException.class, () -> visitService.getDoctorAvailableVisits(1L, pageable));

        // then
        assertAll(
                () -> assertEquals("Doctor with id 1 not found", exception.getMessage()),
                () -> assertEquals(HttpStatus.NOT_FOUND, exception.getStatus())
        );
        verify(visitJpaRepository, never()).findAll(any(Specification.class), any(Pageable.class));
    }

    @Test
    void getDoctorVisits_ScopePast_ReturnVisits() {
        // given
        Facility facility = buildFacility();
        Doctor doctor = buildDoctor(null);

        LocalDateTime start = LocalDateTime.now().minusDays(1).withHour(10).withMinute(0).withSecond(0).withNano(0);
        LocalDateTime end = start.plusMinutes(15);

        Visit visit = Visit.builder()
                .id(1L)
                .startDateTime(start)
                .endDateTime(end)
                .doctor(doctor)
                .facility(facility)
                .build();

        Pageable pageable = PageRequest.of(0, 10);
        Page<Visit> visitPage = new PageImpl<>(List.of(visit), pageable, 1);

        when(doctorJpaRepository.existsById(1L)).thenReturn(true);
        when(visitJpaRepository.findAll(any(Specification.class), eq(pageable))).thenReturn(visitPage);

        // when
        Page<VisitDto> result = visitService.getDoctorVisits(1L, VisitScope.PAST, pageable);

        // then
        assertAll(
                () -> assertEquals(1, result.getContent().size()),
                () -> assertEquals(1L, result.getContent().get(0).getId()),
                () -> assertEquals(start, result.getContent().get(0).getStartDateTime()),
                () -> assertEquals(end, result.getContent().get(0).getEndDateTime()),
                () -> assertEquals(1L, result.getContent().get(0).getDoctorId()),
                () -> assertEquals(1L, result.getContent().get(0).getFacilityId()),
                () -> assertNull(result.getContent().get(0).getPatientId())
        );
    }

    @Test
    void getDoctorVisits_ScopeUpcoming_ReturnVisits() {
        // given
        Facility facility = buildFacility();
        Doctor doctor = buildDoctor(null);

        LocalDateTime start = LocalDateTime.now().plusDays(1).withHour(10).withMinute(0).withSecond(0).withNano(0);
        LocalDateTime end = start.plusMinutes(15);

        Visit visit = Visit.builder()
                .id(1L)
                .startDateTime(start)
                .endDateTime(end)
                .doctor(doctor)
                .facility(facility)
                .build();

        Pageable pageable = PageRequest.of(0, 10);
        Page<Visit> visitPage = new PageImpl<>(List.of(visit), pageable, 1);

        when(doctorJpaRepository.existsById(1L)).thenReturn(true);
        when(visitJpaRepository.findAll(any(Specification.class), eq(pageable))).thenReturn(visitPage);

        // when
        Page<VisitDto> result = visitService.getDoctorVisits(1L, VisitScope.UPCOMING, pageable);

        // then
        assertAll(
                () -> assertEquals(1, result.getContent().size()),
                () -> assertEquals(1L, result.getContent().get(0).getId()),
                () -> assertEquals(start, result.getContent().get(0).getStartDateTime()),
                () -> assertEquals(end, result.getContent().get(0).getEndDateTime()),
                () -> assertEquals(1L, result.getContent().get(0).getDoctorId()),
                () -> assertEquals(1L, result.getContent().get(0).getFacilityId()),
                () -> assertNull(result.getContent().get(0).getPatientId())
        );
    }

    @Test
    void getDoctorVisits_ScopeAll_ReturnVisits() {
        // given
        Facility facility = buildFacility();
        Doctor doctor = buildDoctor(null);

        LocalDateTime start = LocalDateTime.now().plusDays(1).withHour(10).withMinute(0).withSecond(0).withNano(0);
        LocalDateTime end = start.plusMinutes(15);

        Visit visit = Visit.builder()
                .id(1L)
                .startDateTime(start)
                .endDateTime(end)
                .doctor(doctor)
                .facility(facility)
                .build();

        Pageable pageable = PageRequest.of(0, 10);
        Page<Visit> visitPage = new PageImpl<>(List.of(visit), pageable, 1);

        when(doctorJpaRepository.existsById(1L)).thenReturn(true);
        when(visitJpaRepository.findAll(any(Specification.class), eq(pageable))).thenReturn(visitPage);

        // when
        Page<VisitDto> result = visitService.getDoctorVisits(1L, VisitScope.ALL, pageable);

        // then
        assertAll(
                () -> assertEquals(1, result.getContent().size()),
                () -> assertEquals(1L, result.getContent().get(0).getId()),
                () -> assertEquals(start, result.getContent().get(0).getStartDateTime()),
                () -> assertEquals(end, result.getContent().get(0).getEndDateTime()),
                () -> assertEquals(1L, result.getContent().get(0).getDoctorId()),
                () -> assertEquals(1L, result.getContent().get(0).getFacilityId()),
                () -> assertNull(result.getContent().get(0).getPatientId())
        );
    }

    @Test
    void getDoctorVisits_DoctorNotFound_ThrowsDoctorNotFoundException() {
        // given
        Pageable pageable = PageRequest.of(0, 10);
        when(doctorJpaRepository.existsById(1L)).thenReturn(false);

        // when
        DoctorNotFoundException exception = Assertions.assertThrows(
                DoctorNotFoundException.class, () -> visitService.getDoctorVisits(1L, VisitScope.ALL, pageable));

        // then
        assertAll(
                () -> assertEquals("Doctor with id 1 not found", exception.getMessage()),
                () -> assertEquals(HttpStatus.NOT_FOUND, exception.getStatus())
        );
        verify(visitJpaRepository, never()).findAll(any(Specification.class), any(Pageable.class));
    }

    @Test
    void cancelVisit_DataCorrect_DeletesVisit() {
        // given
        Doctor doctor = buildDoctor(null);
        Visit visit = Visit.builder()
                .id(1L)
                .doctor(doctor)
                .build();

        when(visitJpaRepository.findById(1L)).thenReturn(Optional.of(visit));

        // when
        visitService.cancelVisit(1L, 1L);

        // then
        verify(visitJpaRepository).delete(visit);
    }

    @Test
    void cancelVisit_VisitNotFound_ThrowsVisitNotFoundException() {
        // given
        when(visitJpaRepository.findById(1L)).thenReturn(Optional.empty());

        // when
        VisitNotFoundException exception = Assertions.assertThrows(
                VisitNotFoundException.class, () -> visitService.cancelVisit(1L, 1L));

        // then
        assertAll(
                () -> assertEquals("Visit with id 1 not found", exception.getMessage()),
                () -> assertEquals(HttpStatus.NOT_FOUND, exception.getStatus())
        );
        verify(visitJpaRepository, never()).delete(any(Visit.class));
    }

    @Test
    void cancelVisit_VisitBelongsToDifferentDoctor_ThrowsVisitNotFoundException() {
        // given
        Doctor otherDoctor = buildDoctor(null);
        otherDoctor.setId(2L);
        Visit visit = Visit.builder()
                .id(1L)
                .doctor(otherDoctor)
                .build();

        when(visitJpaRepository.findById(1L)).thenReturn(Optional.of(visit));

        // when
        VisitNotFoundException exception = Assertions.assertThrows(
                VisitNotFoundException.class, () -> visitService.cancelVisit(1L, 1L));

        // then
        assertAll(
                () -> assertEquals("Visit with id 1 not found", exception.getMessage()),
                () -> assertEquals(HttpStatus.NOT_FOUND, exception.getStatus())
        );
        verify(visitJpaRepository, never()).delete(any(Visit.class));
    }

    @Test
    void getAvailableVisitsInRange_DataCorrect_ReturnVisits() {
        // given
        Facility facility = buildFacility();
        Doctor doctor = buildDoctor(null);

        LocalDateTime from = LocalDateTime.now().plusDays(1).withHour(0).withMinute(0).withSecond(0).withNano(0);
        LocalDateTime to = from.plusDays(1);
        LocalDateTime start = from.plusHours(10);
        LocalDateTime end = start.plusMinutes(15);

        Visit visit = Visit.builder()
                .id(1L)
                .startDateTime(start)
                .endDateTime(end)
                .doctor(doctor)
                .facility(facility)
                .build();

        Pageable pageable = PageRequest.of(0, 10);
        Page<Visit> visitPage = new PageImpl<>(List.of(visit), pageable, 1);

        when(visitJpaRepository.findAll(any(Specification.class), eq(pageable))).thenReturn(visitPage);

        // when
        Page<VisitDto> result = visitService.getAvailableVisitsInRange(from, to, "cardiology", pageable);

        // then
        assertAll(
                () -> assertEquals(1, result.getContent().size()),
                () -> assertEquals(1L, result.getContent().get(0).getId()),
                () -> assertEquals(start, result.getContent().get(0).getStartDateTime()),
                () -> assertEquals(end, result.getContent().get(0).getEndDateTime()),
                () -> assertEquals(1L, result.getContent().get(0).getDoctorId()),
                () -> assertEquals(1L, result.getContent().get(0).getFacilityId()),
                () -> assertNull(result.getContent().get(0).getPatientId())
        );
    }

    @Test
    void getAvailableVisitsInRange_InvalidDateRange_ThrowsInvalidVisitDateException() {
        // given
        LocalDateTime from = LocalDateTime.now().plusDays(2);
        LocalDateTime to = LocalDateTime.now().plusDays(1);
        Pageable pageable = PageRequest.of(0, 10);

        // when + then
        Assertions.assertThrows(InvalidVisitDateException.class,
                () -> visitService.getAvailableVisitsInRange(from, to, "cardiology", pageable));
        verify(visitJpaRepository, never()).findAll(any(Specification.class), any(Pageable.class));
    }

    @Test
    void getVisitsInRange_DataCorrect_ReturnVisits() {
        // given
        Facility facility = buildFacility();
        Doctor doctor = buildDoctor(null);
        Patient patient = buildPatient();

        LocalDateTime from = LocalDateTime.now().plusDays(1).withHour(0).withMinute(0).withSecond(0).withNano(0);
        LocalDateTime to = from.plusDays(1);
        LocalDateTime start = from.plusHours(10);
        LocalDateTime end = start.plusMinutes(15);

        Visit visit = Visit.builder()
                .id(1L)
                .startDateTime(start)
                .endDateTime(end)
                .doctor(doctor)
                .facility(facility)
                .patient(patient)
                .build();

        Pageable pageable = PageRequest.of(0, 10);
        Page<Visit> visitPage = new PageImpl<>(List.of(visit), pageable, 1);

        when(visitJpaRepository.findAll(any(Specification.class), eq(pageable))).thenReturn(visitPage);

        // when
        Page<VisitDto> result = visitService.getVisitsInRange(from, to, "cardiology", pageable);

        // then
        assertAll(
                () -> assertEquals(1, result.getContent().size()),
                () -> assertEquals(1L, result.getContent().get(0).getId()),
                () -> assertEquals(start, result.getContent().get(0).getStartDateTime()),
                () -> assertEquals(end, result.getContent().get(0).getEndDateTime()),
                () -> assertEquals(1L, result.getContent().get(0).getDoctorId()),
                () -> assertEquals(1L, result.getContent().get(0).getFacilityId()),
                () -> assertEquals(1L, result.getContent().get(0).getPatientId())
        );
    }

    @Test
    void getVisitsInRange_InvalidDateRange_ThrowsInvalidVisitDateException() {
        // given
        LocalDateTime from = LocalDateTime.now().plusDays(2);
        LocalDateTime to = LocalDateTime.now().plusDays(1);
        Pageable pageable = PageRequest.of(0, 10);

        // when + then
        Assertions.assertThrows(InvalidVisitDateException.class,
                () -> visitService.getVisitsInRange(from, to, "cardiology", pageable));
        verify(visitJpaRepository, never()).findAll(any(Specification.class), any(Pageable.class));
    }

    @Test
    void addVisit_DoctorNotFound_ThrowsDoctorNotFoundException() {
        // given
        LocalDateTime start = LocalDateTime.now().plusDays(1).withHour(10).withMinute(0).withSecond(0).withNano(0);
        LocalDateTime end = start.plusMinutes(15);

        AddVisitCommand command = new AddVisitCommand();
        command.setStartDateTime(start);
        command.setEndDateTime(end);
        command.setFacilityId(1L);

        when(doctorJpaRepository.findById(1L)).thenReturn(Optional.empty());

        // when
        DoctorNotFoundException exception = Assertions.assertThrows(
                DoctorNotFoundException.class, () -> visitService.addVisit(1L, command));

        // then
        assertAll(
                () -> assertEquals("Doctor with id 1 not found", exception.getMessage()),
                () -> assertEquals(HttpStatus.NOT_FOUND, exception.getStatus())
        );
    }

    @Test
    void addVisit_FacilityNotFound_ThrowsFacilityNotFoundException() {
        // given
        Doctor doctor = Doctor.builder()
                .id(1L)
                .facilities(new ArrayList<>())
                .build();

        LocalDateTime start = LocalDateTime.now().plusDays(1).withHour(10).withMinute(0).withSecond(0).withNano(0);
        LocalDateTime end = start.plusMinutes(15);

        AddVisitCommand command = new AddVisitCommand();
        command.setStartDateTime(start);
        command.setEndDateTime(end);
        command.setFacilityId(1L);

        when(doctorJpaRepository.findById(1L)).thenReturn(Optional.of(doctor));
        when(facilityJpaRepository.findById(1L)).thenReturn(Optional.empty());

        // when
        FacilityNotFoundException exception = Assertions.assertThrows(
                FacilityNotFoundException.class, () -> visitService.addVisit(1L, command));

        // then
        assertAll(
                () -> assertEquals("Facility with id 1 not found", exception.getMessage()),
                () -> assertEquals(HttpStatus.NOT_FOUND, exception.getStatus())
        );
    }

    @Test
    void addVisit_DoctorNotAssignedToFacility_ThrowsDoctorNotAssignedToFacilityException() {
        // given
        Facility facility = Facility.builder()
                .id(1L)
                .build();

        Doctor doctor = Doctor.builder()
                .id(1L)
                .facilities(new ArrayList<>())
                .build();

        LocalDateTime start = LocalDateTime.now().plusDays(1).withHour(10).withMinute(0).withSecond(0).withNano(0);
        LocalDateTime end = start.plusMinutes(15);

        AddVisitCommand command = new AddVisitCommand();
        command.setStartDateTime(start);
        command.setEndDateTime(end);
        command.setFacilityId(1L);

        when(doctorJpaRepository.findById(1L)).thenReturn(Optional.of(doctor));
        when(facilityJpaRepository.findById(1L)).thenReturn(Optional.of(facility));

        // when
        DoctorNotAssignedToFacilityException exception = Assertions.assertThrows(
                DoctorNotAssignedToFacilityException.class, () -> visitService.addVisit(1L, command));

        // then
        assertAll(
                () -> assertEquals("Doctor with id 1 is not assigned to facility with id 1", exception.getMessage()),
                () -> assertEquals(HttpStatus.CONFLICT, exception.getStatus())
        );
    }

    @Test
    void addVisit_ConflictingVisit_ThrowsDoctorVisitConflictException() {
        // given
        Facility facility = Facility.builder()
                .id(1L)
                .build();

        Doctor doctor = Doctor.builder()
                .id(1L)
                .facilities(new ArrayList<>(List.of(facility)))
                .build();

        LocalDateTime start = LocalDateTime.now().plusDays(1).withHour(10).withMinute(0).withSecond(0).withNano(0);
        LocalDateTime end = start.plusMinutes(15);

        AddVisitCommand command = new AddVisitCommand();
        command.setStartDateTime(start);
        command.setEndDateTime(end);
        command.setFacilityId(1L);

        Visit conflictingVisit = Visit.builder()
                .id(2L)
                .startDateTime(start)
                .endDateTime(end)
                .build();

        when(doctorJpaRepository.findById(1L)).thenReturn(Optional.of(doctor));
        when(facilityJpaRepository.findById(1L)).thenReturn(Optional.of(facility));
        when(visitJpaRepository.findConflictingVisits(1L, start, end)).thenReturn(List.of(conflictingVisit));

        // when
        DoctorVisitConflictException exception = Assertions.assertThrows(
                DoctorVisitConflictException.class, () -> visitService.addVisit(1L, command));

        // then
        assertAll(
                () -> assertEquals("Doctor already has a visit at " + start, exception.getMessage()),
                () -> assertEquals(HttpStatus.CONFLICT, exception.getStatus())
        );
    }

    @Test
    void registerPatientForVisit_VisitNotFound_ThrowsVisitNotFoundException() {
        // given
        when(visitJpaRepository.findById(1L)).thenReturn(Optional.empty());

        // when
        VisitNotFoundException exception = Assertions.assertThrows(
                VisitNotFoundException.class, () -> visitService.registerPatientForVisit(1L, 1L));

        // then
        assertAll(
                () -> assertEquals("Visit with id 1 not found", exception.getMessage()),
                () -> assertEquals(HttpStatus.NOT_FOUND, exception.getStatus())
        );
    }

    @Test
    void registerPatientForVisit_PatientNotFound_ThrowsPatientNotFoundException() {
        // given
        Visit visit = Visit.builder()
                .id(1L)
                .build();

        when(visitJpaRepository.findById(1L)).thenReturn(Optional.of(visit));
        when(patientJpaRepository.findById(1L)).thenReturn(Optional.empty());

        // when
        PatientNotFoundException exception = Assertions.assertThrows(
                PatientNotFoundException.class, () -> visitService.registerPatientForVisit(1L, 1L));

        // then
        assertAll(
                () -> assertEquals("Patient with id 1 not found", exception.getMessage()),
                () -> assertEquals(HttpStatus.NOT_FOUND, exception.getStatus())
        );
    }

    @Test
    void registerPatientForVisit_VisitAlreadyTaken_ThrowsVisitAlreadyTakenException() {
        // given
        Patient existingPatient = Patient.builder()
                .id(2L)
                .idCardNo("XYZ987654")
                .firstName("otherFirstName")
                .lastName("otherLastName")
                .phoneNumber("987654321")
                .build();

        Visit visit = Visit.builder()
                .id(1L)
                .patient(existingPatient)
                .build();

        Patient newPatient = buildPatient();

        when(visitJpaRepository.findById(1L)).thenReturn(Optional.of(visit));
        when(patientJpaRepository.findById(1L)).thenReturn(Optional.of(newPatient));

        // when
        VisitAlreadyTakenException exception = Assertions.assertThrows(
                VisitAlreadyTakenException.class, () -> visitService.registerPatientForVisit(1L, 1L));

        // then
        assertAll(
                () -> assertEquals("Visit with id 1 is already taken", exception.getMessage()),
                () -> assertEquals(HttpStatus.CONFLICT, exception.getStatus())
        );
    }

    @Test
    void getPatientVisits_PatientNotFound_ThrowsPatientNotFoundException() {
        // given
        Pageable pageable = PageRequest.of(0, 10);
        when(patientJpaRepository.existsById(1L)).thenReturn(false);

        // when + then
        PatientNotFoundException exception = Assertions.assertThrows(
                PatientNotFoundException.class, () -> visitService.getPatientVisits(1L, pageable));

        assertAll(
                () -> assertEquals("Patient with id 1 not found", exception.getMessage()),
                () -> assertEquals(HttpStatus.NOT_FOUND, exception.getStatus())
        );
        verify(visitJpaRepository, never()).findByPatientId(any(), any());
    }
}