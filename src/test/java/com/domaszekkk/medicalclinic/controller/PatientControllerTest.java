package com.domaszekkk.medicalclinic.controller;

import com.domaszekkk.medicalclinic.TestcontainersConfiguration;
import com.domaszekkk.medicalclinic.dto.AddPatientCommand;
import com.domaszekkk.medicalclinic.dto.ChangePasswordCommand;
import com.domaszekkk.medicalclinic.dto.PatientDto;
import com.domaszekkk.medicalclinic.dto.UpdatePatientRequest;
import com.domaszekkk.medicalclinic.exception.PatientNotFoundException;
import com.domaszekkk.medicalclinic.service.PatientService;
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

import java.time.LocalDate;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Import(TestcontainersConfiguration.class)
class PatientControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private PatientService patientService;

    @Test
    void getAllPatients_DataCorrect_ReturnsPatients() throws Exception {
        // given
        PatientDto patient = PatientDto.builder()
                .id(1L)
                .firstName("firstName")
                .lastName("lastName")
                .phoneNumber("123456789")
                .birthday(LocalDate.of(2000, 1, 1))
                .userId(1L)
                .build();

        PatientDto patient2 = PatientDto.builder()
                .id(2L)
                .firstName("firstName2")
                .lastName("lastName2")
                .phoneNumber("987654321")
                .birthday(LocalDate.of(2001, 2, 2))
                .userId(2L)
                .build();

        Page<PatientDto> page = new PageImpl<>(List.of(patient, patient2), PageRequest.of(0, 10), 2);
        when(patientService.getAllPatients(any())).thenReturn(page);

        // when + then
        mockMvc.perform(MockMvcRequestBuilders.get("/patients"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content.length()").value(2))
                .andExpect(jsonPath("$.content[0].id").value(1))
                .andExpect(jsonPath("$.content[0].firstName").value("firstName"))
                .andExpect(jsonPath("$.content[1].id").value(2))
                .andExpect(jsonPath("$.content[1].firstName").value("firstName2"))
                .andExpect(jsonPath("$.totalElements").value(2));
    }

    @Test
    void addPatient_DataCorrect_ReturnsCreatedPatient() throws Exception {
        // given
        AddPatientCommand command = new AddPatientCommand();
        command.setIdCardNo("ABC123456");
        command.setFirstName("firstName");
        command.setLastName("lastName");
        command.setPhoneNumber("123456789");
        command.setBirthday(LocalDate.of(2000, 1, 1));
        command.setUserId(1L);

        PatientDto savedPatient = PatientDto.builder()
                .id(1L)
                .firstName("firstName")
                .lastName("lastName")
                .phoneNumber("123456789")
                .birthday(LocalDate.of(2000, 1, 1))
                .userId(1L)
                .build();

        when(patientService.addPatient(any(AddPatientCommand.class))).thenReturn(savedPatient);

        // when + then
        mockMvc.perform(MockMvcRequestBuilders.post("/patients")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(command)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.firstName").value("firstName"))
                .andExpect(jsonPath("$.lastName").value("lastName"))
                .andExpect(jsonPath("$.phoneNumber").value("123456789"))
                .andExpect(jsonPath("$.userId").value(1));
    }

    @Test
    void getPatientByEmail_DataCorrect_ReturnsPatient() throws Exception {
        // given
        PatientDto patient = PatientDto.builder()
                .id(1L)
                .firstName("firstName")
                .lastName("lastName")
                .phoneNumber("123456789")
                .birthday(LocalDate.of(2000, 1, 1))
                .userId(1L)
                .build();

        when(patientService.getPatientByEmail("email")).thenReturn(patient);

        // when + then
        mockMvc.perform(MockMvcRequestBuilders.get("/patients/{email}", "email"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.firstName").value("firstName"))
                .andExpect(jsonPath("$.lastName").value("lastName"));
    }

    @Test
    void getPatientByEmail_PatientNotFound_Returns404() throws Exception {
        // given
        when(patientService.getPatientByEmail("email")).thenThrow(new PatientNotFoundException("email"));

        // when + then
        mockMvc.perform(MockMvcRequestBuilders.get("/patients/{email}", "email"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Patient with email email not found"));
    }

    @Test
    void updatePatient_DataCorrect_ReturnsUpdatedPatient() throws Exception {
        // given
        UpdatePatientRequest request = new UpdatePatientRequest();
        request.setFirstName("updatedFirstName");
        request.setLastName("updatedLastName");
        request.setPhoneNumber("111222333");
        request.setBirthday(LocalDate.of(1991, 2, 2));

        PatientDto updatedPatient = PatientDto.builder()
                .id(1L)
                .firstName("updatedFirstName")
                .lastName("updatedLastName")
                .phoneNumber("111222333")
                .birthday(LocalDate.of(1991, 2, 2))
                .userId(1L)
                .build();

        when(patientService.updatePatient(eq("email"), any(UpdatePatientRequest.class))).thenReturn(updatedPatient);

        // when + then
        mockMvc.perform(MockMvcRequestBuilders.put("/patients/{email}", "email")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.firstName").value("updatedFirstName"))
                .andExpect(jsonPath("$.lastName").value("updatedLastName"))
                .andExpect(jsonPath("$.phoneNumber").value("111222333"));
    }

    @Test
    void deletePatientByEmail_DataCorrect_Returns204() throws Exception {
        // when + then
        mockMvc.perform(MockMvcRequestBuilders.delete("/patients/{email}", "email"))
                .andExpect(status().isNoContent());

        verify(patientService).deletePatientByEmail("email");
    }

    @Test
    void updatePassword_DataCorrect_Returns204() throws Exception {
        // given
        ChangePasswordCommand command = new ChangePasswordCommand();
        command.setPassword("newPassword");

        // when + then
        mockMvc.perform(MockMvcRequestBuilders.patch("/patients/{email}/password", "email")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(command)))
                .andExpect(status().isNoContent());

        verify(patientService).updatePassword("email", "newPassword");
    }
}