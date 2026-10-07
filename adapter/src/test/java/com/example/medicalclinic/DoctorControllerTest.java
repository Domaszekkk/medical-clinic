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

import java.util.ArrayList;
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
class DoctorControllerTest {
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    @MockitoBean
    private DoctorService doctorService;

    private Doctor doctor(Long id, Long userId, String firstName, String lastName, String specialization) {
        return Doctor.builder()
                .id(id)
                .firstName(firstName)
                .lastName(lastName)
                .specialization(specialization)
                .user(User.builder().id(userId).email("email").build())
                .facilities(new ArrayList<>())
                .build();
    }

    @Test
    void getAllDoctors_DataCorrect_ReturnsDoctors() throws Exception {
        Doctor d1 = doctor(1L, 1L, "firstName", "lastName", "cardiology");
        Doctor d2 = doctor(2L, 2L, "firstName2", "lastName2", "neurology");
        Page<Doctor> page = Page.<Doctor>builder().content(List.of(d1, d2))
                .pageNumber(0).pageSize(10).totalElements(2).totalPages(1).build();
        when(doctorService.getDoctors(any(), any())).thenReturn(page);

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
        when(doctorService.getDoctorById(1L)).thenReturn(doctor(1L, 1L, "firstName", "lastName", "cardiology"));

        mockMvc.perform(MockMvcRequestBuilders.get("/doctors/{id}", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.userId").value(1))
                .andExpect(jsonPath("$.specialization").value("cardiology"));
    }

    @Test
    void getDoctorById_DoctorNotFound_Returns404() throws Exception {
        when(doctorService.getDoctorById(1L)).thenThrow(new DoctorNotFoundException(1L));

        mockMvc.perform(MockMvcRequestBuilders.get("/doctors/{id}", 1L))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Doctor with id 1 not found"));
    }

    @Test
    void addDoctor_DataCorrect_ReturnsCreatedDoctor() throws Exception {
        AddDoctorCommand command = AddDoctorCommand.builder()
                .firstName("firstName").lastName("lastName").specialization("cardiology").userId(1L).build();
        when(doctorService.addDoctor(any(AddDoctorCommand.class)))
                .thenReturn(doctor(1L, 1L, "firstName", "lastName", "cardiology"));

        mockMvc.perform(MockMvcRequestBuilders.post("/doctors")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(command)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.userId").value(1))
                .andExpect(jsonPath("$.specialization").value("cardiology"));
    }

    @Test
    void updateDoctor_DataCorrect_ReturnsUpdatedDoctor() throws Exception {
        UpdateDoctorRequest request = UpdateDoctorRequest.builder()
                .firstName("updatedFirstName").lastName("updatedLastName").specialization("neurology").build();
        when(doctorService.updateDoctor(eq(1L), any(UpdateDoctorRequest.class)))
                .thenReturn(doctor(1L, 1L, "updatedFirstName", "updatedLastName", "neurology"));

        mockMvc.perform(MockMvcRequestBuilders.put("/doctors/{id}", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.firstName").value("updatedFirstName"))
                .andExpect(jsonPath("$.specialization").value("neurology"));
    }

    @Test
    void deleteDoctor_DataCorrect_Returns204() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.delete("/doctors/{id}", 1L))
                .andExpect(status().isNoContent());

        verify(doctorService).deleteDoctor(1L);
    }

    @Test
    void assignDoctorToFacility_DataCorrect_ReturnsDoctorWithFacility() throws Exception {
        Facility facility = Facility.builder().id(1L).name("facilityName").city("city")
                .zipCode("00-000").street("street").buildingNumber("1").build();
        Doctor doctor = doctor(1L, 1L, "firstName", "lastName", "cardiology");
        doctor.getFacilities().add(facility);
        when(doctorService.assignDoctorToFacility(1L, 1L)).thenReturn(doctor);

        mockMvc.perform(MockMvcRequestBuilders.post("/doctors/{doctorId}/facilities/{facilityId}", 1L, 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.facilities.length()").value(1))
                .andExpect(jsonPath("$.facilities[0].id").value(1))
                .andExpect(jsonPath("$.facilities[0].name").value("facilityName"));
    }

    @Test
    void assignDoctorToFacility_FacilityNotFound_Returns404() throws Exception {
        when(doctorService.assignDoctorToFacility(1L, 1L)).thenThrow(new FacilityNotFoundException(1L));

        mockMvc.perform(MockMvcRequestBuilders.post("/doctors/{doctorId}/facilities/{facilityId}", 1L, 1L))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Facility with id 1 not found"));
    }
}
