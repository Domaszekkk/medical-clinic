package com.domaszekkk.medicalclinic.service;

import com.domaszekkk.medicalclinic.dto.AddFacilityCommand;
import com.domaszekkk.medicalclinic.dto.FacilityDto;
import com.domaszekkk.medicalclinic.entity.Facility;
import com.domaszekkk.medicalclinic.exception.FacilityNotFoundException;
import com.domaszekkk.medicalclinic.mapper.FacilityMapper;
import com.domaszekkk.medicalclinic.repository.FacilityJpaRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;
import org.mockito.Mockito;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class FacilityServiceTest {

    private FacilityJpaRepository facilityJpaRepository;
    private FacilityMapper facilityMapper;
    private FacilityService facilityService;

    @BeforeEach
    void setup() {
        this.facilityJpaRepository = Mockito.mock(FacilityJpaRepository.class);
        this.facilityMapper = Mappers.getMapper(FacilityMapper.class);
        this.facilityService = new FacilityService(facilityJpaRepository, facilityMapper);
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
        List<Facility> facilities = List.of(facility, facility2);
        Pageable pageable = PageRequest.of(0, 10);
        Page<Facility> facilityPage = new PageImpl<>(facilities, pageable, facilities.size());
        when(facilityJpaRepository.findAll(pageable)).thenReturn(facilityPage);

        //when
        Page<FacilityDto> result = facilityService.getAllFacilities(pageable);

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
        AddFacilityCommand command = new AddFacilityCommand();
        command.setName("facilityName");
        command.setCity("city");
        command.setZipCode("00-000");
        command.setStreet("street");
        command.setBuildingNumber("1");

        Facility savedFacility = Facility.builder()
                .id(1L)
                .name("facilityName")
                .city("city")
                .zipCode("00-000")
                .street("street")
                .buildingNumber("1")
                .build();

        when(facilityJpaRepository.save(any(Facility.class))).thenReturn(savedFacility);

        //when
        FacilityDto result = facilityService.addFacility(command);

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

        when(facilityJpaRepository.findById(1L)).thenReturn(Optional.of(facility));

        //when
        FacilityDto result = facilityService.getFacilityById(1L);

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

        AddFacilityCommand command = new AddFacilityCommand();
        command.setName("updatedFacilityName");
        command.setCity("updatedCity");
        command.setZipCode("99-999");
        command.setStreet("updatedStreet");
        command.setBuildingNumber("99");

        when(facilityJpaRepository.findById(1L)).thenReturn(Optional.of(existingFacility));
        when(facilityJpaRepository.save(any(Facility.class))).thenReturn(existingFacility);

        //when
        FacilityDto result = facilityService.updateFacility(1L, command);

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
        when(facilityJpaRepository.findById(1L)).thenReturn(Optional.empty());

        //when
        FacilityNotFoundException exception = Assertions.assertThrows(
                FacilityNotFoundException.class, () -> facilityService.getFacilityById(1L));

        //then
        assertAll(
                () -> assertEquals("Facility with id 1 not found", exception.getMessage()),
                () -> assertEquals(HttpStatus.NOT_FOUND, exception.getStatus())
        );
    }

    @Test
    void updateFacility_FacilityNotFound_ThrowsFacilityNotFoundException() {
        //given
        AddFacilityCommand command = new AddFacilityCommand();
        command.setName("updatedFacilityName");
        when(facilityJpaRepository.findById(1L)).thenReturn(Optional.empty());

        //when
        FacilityNotFoundException exception = Assertions.assertThrows(
                FacilityNotFoundException.class, () -> facilityService.updateFacility(1L, command));

        //then
        assertAll(
                () -> assertEquals("Facility with id 1 not found", exception.getMessage()),
                () -> assertEquals(HttpStatus.NOT_FOUND, exception.getStatus())
        );
    }

    @Test
    void deleteFacility_DataCorrect_DeletesFacility() {
        //given
        Long id = 1L;
        when(facilityJpaRepository.existsById(id)).thenReturn(true);

        //when
        facilityService.deleteFacility(id);

        //then
        verify(facilityJpaRepository).existsById(id);
        verify(facilityJpaRepository).deleteById(id);
        verifyNoMoreInteractions(facilityJpaRepository);
    }

    @Test
    void deleteFacility_FacilityNotFound_ThrowsFacilityNotFoundException() {
        //given
        Long id = 1L;
        when(facilityJpaRepository.existsById(id)).thenReturn(false);

        //when + then
        FacilityNotFoundException exception = Assertions.assertThrows(
                FacilityNotFoundException.class, () -> facilityService.deleteFacility(id));

        assertAll(
                () -> assertEquals("Facility with id 1 not found", exception.getMessage()),
                () -> assertEquals(HttpStatus.NOT_FOUND, exception.getStatus())
        );
        verify(facilityJpaRepository, never()).deleteById(any());
    }
}