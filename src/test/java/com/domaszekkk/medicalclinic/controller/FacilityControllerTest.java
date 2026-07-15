package com.domaszekkk.medicalclinic.controller;

import com.domaszekkk.medicalclinic.dto.AddFacilityCommand;
import com.domaszekkk.medicalclinic.dto.FacilityDto;
import com.domaszekkk.medicalclinic.exception.FacilityNotFoundException;
import com.domaszekkk.medicalclinic.service.FacilityService;
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

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class FacilityControllerTest {
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    @MockitoBean
    private FacilityService facilityService;

    @Test
    void getAllFacilities_DataCorrect_ReturnsFacilities() throws Exception {
        // given
        FacilityDto facility = FacilityDto.builder()
                .id(1L)
                .name("facilityName")
                .city("city")
                .zipCode("00-000")
                .street("street")
                .buildingNumber("1")
                .build();

        FacilityDto facility2 = FacilityDto.builder()
                .id(2L)
                .name("facilityName2")
                .city("city2")
                .zipCode("11-111")
                .street("street2")
                .buildingNumber("2")
                .build();

        Page<FacilityDto> page = new PageImpl<>(List.of(facility, facility2), PageRequest.of(0, 10), 2);
        when(facilityService.getAllFacilities(any())).thenReturn(page);

        // when + then
        mockMvc.perform(MockMvcRequestBuilders.get("/facilities"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content.length()").value(2))
                .andExpect(jsonPath("$.content[0].id").value(1))
                .andExpect(jsonPath("$.content[0].name").value("facilityName"))
                .andExpect(jsonPath("$.content[1].id").value(2))
                .andExpect(jsonPath("$.content[1].name").value("facilityName2"))
                .andExpect(jsonPath("$.totalElements").value(2));
    }

    @Test
    void getFacilityById_DataCorrect_ReturnsFacility() throws Exception {
        // given
        FacilityDto facility = FacilityDto.builder()
                .id(1L)
                .name("facilityName")
                .city("city")
                .zipCode("00-000")
                .street("street")
                .buildingNumber("1")
                .build();

        when(facilityService.getFacilityById(1L)).thenReturn(facility);

        // when + then
        mockMvc.perform(MockMvcRequestBuilders.get("/facilities/{id}", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("facilityName"))
                .andExpect(jsonPath("$.city").value("city"));
    }

    @Test
    void getFacilityById_FacilityNotFound_Returns404() throws Exception {
        // given
        when(facilityService.getFacilityById(1L)).thenThrow(new FacilityNotFoundException(1L));

        // when + then
        mockMvc.perform(MockMvcRequestBuilders.get("/facilities/{id}", 1L))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Facility with id 1 not found"));
    }

    @Test
    void addFacility_DataCorrect_ReturnsCreatedFacility() throws Exception {
        // given
        AddFacilityCommand command = AddFacilityCommand.builder()
                .name("facilityName")
                .city("city")
                .zipCode("00-000")
                .street("street")
                .buildingNumber("1")
                .build();

        FacilityDto savedFacility = FacilityDto.builder()
                .id(1L)
                .name("facilityName")
                .city("city")
                .zipCode("00-000")
                .street("street")
                .buildingNumber("1")
                .build();

        when(facilityService.addFacility(any(AddFacilityCommand.class))).thenReturn(savedFacility);

        // when + then
        mockMvc.perform(MockMvcRequestBuilders.post("/facilities")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(command)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("facilityName"))
                .andExpect(jsonPath("$.city").value("city"));
    }

    @Test
    void updateFacility_DataCorrect_ReturnsUpdatedFacility() throws Exception {
        // given
        AddFacilityCommand command = AddFacilityCommand.builder()
                .name("updatedName")
                .city("updatedCity")
                .zipCode("22-222")
                .street("updatedStreet")
                .buildingNumber("3")
                .build();

        FacilityDto updatedFacility = FacilityDto.builder()
                .id(1L)
                .name("updatedName")
                .city("updatedCity")
                .zipCode("22-222")
                .street("updatedStreet")
                .buildingNumber("3")
                .build();

        when(facilityService.updateFacility(eq(1L), any(AddFacilityCommand.class))).thenReturn(updatedFacility);

        // when + then
        mockMvc.perform(MockMvcRequestBuilders.put("/facilities/{id}", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(command)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("updatedName"))
                .andExpect(jsonPath("$.city").value("updatedCity"));
    }

    @Test
    void updateFacility_FacilityNotFound_Returns404() throws Exception {
        // given
        AddFacilityCommand command = AddFacilityCommand.builder()
                .name("updatedName")
                .city("updatedCity")
                .zipCode("22-222")
                .street("updatedStreet")
                .buildingNumber("3")
                .build();

        when(facilityService.updateFacility(eq(1L), any(AddFacilityCommand.class)))
                .thenThrow(new FacilityNotFoundException(1L));

        // when + then
        mockMvc.perform(MockMvcRequestBuilders.put("/facilities/{id}", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(command)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Facility with id 1 not found"));
    }

    @Test
    void deleteFacility_DataCorrect_Returns204() throws Exception {
        // when + then
        mockMvc.perform(MockMvcRequestBuilders.delete("/facilities/{id}", 1L))
                .andExpect(status().isNoContent());

        verify(facilityService).deleteFacility(1L);
    }
}