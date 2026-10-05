package com.example.medicalclinic;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

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
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class VisitServiceTest {

    private VisitJpaRepositoryPort visitJpaRepositoryPort;
    private DoctorJpaRepositoryPort doctorJpaRepositoryPort;
    private FacilityJpaRepositoryPort facilityJpaRepositoryPort;
    private PatientJpaRepositoryPort patientJpaRepositoryPort;
    private VisitService visitService;

    @BeforeEach
    void setup() {
        this.visitJpaRepositoryPort = Mockito.mock(VisitJpaRepositoryPort.class);
        this.doctorJpaRepositoryPort = Mockito.mock(DoctorJpaRepositoryPort.class);
        this.facilityJpaRepositoryPort = Mockito.mock(FacilityJpaRepositoryPort.class);
        this.patientJpaRepositoryPort = Mockito.mock(PatientJpaRepositoryPort.class);
        this.visitService = new VisitService(visitJpaRepositoryPort, doctorJpaRepositoryPort, facilityJpaRepositoryPort, patientJpaRepositoryPort);
    }

    private Facility buildFacility() {
        return Facility.builder().id(1L).name("facilityName").city("city")
                .zipCode("00-000").street("street").buildingNumber("1").build();
    }

    private Doctor buildDoctor(Facility facility) {
        return Doctor.builder().id(1L).firstName("firstName").lastName("lastName").specialization("cardiology")
                .facilities(facility == null ? new ArrayList<>() : new ArrayList<>(List.of(facility))).build();
    }

    private Patient buildPatient() {
        return Patient.builder().id(1L).idCardNo("ABC123456").firstName("firstName")
                .lastName("lastName").phoneNumber("123456789").build();
    }

    @Test
    void addVisit_DataCorrect_ReturnVisit() {
        Facility facility = buildFacility();
        Doctor doctor = buildDoctor(facility);
        LocalDateTime start = LocalDateTime.now().plusDays(1).withHour(10).withMinute(0).withSecond(0).withNano(0);
        LocalDateTime end = start.plusMinutes(15);
        AddVisitCommand command = AddVisitCommand.builder().startDateTime(start).endDateTime(end).facilityId(1L).build();
        Visit savedVisit = Visit.builder().id(1L).startDateTime(start).endDateTime(end).doctor(doctor).facility(facility).build();
        when(doctorJpaRepositoryPort.findById(1L)).thenReturn(Optional.of(doctor));
        when(facilityJpaRepositoryPort.findById(1L)).thenReturn(Optional.of(facility));
        when(visitJpaRepositoryPort.findConflictingVisits(1L, start, end)).thenReturn(Collections.emptyList());
        when(visitJpaRepositoryPort.save(any(Visit.class))).thenReturn(savedVisit);

        Visit result = visitService.addVisit(1L, command);

        assertAll(
                () -> assertEquals(1L, result.getId()),
                () -> assertEquals(start, result.getStartDateTime()),
                () -> assertEquals(end, result.getEndDateTime()),
                () -> assertEquals(1L, result.getDoctor().getId()),
                () -> assertEquals(1L, result.getFacility().getId()),
                () -> assertNull(result.getPatient())
        );
    }

    @Test
    void addVisit_DoctorNotFound_ThrowsDoctorNotFoundException() {
        LocalDateTime start = LocalDateTime.now().plusDays(1).withHour(10).withMinute(0).withSecond(0).withNano(0);
        AddVisitCommand command = AddVisitCommand.builder().startDateTime(start).endDateTime(start.plusMinutes(15)).facilityId(1L).build();
        when(doctorJpaRepositoryPort.findById(1L)).thenReturn(Optional.empty());

        DoctorNotFoundException exception = Assertions.assertThrows(
                DoctorNotFoundException.class, () -> visitService.addVisit(1L, command));

        assertAll(
                () -> assertEquals("Doctor with id 1 not found", exception.getMessage()),
                () -> assertEquals(404, exception.getResponseCode())
        );
    }

    @Test
    void addVisit_FacilityNotFound_ThrowsFacilityNotFoundException() {
        Doctor doctor = Doctor.builder().id(1L).facilities(new ArrayList<>()).build();
        LocalDateTime start = LocalDateTime.now().plusDays(1).withHour(10).withMinute(0).withSecond(0).withNano(0);
        AddVisitCommand command = AddVisitCommand.builder().startDateTime(start).endDateTime(start.plusMinutes(15)).facilityId(1L).build();
        when(doctorJpaRepositoryPort.findById(1L)).thenReturn(Optional.of(doctor));
        when(facilityJpaRepositoryPort.findById(1L)).thenReturn(Optional.empty());

        FacilityNotFoundException exception = Assertions.assertThrows(
                FacilityNotFoundException.class, () -> visitService.addVisit(1L, command));

        assertAll(
                () -> assertEquals("Facility with id 1 not found", exception.getMessage()),
                () -> assertEquals(404, exception.getResponseCode())
        );
    }

    @Test
    void addVisit_DoctorNotAssignedToFacility_ThrowsDoctorNotAssignedToFacilityException() {
        Facility facility = Facility.builder().id(1L).build();
        Doctor doctor = Doctor.builder().id(1L).facilities(new ArrayList<>()).build();
        LocalDateTime start = LocalDateTime.now().plusDays(1).withHour(10).withMinute(0).withSecond(0).withNano(0);
        AddVisitCommand command = AddVisitCommand.builder().startDateTime(start).endDateTime(start.plusMinutes(15)).facilityId(1L).build();
        when(doctorJpaRepositoryPort.findById(1L)).thenReturn(Optional.of(doctor));
        when(facilityJpaRepositoryPort.findById(1L)).thenReturn(Optional.of(facility));

        DoctorNotAssignedToFacilityException exception = Assertions.assertThrows(
                DoctorNotAssignedToFacilityException.class, () -> visitService.addVisit(1L, command));

        assertAll(
                () -> assertEquals("Doctor with id 1 is not assigned to facility with id 1", exception.getMessage()),
                () -> assertEquals(409, exception.getResponseCode())
        );
    }

    @Test
    void addVisit_ConflictingVisit_ThrowsDoctorVisitConflictException() {
        Facility facility = Facility.builder().id(1L).build();
        Doctor doctor = Doctor.builder().id(1L).facilities(new ArrayList<>(List.of(facility))).build();
        LocalDateTime start = LocalDateTime.now().plusDays(1).withHour(10).withMinute(0).withSecond(0).withNano(0);
        LocalDateTime end = start.plusMinutes(15);
        AddVisitCommand command = AddVisitCommand.builder().startDateTime(start).endDateTime(end).facilityId(1L).build();
        Visit conflictingVisit = Visit.builder().id(2L).startDateTime(start).endDateTime(end).build();
        when(doctorJpaRepositoryPort.findById(1L)).thenReturn(Optional.of(doctor));
        when(facilityJpaRepositoryPort.findById(1L)).thenReturn(Optional.of(facility));
        when(visitJpaRepositoryPort.findConflictingVisits(1L, start, end)).thenReturn(List.of(conflictingVisit));

        DoctorVisitConflictException exception = Assertions.assertThrows(
                DoctorVisitConflictException.class, () -> visitService.addVisit(1L, command));

        assertAll(
                () -> assertEquals("Doctor already has a visit at " + start, exception.getMessage()),
                () -> assertEquals(409, exception.getResponseCode())
        );
    }

    @Test
    void registerPatientForVisit_DataCorrect_ReturnVisit() {
        Facility facility = buildFacility();
        Doctor doctor = buildDoctor(null);
        LocalDateTime start = LocalDateTime.now().plusDays(1).withHour(10).withMinute(0).withSecond(0).withNano(0);
        LocalDateTime end = start.plusMinutes(15);
        Visit visit = Visit.builder().id(1L).startDateTime(start).endDateTime(end).doctor(doctor).facility(facility).build();
        Patient patient = buildPatient();
        when(visitJpaRepositoryPort.findById(1L)).thenReturn(Optional.of(visit));
        when(patientJpaRepositoryPort.findById(1L)).thenReturn(Optional.of(patient));
        when(visitJpaRepositoryPort.save(any(Visit.class))).thenReturn(visit);

        Visit result = visitService.registerPatientForVisit(1L, 1L);

        assertAll(
                () -> assertEquals(1L, result.getId()),
                () -> assertEquals(1L, result.getDoctor().getId()),
                () -> assertEquals(1L, result.getFacility().getId()),
                () -> assertEquals(1L, result.getPatient().getId())
        );
    }

    @Test
    void registerPatientForVisit_VisitNotFound_ThrowsVisitNotFoundException() {
        when(visitJpaRepositoryPort.findById(1L)).thenReturn(Optional.empty());

        VisitNotFoundException exception = Assertions.assertThrows(
                VisitNotFoundException.class, () -> visitService.registerPatientForVisit(1L, 1L));

        assertAll(
                () -> assertEquals("Visit with id 1 not found", exception.getMessage()),
                () -> assertEquals(404, exception.getResponseCode())
        );
    }

    @Test
    void registerPatientForVisit_PatientNotFound_ThrowsPatientNotFoundException() {
        Visit visit = Visit.builder().id(1L).build();
        when(visitJpaRepositoryPort.findById(1L)).thenReturn(Optional.of(visit));
        when(patientJpaRepositoryPort.findById(1L)).thenReturn(Optional.empty());

        PatientNotFoundException exception = Assertions.assertThrows(
                PatientNotFoundException.class, () -> visitService.registerPatientForVisit(1L, 1L));

        assertAll(
                () -> assertEquals("Patient with id 1 not found", exception.getMessage()),
                () -> assertEquals(404, exception.getResponseCode())
        );
    }

    @Test
    void registerPatientForVisit_VisitAlreadyTaken_ThrowsVisitAlreadyTakenException() {
        Patient existingPatient = Patient.builder().id(2L).build();
        Visit visit = Visit.builder().id(1L).patient(existingPatient).build();
        Patient newPatient = buildPatient();
        when(visitJpaRepositoryPort.findById(1L)).thenReturn(Optional.of(visit));
        when(patientJpaRepositoryPort.findById(1L)).thenReturn(Optional.of(newPatient));

        VisitAlreadyTakenException exception = Assertions.assertThrows(
                VisitAlreadyTakenException.class, () -> visitService.registerPatientForVisit(1L, 1L));

        assertAll(
                () -> assertEquals("Visit with id 1 is already taken", exception.getMessage()),
                () -> assertEquals(409, exception.getResponseCode())
        );
    }

    @Test
    void getVisits_FilterByPatientId_ReturnVisits() {
        Facility facility = buildFacility();
        Doctor doctor = buildDoctor(null);
        Patient patient = buildPatient();
        LocalDateTime start = LocalDateTime.now().plusDays(1).withHour(10).withMinute(0).withSecond(0).withNano(0);
        Visit visit = Visit.builder().id(1L).startDateTime(start).endDateTime(start.plusMinutes(15))
                .doctor(doctor).facility(facility).patient(patient).build();
        Pageable pageable = Pageable.builder().pageNumber(0).pageSize(10).build();
        VisitFilter filter = new VisitFilter(1L, null, null, null, null, null, null);
        Page<Visit> page = Page.<Visit>builder().content(List.of(visit))
                .pageNumber(0).pageSize(10).totalElements(1).totalPages(1).build();
        when(patientJpaRepositoryPort.existsById(1L)).thenReturn(true);
        when(visitJpaRepositoryPort.findAll(filter, pageable)).thenReturn(page);

        Page<Visit> result = visitService.getVisits(filter, pageable);

        assertAll(
                () -> assertEquals(1, result.getContent().size()),
                () -> assertEquals(1L, result.getContent().get(0).getId()),
                () -> assertEquals(1L, result.getContent().get(0).getPatient().getId())
        );
    }

    @Test
    void getVisits_PatientNotFound_ThrowsPatientNotFoundException() {
        Pageable pageable = Pageable.builder().pageNumber(0).pageSize(10).build();
        VisitFilter filter = new VisitFilter(1L, null, null, null, null, null, null);
        when(patientJpaRepositoryPort.existsById(1L)).thenReturn(false);

        PatientNotFoundException exception = Assertions.assertThrows(
                PatientNotFoundException.class, () -> visitService.getVisits(filter, pageable));

        assertAll(
                () -> assertEquals("Patient with id 1 not found", exception.getMessage()),
                () -> assertEquals(404, exception.getResponseCode())
        );
        verify(visitJpaRepositoryPort, never()).findAll(any(VisitFilter.class), any(Pageable.class));
    }

    @Test
    void getVisits_DoctorNotFound_ThrowsDoctorNotFoundException() {
        Pageable pageable = Pageable.builder().pageNumber(0).pageSize(10).build();
        VisitFilter filter = new VisitFilter(null, 1L, null, null, null, null, null);
        when(doctorJpaRepositoryPort.existsById(1L)).thenReturn(false);

        DoctorNotFoundException exception = Assertions.assertThrows(
                DoctorNotFoundException.class, () -> visitService.getVisits(filter, pageable));

        assertAll(
                () -> assertEquals("Doctor with id 1 not found", exception.getMessage()),
                () -> assertEquals(404, exception.getResponseCode())
        );
        verify(visitJpaRepositoryPort, never()).findAll(any(VisitFilter.class), any(Pageable.class));
    }

    @Test
    void getVisits_InvalidDateRange_ThrowsInvalidVisitDateException() {
        LocalDateTime from = LocalDateTime.now().plusDays(2);
        LocalDateTime to = LocalDateTime.now().plusDays(1);
        Pageable pageable = Pageable.builder().pageNumber(0).pageSize(10).build();
        VisitFilter filter = new VisitFilter(null, null, from, to, "cardiology", null, null);

        Assertions.assertThrows(InvalidVisitDateException.class,
                () -> visitService.getVisits(filter, pageable));
        verify(visitJpaRepositoryPort, never()).findAll(any(VisitFilter.class), any(Pageable.class));
    }

    @Test
    void getVisits_OnlyFromProvided_DoesNotValidateRange() {
        LocalDateTime from = LocalDateTime.now().plusDays(1);
        Pageable pageable = Pageable.builder().pageNumber(0).pageSize(10).build();
        VisitFilter filter = new VisitFilter(null, null, from, null, null, null, null);
        Page<Visit> page = Page.<Visit>builder().content(List.of())
                .pageNumber(0).pageSize(10).totalElements(0).totalPages(0).build();
        when(visitJpaRepositoryPort.findAll(filter, pageable)).thenReturn(page);

        Assertions.assertDoesNotThrow(() -> visitService.getVisits(filter, pageable));
    }

    @Test
    void cancelVisit_DataCorrect_DeletesVisit() {
        Doctor doctor = buildDoctor(null);
        Visit visit = Visit.builder().id(1L).doctor(doctor).build();
        when(visitJpaRepositoryPort.findById(1L)).thenReturn(Optional.of(visit));

        visitService.cancelVisit(1L, 1L);

        verify(visitJpaRepositoryPort).delete(visit);
    }

    @Test
    void cancelVisit_DoctorIdNotProvided_DeletesWithoutOwnerCheck() {
        Doctor doctor = buildDoctor(null);
        Visit visit = Visit.builder().id(1L).doctor(doctor).build();
        when(visitJpaRepositoryPort.findById(1L)).thenReturn(Optional.of(visit));

        visitService.cancelVisit(1L, null);

        verify(visitJpaRepositoryPort).delete(visit);
    }

    @Test
    void cancelVisit_VisitNotFound_ThrowsVisitNotFoundException() {
        when(visitJpaRepositoryPort.findById(1L)).thenReturn(Optional.empty());

        VisitNotFoundException exception = Assertions.assertThrows(
                VisitNotFoundException.class, () -> visitService.cancelVisit(1L, 1L));

        assertAll(
                () -> assertEquals("Visit with id 1 not found", exception.getMessage()),
                () -> assertEquals(404, exception.getResponseCode())
        );
        verify(visitJpaRepositoryPort, never()).delete(any(Visit.class));
    }

    @Test
    void cancelVisit_VisitBelongsToDifferentDoctor_ThrowsVisitNotFoundException() {
        Doctor otherDoctor = buildDoctor(null);
        otherDoctor.setId(2L);
        Visit visit = Visit.builder().id(1L).doctor(otherDoctor).build();
        when(visitJpaRepositoryPort.findById(1L)).thenReturn(Optional.of(visit));

        VisitNotFoundException exception = Assertions.assertThrows(
                VisitNotFoundException.class, () -> visitService.cancelVisit(1L, 1L));

        assertAll(
                () -> assertEquals("Visit with id 1 not found", exception.getMessage()),
                () -> assertEquals(404, exception.getResponseCode())
        );
        verify(visitJpaRepositoryPort, never()).delete(any(Visit.class));
    }

    @Test
    void cancelVisit_VisitHasNoDoctor_ThrowsVisitNotFoundExceptionWithoutNPE() {
        Visit visit = Visit.builder().id(1L).doctor(null).build();
        when(visitJpaRepositoryPort.findById(1L)).thenReturn(Optional.of(visit));

        VisitNotFoundException exception = Assertions.assertThrows(
                VisitNotFoundException.class, () -> visitService.cancelVisit(1L, 1L));

        assertAll(
                () -> assertEquals("Visit with id 1 not found", exception.getMessage()),
                () -> assertEquals(404, exception.getResponseCode())
        );
        verify(visitJpaRepositoryPort, never()).delete(any(Visit.class));
    }
}
