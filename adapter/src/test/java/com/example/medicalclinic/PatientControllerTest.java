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

    private Patient patient(Long id, Long userId, String firstName, String lastName, String phoneNumber, LocalDate birthday) {
        return Patient.builder()
                .id(id)
                .firstName(firstName)
                .lastName(lastName)
                .phoneNumber(phoneNumber)
                .birthday(birthday)
                .user(User.builder().id(userId).email("email").build())
                .build();
    }

    @Test
    void getAllPatients_DataCorrect_ReturnsPatients() throws Exception {
        Patient p1 = patient(1L, 1L, "firstName", "lastName", "123456789", LocalDate.of(2000, 1, 1));
        Patient p2 = patient(2L, 2L, "firstName2", "lastName2", "987654321", LocalDate.of(2001, 2, 2));
        Page<Patient> page = Page.<Patient>builder().content(List.of(p1, p2))
                .pageNumber(0).pageSize(10).totalElements(2).totalPages(1).build();
        when(patientService.getAllPatients(any())).thenReturn(page);

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
        AddPatientCommand command = AddPatientCommand.builder()
                .idCardNo("ABC123456").firstName("firstName").lastName("lastName")
                .phoneNumber("123456789").birthday(LocalDate.of(2000, 1, 1)).userId(1L).build();
        when(patientService.addPatient(any(AddPatientCommand.class)))
                .thenReturn(patient(1L, 1L, "firstName", "lastName", "123456789", LocalDate.of(2000, 1, 1)));

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
        when(patientService.getPatientByEmail("email"))
                .thenReturn(patient(1L, 1L, "firstName", "lastName", "123456789", LocalDate.of(2000, 1, 1)));

        mockMvc.perform(MockMvcRequestBuilders.get("/patients/{email}", "email"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.firstName").value("firstName"))
                .andExpect(jsonPath("$.lastName").value("lastName"));
    }

    @Test
    void getPatientByEmail_PatientNotFound_Returns404() throws Exception {
        when(patientService.getPatientByEmail("email")).thenThrow(new PatientNotFoundException("email"));

        mockMvc.perform(MockMvcRequestBuilders.get("/patients/{email}", "email"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Patient with email email not found"));
    }

    @Test
    void updatePatient_DataCorrect_ReturnsUpdatedPatient() throws Exception {
        UpdatePatientRequest request = UpdatePatientRequest.builder()
                .firstName("updatedFirstName").lastName("updatedLastName")
                .phoneNumber("111222333").birthday(LocalDate.of(1991, 2, 2)).build();
        when(patientService.updatePatient(eq("email"), any(UpdatePatientRequest.class)))
                .thenReturn(patient(1L, 1L, "updatedFirstName", "updatedLastName", "111222333", LocalDate.of(1991, 2, 2)));

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
        mockMvc.perform(MockMvcRequestBuilders.delete("/patients/{email}", "email"))
                .andExpect(status().isNoContent());

        verify(patientService).deletePatientByEmail("email");
    }

    @Test
    void updatePassword_DataCorrect_Returns204() throws Exception {
        ChangePasswordCommand command = new ChangePasswordCommand();
        command.setPassword("newPassword");

        mockMvc.perform(MockMvcRequestBuilders.patch("/patients/{email}/password", "email")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(command)))
                .andExpect(status().isNoContent());

        verify(patientService).updatePassword("email", "newPassword");
    }
}
