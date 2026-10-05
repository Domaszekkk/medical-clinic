package com.example.medicalclinic;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

class FacilityServiceTest {

    private FacilityJpaRepositoryPort facilityJpaRepositoryPort;
    private FacilityService facilityService;

    @BeforeEach
    void setup() {
        this.facilityJpaRepositoryPort = Mockito.mock(FacilityJpaRepositoryPort.class);
        this.facilityService = new FacilityService(facilityJpaRepositoryPort);
    }

    //    nazwaMetodyKtoraTestuje_StanKtóryTestuje_CoPowinnoSieStac

    @Test
    void getAllFacilities_DataCorrect_ReturnFacilities() {
        //given
        Facility facility = Facility.builder()
                .id(1L)
                .name("facilityName")
                .city("city")
                .zipCode("00-000")
                .street("street")
                .buildingNumber("1")
                .build();

        Facility facility2 = Facility.builder()
                .id(2L)
                .name("facilityName2")
                .city("city2")
                .zipCode("11-111")
                .street("street2")
                .buildingNumber("2")
                .build();

        Pageable pageable = Pageable.builder().pageNumber(0).pageSize(10).build();
        Page<Facility> facilityPage = Page.<Facility>builder()
                .content(List.of(facility, facility2))
                .pageNumber(0)
                .pageSize(10)
                .totalElements(2)
                .totalPages(1)
                .build();
        when(facilityJpaRepositoryPort.findAll(pageable)).thenReturn(facilityPage);

        //when
        Page<Facility> result = facilityService.getAllFacilities(pageable);

        //then
        assertAll(
                () -> assertEquals(2, result.getContent().size()),
                () -> assertEquals(1L, result.getContent().get(0).getId()),
                () -> assertEquals("facilityName", result.getContent().get(0).getName()),
                () -> assertEquals("city", result.getContent().get(0).getCity()),
                () -> assertEquals("00-000", result.getContent().get(0).getZipCode()),
                () -> assertEquals("street", result.getContent().get(0).getStreet()),
                () -> assertEquals("1", result.getContent().get(0).getBuildingNumber()),
                () -> assertEquals(2L, result.getContent().get(1).getId()),
                () -> assertEquals("facilityName2", result.getContent().get(1).getName()),
                () -> assertEquals("city2", result.getContent().get(1).getCity()),
                () -> assertEquals("11-111", result.getContent().get(1).getZipCode()),
                () -> assertEquals("street2", result.getContent().get(1).getStreet()),
                () -> assertEquals("2", result.getContent().get(1).getBuildingNumber())
        );
    }

    @Test
    void addFacility_DataCorrect_ReturnFacility() {
        //given
        AddFacilityCommand command = AddFacilityCommand.builder()
                .name("facilityName")
                .city("city")
                .zipCode("00-000")
                .street("street")
                .buildingNumber("1")
                .build();

        Facility savedFacility = Facility.builder()
                .id(1L)
                .name("facilityName")
                .city("city")
                .zipCode("00-000")
                .street("street")
                .buildingNumber("1")
                .build();

        when(facilityJpaRepositoryPort.save(any(Facility.class))).thenReturn(savedFacility);

        //when
        Facility result = facilityService.addFacility(command);

        //then
        assertAll(
                () -> assertEquals(1L, result.getId()),
                () -> assertEquals("facilityName", result.getName()),
                () -> assertEquals("city", result.getCity()),
                () -> assertEquals("00-000", result.getZipCode()),
                () -> assertEquals("street", result.getStreet()),
                () -> assertEquals("1", result.getBuildingNumber())
        );
    }

