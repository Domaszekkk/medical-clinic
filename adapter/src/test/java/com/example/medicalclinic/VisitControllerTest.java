package com.example.medicalclinic;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
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

    private Visit visit(Long id, Long doctorId, Long facilityId, Long patientId, LocalDateTime start, LocalDateTime end) {
        return Visit.builder()
                .id(id)
                .startDateTime(start)
                .endDateTime(end)
                .doctor(doctorId == null ? null : Doctor.builder().id(doctorId).build())
                .facility(facilityId == null ? null : Facility.builder().id(facilityId).build())
                .patient(patientId == null ? null : Patient.builder().id(patientId).build())
                .build();
    }

    @Test
    void addVisit_DataCorrect_ReturnsCreatedVisit() throws Exception {
        AddVisitCommand command = AddVisitCommand.builder()
                .startDateTime(LocalDateTime.of(2026, 8, 1, 10, 0))
                .endDateTime(LocalDateTime.of(2026, 8, 1, 10, 15))
                .facilityId(1L).build();
        Visit savedVisit = visit(1L, 1L, 1L, null,
                LocalDateTime.of(2026, 8, 1, 10, 0), LocalDateTime.of(2026, 8, 1, 10, 15));
        when(visitService.addVisit(eq(1L), any(AddVisitCommand.class))).thenReturn(savedVisit);

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
        AddVisitCommand command = AddVisitCommand.builder()
                .startDateTime(LocalDateTime.of(2026, 8, 1, 10, 0))
                .endDateTime(LocalDateTime.of(2026, 8, 1, 10, 15))
                .facilityId(1L).build();
        when(visitService.addVisit(eq(1L), any(AddVisitCommand.class))).thenThrow(new DoctorNotFoundException(1L));

        mockMvc.perform(MockMvcRequestBuilders.post("/doctors/{doctorId}/visits", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(command)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Doctor with id 1 not found"));
    }

    @Test
    void addVisit_DoctorNotAssignedToFacility_Returns409() throws Exception {
        AddVisitCommand command = AddVisitCommand.builder()
                .startDateTime(LocalDateTime.of(2026, 8, 1, 10, 0))
                .endDateTime(LocalDateTime.of(2026, 8, 1, 10, 15))
                .facilityId(1L).build();
        when(visitService.addVisit(eq(1L), any(AddVisitCommand.class)))
                .thenThrow(new DoctorNotAssignedToFacilityException(1L, 1L));

        mockMvc.perform(MockMvcRequestBuilders.post("/doctors/{doctorId}/visits", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(command)))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.message").value("Doctor with id 1 is not assigned to facility with id 1"));
    }

    @Test
    void registerPatientForVisit_DataCorrect_ReturnsVisit() throws Exception {
        Visit visit = visit(1L, 1L, 1L, 5L,
                LocalDateTime.of(2026, 8, 1, 10, 0), LocalDateTime.of(2026, 8, 1, 10, 15));
        when(visitService.registerPatientForVisit(1L, 5L)).thenReturn(visit);

        mockMvc.perform(MockMvcRequestBuilders.put("/visits/{visitId}", 1L).param("patientId", "5"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.patientId").value(5));
    }

    @Test
    void registerPatientForVisit_PatientNotFound_Returns404() throws Exception {
        when(visitService.registerPatientForVisit(1L, 5L)).thenThrow(new PatientNotFoundException(5L));

        mockMvc.perform(MockMvcRequestBuilders.put("/visits/{visitId}", 1L).param("patientId", "5"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Patient with id 5 not found"));
    }

    @Test
    void registerPatientForVisit_VisitNotFound_Returns404() throws Exception {
        when(visitService.registerPatientForVisit(1L, 5L)).thenThrow(new VisitNotFoundException(1L));

        mockMvc.perform(MockMvcRequestBuilders.put("/visits/{visitId}", 1L).param("patientId", "5"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Visit with id 1 not found"));
    }

    @Test
    void registerPatientForVisit_VisitAlreadyTaken_Returns409() throws Exception {
        when(visitService.registerPatientForVisit(1L, 5L)).thenThrow(new VisitAlreadyTakenException(1L));

        mockMvc.perform(MockMvcRequestBuilders.put("/visits/{visitId}", 1L).param("patientId", "5"))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.message").value("Visit with id 1 is already taken"));
    }

    @Test
    void getVisits_FilterByPatientId_ReturnsVisits() throws Exception {
        Visit v1 = visit(1L, 1L, 1L, 5L, LocalDateTime.of(2026, 8, 1, 10, 0), LocalDateTime.of(2026, 8, 1, 10, 15));
        Visit v2 = visit(2L, 2L, 1L, 5L, LocalDateTime.of(2026, 8, 2, 11, 0), LocalDateTime.of(2026, 8, 2, 11, 15));
        Page<Visit> page = Page.<Visit>builder().content(List.of(v1, v2))
                .pageNumber(0).pageSize(10).totalElements(2).totalPages(1).build();
        when(visitService.getVisits(eq(new VisitFilter(5L, null, null, null, null, null, null)), any()))
                .thenReturn(page);

        mockMvc.perform(MockMvcRequestBuilders.get("/visits").param("patientId", "5"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content.length()").value(2))
                .andExpect(jsonPath("$.content[0].id").value(1))
                .andExpect(jsonPath("$.content[1].id").value(2))
                .andExpect(jsonPath("$.totalElements").value(2));
    }

    @Test
    void getVisits_FilterByAvailable_ReturnsVisits() throws Exception {
        Visit v1 = visit(1L, 1L, 1L, null, LocalDateTime.of(2026, 8, 1, 10, 0), LocalDateTime.of(2026, 8, 1, 10, 15));
        Page<Visit> page = Page.<Visit>builder().content(List.of(v1))
                .pageNumber(0).pageSize(10).totalElements(1).totalPages(1).build();
        when(visitService.getVisits(eq(new VisitFilter(null, null, null, null, null, true, null)), any()))
                .thenReturn(page);

        mockMvc.perform(MockMvcRequestBuilders.get("/visits").param("available", "true"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content.length()").value(1))
                .andExpect(jsonPath("$.content[0].id").value(1))
                .andExpect(jsonPath("$.content[0].patientId").doesNotExist());
    }
}
