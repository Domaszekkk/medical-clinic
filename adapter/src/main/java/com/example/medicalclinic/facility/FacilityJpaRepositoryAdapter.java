package com.example.medicalclinic.facility;

import com.example.medicalclinic.Facility;
import com.example.medicalclinic.FacilityJpaRepositoryPort;
import com.example.medicalclinic.Page;
import com.example.medicalclinic.PageFactory;
import com.example.medicalclinic.Pageable;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
@RequiredArgsConstructor
public class FacilityJpaRepositoryAdapter implements FacilityJpaRepositoryPort {
    private final FacilityJpaRepository facilityJpaRepository;
    private final FacilityMapper facilityMapper;

    @Override
    public Page<Facility> findAll(Pageable pageable) {
        return PageFactory.from(
                facilityJpaRepository.findAll(PageRequest.of(pageable.getPageNumber(), pageable.getPageSize())),
                facilityMapper::toModel);
    }

    @Override
    public Optional<Facility> findById(Long id) {
        return facilityJpaRepository.findById(id).map(facilityMapper::toModel);
    }

    @Override
    public boolean existsById(Long id) {
        return facilityJpaRepository.existsById(id);
    }

    @Override
    public Facility save(Facility facility) {
        FacilityEntity saved = facilityJpaRepository.save(facilityMapper.toEntity(facility));
        return facilityMapper.toModel(saved);
    }

    @Override
    public void deleteById(Long id) {
        facilityJpaRepository.deleteById(id);
    }
}
