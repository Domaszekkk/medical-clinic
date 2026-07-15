package com.domaszekkk.medicalclinic.controller;

import com.domaszekkk.medicalclinic.dto.AddDoctorCommand;
import com.domaszekkk.medicalclinic.dto.DoctorDto;
import com.domaszekkk.medicalclinic.dto.FacilityDto;
import com.domaszekkk.medicalclinic.exception.DoctorNotFoundException;
import com.domaszekkk.medicalclinic.exception.FacilityNotFoundException;
import com.domaszekkk.medicalclinic.service.DoctorService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.util.Collections;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class DoctorControllerTest {
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    @MockitoBean
    private DoctorService doctorService;

    @Test
    void getAllDoctors_DataCorrect_ReturnsDoctors() throws Exception {
        // given
        DoctorDto doctor = DoctorDto.builder()
                .id(1L)
                .email("email")
                .firstName("firstName")
                .lastName("lastName")
                .specialization("cardiology")
                .facilities(Collections.emptyList())
                .build();

        DoctorDto doctor2 = DoctorDto.builder()
                .id(2L)
                .email("email2")
                .firstName("firstName2")
                .lastName("lastName2")
                .specialization("neurology")
                .facilities(Collections.emptyList())
                .build();

        Page<DoctorDto> page = new PageImpl<>(List.of(doctor, doctor2), PageRequest.of(0, 10), 2);
        when(doctorService.getAllDoctors(any())).thenReturn(page);

        // when + then
        mockMvc.perform(MockMvcRequestBuilders.get("/doctors"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content.length()").value(2))
                .andExpect(jsonPath("$.content[0].id").value(1))
                .andExpect(jsonPath("$.content[0].specialization").value("cardiology"))
                .andExpect(jsonPath("$.content[1].id").value(2))
                .andExpect(jsonPath("$.content[1].specialization").value("neurology"));
    }

    @Test
    void getDoctorById_DataCorrect_ReturnsDoctor() throws Exception {
        // given
        DoctorDto doctor = DoctorDto.builder()
                .id(1L)
                .email("email")
                .firstName("firstName")
                .lastName("lastName")
                .specialization("cardiology")
                .facilities(Collections.emptyList())
                .build();

        when(doctorService.getDoctorById(1L)).thenReturn(doctor);

        // when + then
        mockMvc.perform(MockMvcRequestBuilders.get("/doctors/{id}", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.email").value("email"))
                .andExpect(jsonPath("$.specialization").value("cardiology"));
    }

    @Test
    void getDoctorById_DoctorNotFound_Returns404() throws Exception {
        // given
        when(doctorService.getDoctorById(1L)).thenThrow(new DoctorNotFoundException(1L));

        // when + then
        mockMvc.perform(MockMvcRequestBuilders.get("/doctors/{id}", 1L))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Doctor with id 1 not found"));
    }

    @Test
    void addDoctor_DataCorrect_ReturnsCreatedDoctor() throws Exception {
        // given
        AddDoctorCommand command = AddDoctorCommand.builder()
                .email("email")
                .password("pass")
                .firstName("firstName")
                .lastName("lastName")
                .specialization("cardiology")
                .build();

        DoctorDto savedDoctor = DoctorDto.builder()
                .id(1L)
                .email("email")
                .firstName("firstName")
                .lastName("lastName")
                .specialization("cardiology")
                .facilities(Collections.emptyList())
                .build();

        when(doctorService.addDoctor(any(AddDoctorCommand.class))).thenReturn(savedDoctor);

        // when + then
        mockMvc.perform(MockMvcRequestBuilders.post("/doctors")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(command)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.email").value("email"))
                .andExpect(jsonPath("$.specialization").value("cardiology"));
    }

    @Test
    void updateDoctor_DataCorrect_ReturnsUpdatedDoctor() throws Exception {
        // given
        AddDoctorCommand command = AddDoctorCommand.builder()
                .email("updatedEmail")
                .password("pass")
                .firstName("updatedFirstName")
                .lastName("updatedLastName")
                .specialization("neurology")
                .build();

        DoctorDto updatedDoctor = DoctorDto.builder()
                .id(1L)
                .email("updatedEmail")
                .firstName("updatedFirstName")
                .lastName("updatedLastName")
                .specialization("neurology")
                .facilities(Collections.emptyList())
                .build();

        when(doctorService.updateDoctor(eq(1L), any(AddDoctorCommand.class))).thenReturn(updatedDoctor);

        // when + then
        mockMvc.perform(MockMvcRequestBuilders.put("/doctors/{id}", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(command)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value("updatedEmail"))
                .andExpect(jsonPath("$.specialization").value("neurology"));
    }

    @Test
    void deleteDoctor_DataCorrect_Returns204() throws Exception {
        // when + then
        mockMvc.perform(MockMvcRequestBuilders.delete("/doctors/{id}", 1L))
                .andExpect(status().isNoContent());

        verify(doctorService).deleteDoctor(1L);
    }

    @Test
    void assignDoctorToFacility_DataCorrect_ReturnsDoctorWithFacility() throws Exception {
        // given
        FacilityDto facility = FacilityDto.builder()
                .id(1L)
                .name("facilityName")
                .city("city")
                .zipCode("00-000")
                .street("street")
                .buildingNumber("1")
                .build();

        DoctorDto doctor = DoctorDto.builder()
                .id(1L)
                .email("email")
                .firstName("firstName")
                .lastName("lastName")
                .specialization("cardiology")
                .facilities(List.of(facility))
                .build();

        when(doctorService.assignDoctorToFacility(1L, 1L)).thenReturn(doctor);

        // when + then
        mockMvc.perform(MockMvcRequestBuilders.post("/doctors/{doctorId}/facilities/{facilityId}", 1L, 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.facilities.length()").value(1))
                .andExpect(jsonPath("$.facilities[0].id").value(1))
                .andExpect(jsonPath("$.facilities[0].name").value("facilityName"));
    }

    @Test
    void assignDoctorToFacility_FacilityNotFound_Returns404() throws Exception {
        // given
        when(doctorService.assignDoctorToFacility(1L, 1L)).thenThrow(new FacilityNotFoundException(1L));

        // when + then
        mockMvc.perform(MockMvcRequestBuilders.post("/doctors/{doctorId}/facilities/{facilityId}", 1L, 1L))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Facility with id 1 not found"));
    }
}