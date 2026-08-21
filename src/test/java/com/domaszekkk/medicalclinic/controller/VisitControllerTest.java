package com.domaszekkk.medicalclinic.controller;

import com.domaszekkk.medicalclinic.TestcontainersConfiguration;
import com.domaszekkk.medicalclinic.dto.AddVisitCommand;
import com.domaszekkk.medicalclinic.dto.VisitDto;
import com.domaszekkk.medicalclinic.exception.DoctorNotAssignedToFacilityException;
import com.domaszekkk.medicalclinic.exception.DoctorNotFoundException;
import com.domaszekkk.medicalclinic.exception.PatientNotFoundException;
import com.domaszekkk.medicalclinic.exception.VisitAlreadyTakenException;
import com.domaszekkk.medicalclinic.exception.VisitNotFoundException;
import com.domaszekkk.medicalclinic.service.VisitService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Import(TestcontainersConfiguration.class)
class VisitControllerTest {
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    @MockitoBean
    private VisitService visitService;

    @Test
    void addVisit_DataCorrect_ReturnsCreatedVisit() throws Exception {
        // given
        AddVisitCommand command = AddVisitCommand.builder()
                .startDateTime(LocalDateTime.of(2026, 8, 1, 10, 0))
                .endDateTime(LocalDateTime.of(2026, 8, 1, 10, 15))
                .facilityId(1L)
                .build();

        VisitDto savedVisit = VisitDto.builder()
                .id(1L)
                .startDateTime(LocalDateTime.of(2026, 8, 1, 10, 0))
                .endDateTime(LocalDateTime.of(2026, 8, 1, 10, 15))
                .doctorId(1L)
                .facilityId(1L)
                .patientId(null)
                .build();

        when(visitService.addVisit(eq(1L), any(AddVisitCommand.class))).thenReturn(savedVisit);

        // when + then
        mockMvc.perform(MockMvcRequestBuilders.post("/doctors/{doctorId}/visits", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(command)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.doctorId").value(1))
                .andExpect(jsonPath("$.facilityId").value(1));
    }

    @Test
    void addVisit_DoctorNotFound_Returns404() throws Exception {
        // given
        AddVisitCommand command = AddVisitCommand.builder()
                .startDateTime(LocalDateTime.of(2026, 8, 1, 10, 0))
                .endDateTime(LocalDateTime.of(2026, 8, 1, 10, 15))
                .facilityId(1L)
                .build();

        when(visitService.addVisit(eq(1L), any(AddVisitCommand.class)))
                .thenThrow(new DoctorNotFoundException(1L));

        // when + then
        mockMvc.perform(MockMvcRequestBuilders.post("/doctors/{doctorId}/visits", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(command)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Doctor with id 1 not found"));
    }

    @Test
    void addVisit_DoctorNotAssignedToFacility_Returns409() throws Exception {
        // given
        AddVisitCommand command = AddVisitCommand.builder()
                .startDateTime(LocalDateTime.of(2026, 8, 1, 10, 0))
                .endDateTime(LocalDateTime.of(2026, 8, 1, 10, 15))
                .facilityId(1L)
                .build();

        when(visitService.addVisit(eq(1L), any(AddVisitCommand.class)))
                .thenThrow(new DoctorNotAssignedToFacilityException(1L, 1L));

        // when + then
        mockMvc.perform(MockMvcRequestBuilders.post("/doctors/{doctorId}/visits", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(command)))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.message").value("Doctor with id 1 is not assigned to facility with id 1"));
    }

    @Test
    void registerPatientForVisit_DataCorrect_ReturnsVisit() throws Exception {
        // given
        VisitDto visit = VisitDto.builder()
                .id(1L)
                .startDateTime(LocalDateTime.of(2026, 8, 1, 10, 0))
                .endDateTime(LocalDateTime.of(2026, 8, 1, 10, 15))
                .doctorId(1L)
                .facilityId(1L)
                .patientId(5L)
                .build();

        when(visitService.registerPatientForVisit(1L, 5L)).thenReturn(visit);

        // when + then
        mockMvc.perform(MockMvcRequestBuilders.put("/visits/{visitId}", 1L)
                        .param("patientId", "5"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.patientId").value(5));
    }

    @Test
    void registerPatientForVisit_PatientNotFound_Returns404() throws Exception {
        // given
        when(visitService.registerPatientForVisit(1L, 5L))
                .thenThrow(new PatientNotFoundException(5L));

        // when + then
        mockMvc.perform(MockMvcRequestBuilders.put("/visits/{visitId}", 1L)
                        .param("patientId", "5"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Patient with id 5 not found"));
    }

    @Test
    void registerPatientForVisit_VisitNotFound_Returns404() throws Exception {
        // given
        when(visitService.registerPatientForVisit(1L, 5L))
                .thenThrow(new VisitNotFoundException(1L));

        // when + then
        mockMvc.perform(MockMvcRequestBuilders.put("/visits/{visitId}", 1L)
                        .param("patientId", "5"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Visit with id 1 not found"));
    }

    @Test
    void registerPatientForVisit_VisitAlreadyTaken_Returns409() throws Exception {
        // given
        when(visitService.registerPatientForVisit(1L, 5L))
                .thenThrow(new VisitAlreadyTakenException(1L));

        // when + then
        mockMvc.perform(MockMvcRequestBuilders.put("/visits/{visitId}", 1L)
                        .param("patientId", "5"))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.message").value("Visit with id 1 is already taken"));
    }

    @Test
    void getPatientVisits_DataCorrect_ReturnsVisits() throws Exception {
        // given
        VisitDto visit = VisitDto.builder()
                .id(1L)
                .startDateTime(LocalDateTime.of(2026, 8, 1, 10, 0))
                .endDateTime(LocalDateTime.of(2026, 8, 1, 10, 15))
                .doctorId(1L)
                .facilityId(1L)
                .patientId(5L)
                .build();

        VisitDto visit2 = VisitDto.builder()
                .id(2L)
                .startDateTime(LocalDateTime.of(2026, 8, 2, 11, 0))
                .endDateTime(LocalDateTime.of(2026, 8, 2, 11, 15))
                .doctorId(2L)
                .facilityId(1L)
                .patientId(5L)
                .build();

        Page<VisitDto> page = new PageImpl<>(List.of(visit, visit2), PageRequest.of(0, 10), 2);
        when(visitService.getPatientVisits(eq(5L), any())).thenReturn(page);

        // when + then
        mockMvc.perform(MockMvcRequestBuilders.get("/patients/{patientId}/visits", 5L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content.length()").value(2))
                .andExpect(jsonPath("$.content[0].id").value(1))
                .andExpect(jsonPath("$.content[1].id").value(2))
                .andExpect(jsonPath("$.totalElements").value(2));
    }

    @Test
    void getAvailableVisits_DataCorrect_ReturnsVisits() throws Exception {
        // given
        VisitDto visit = VisitDto.builder()
                .id(1L)
                .startDateTime(LocalDateTime.of(2026, 8, 1, 10, 0))
                .endDateTime(LocalDateTime.of(2026, 8, 1, 10, 15))
                .doctorId(1L)
                .facilityId(1L)
                .patientId(null)
                .build();

        Page<VisitDto> page = new PageImpl<>(List.of(visit), PageRequest.of(0, 10), 1);
        when(visitService.getAvailableVisits(any())).thenReturn(page);

        // when + then
        mockMvc.perform(MockMvcRequestBuilders.get("/visits/available"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content.length()").value(1))
                .andExpect(jsonPath("$.content[0].id").value(1))
                .andExpect(jsonPath("$.content[0].patientId").doesNotExist());
    }
}