    @Test
    void getFacilityById_DataCorrect_ReturnFacility() {
        //given
        Facility facility = Facility.builder()
                .id(1L)
                .name("facilityName")
                .city("city")
                .zipCode("00-000")
                .street("street")
                .buildingNumber("1")
                .build();

        when(facilityJpaRepositoryPort.findById(1L)).thenReturn(Optional.of(facility));

        //when
        Facility result = facilityService.getFacilityById(1L);

        //then
        assertAll(
                () -> assertEquals(1L, result.getId()),
                () -> assertEquals("facilityName", result.getName()),
                () -> assertEquals("city", result.getCity()),
                () -> assertEquals("00-000", result.getZipCode()),
                () -> assertEquals("street", result.getStreet()),
                () -> assertEquals("1", result.getBuildingNumber())
        );
    }

    @Test
    void updateFacility_DataCorrect_ReturnUpdatedFacility() {
        //given
        Facility existingFacility = Facility.builder()
                .id(1L)
                .name("facilityName")
                .city("city")
                .zipCode("00-000")
                .street("street")
                .buildingNumber("1")
                .build();

        AddFacilityCommand command = AddFacilityCommand.builder()
                .name("updatedFacilityName")
                .city("updatedCity")
                .zipCode("99-999")
                .street("updatedStreet")
                .buildingNumber("99")
                .build();

        when(facilityJpaRepositoryPort.findById(1L)).thenReturn(Optional.of(existingFacility));
        when(facilityJpaRepositoryPort.save(any(Facility.class))).thenAnswer(invocation -> invocation.getArgument(0));

        //when
        Facility result = facilityService.updateFacility(1L, command);

        //then
        assertAll(
                () -> assertEquals(1L, result.getId()),
                () -> assertEquals("updatedFacilityName", result.getName()),
                () -> assertEquals("updatedCity", result.getCity()),
                () -> assertEquals("99-999", result.getZipCode()),
                () -> assertEquals("updatedStreet", result.getStreet()),
                () -> assertEquals("99", result.getBuildingNumber())
        );
    }

    @Test
    void getFacilityById_FacilityNotFound_ThrowsFacilityNotFoundException() {
        //given
        when(facilityJpaRepositoryPort.findById(1L)).thenReturn(Optional.empty());

        //when
        FacilityNotFoundException exception = Assertions.assertThrows(
                FacilityNotFoundException.class, () -> facilityService.getFacilityById(1L));

        //then
        assertAll(
                () -> assertEquals("Facility with id 1 not found", exception.getMessage()),
                () -> assertEquals(404, exception.getResponseCode())
        );
    }

    @Test
    void updateFacility_FacilityNotFound_ThrowsFacilityNotFoundException() {
        //given
        AddFacilityCommand command = AddFacilityCommand.builder()
                .name("updatedFacilityName")
                .build();
        when(facilityJpaRepositoryPort.findById(1L)).thenReturn(Optional.empty());

        //when
        FacilityNotFoundException exception = Assertions.assertThrows(
                FacilityNotFoundException.class, () -> facilityService.updateFacility(1L, command));

        //then
        assertAll(
                () -> assertEquals("Facility with id 1 not found", exception.getMessage()),
                () -> assertEquals(404, exception.getResponseCode())
        );
    }

    @Test
    void deleteFacility_DataCorrect_DeletesFacility() {
        //given
        Long id = 1L;
        when(facilityJpaRepositoryPort.existsById(id)).thenReturn(true);

        //when
        facilityService.deleteFacility(id);

        //then
        verify(facilityJpaRepositoryPort).existsById(id);
        verify(facilityJpaRepositoryPort).deleteById(id);
        verifyNoMoreInteractions(facilityJpaRepositoryPort);
    }

    @Test
    void deleteFacility_FacilityNotFound_ThrowsFacilityNotFoundException() {
        //given
        Long id = 1L;
        when(facilityJpaRepositoryPort.existsById(id)).thenReturn(false);

        //when + then
        FacilityNotFoundException exception = Assertions.assertThrows(
                FacilityNotFoundException.class, () -> facilityService.deleteFacility(id));

        assertAll(
                () -> assertEquals("Facility with id 1 not found", exception.getMessage()),
                () -> assertEquals(404, exception.getResponseCode())
        );
        verify(facilityJpaRepositoryPort, never()).deleteById(any());
    }
}
