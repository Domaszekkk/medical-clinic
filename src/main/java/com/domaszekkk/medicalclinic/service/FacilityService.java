package com.domaszekkk.medicalclinic.service;

import com.domaszekkk.medicalclinic.dto.AddFacilityCommand;
import com.domaszekkk.medicalclinic.dto.FacilityDto;
import com.domaszekkk.medicalclinic.entity.Facility;
import com.domaszekkk.medicalclinic.exception.FacilityNotFoundException;
import com.domaszekkk.medicalclinic.mapper.FacilityMapper;
import com.domaszekkk.medicalclinic.repository.FacilityJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@RequiredArgsConstructor
@Service
public class FacilityService {
    private final FacilityJpaRepository facilityJpaRepository;
    private final FacilityMapper facilityMapper;

    public List<FacilityDto> getAllFacilities() {
        return facilityMapper.mapToDtoList(facilityJpaRepository.findAll());
    }

    public FacilityDto addFacility(AddFacilityCommand command) {
        Facility facility = facilityMapper.mapToEntity(command);
        return facilityMapper.mapToDto(facilityJpaRepository.save(facility));
    }

    public FacilityDto getFacilityById(Long id) {
        Facility facility = facilityJpaRepository
                .findById(id)
                .orElseThrow(() -> new FacilityNotFoundException(id));
        return facilityMapper.mapToDto(facility);
    }

    public FacilityDto updateFacility(Long id, AddFacilityCommand command) {
        Facility facility = facilityJpaRepository
                .findById(id)
                .orElseThrow(() -> new FacilityNotFoundException(id));
        facility.update(facilityMapper.mapToEntity(command));
        return facilityMapper.mapToDto(facilityJpaRepository.save(facility));
    }

    @Transactional
    public void deleteFacility(Long id) {
        facilityJpaRepository.deleteById(id);
    }
}