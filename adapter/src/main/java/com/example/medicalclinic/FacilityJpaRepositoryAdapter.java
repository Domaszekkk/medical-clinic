package com.example.medicalclinic;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class FacilityJpaRepositoryAdapter implements FacilityJpaRepositoryPort {
    private final FacilityJpaRepository facilityJpaRepository;
    private final FacilityMapper facilityMapper;

    @Override
    public Page<Facility> findAll(Pageable pageable) {
        org.springframework.data.domain.Page<FacilityEntity> page = facilityJpaRepository
                .findAll(PageRequest.of(pageable.getPageNumber(), pageable.getPageSize()));
        List<Facility> content = page.getContent().stream()
                .map(facilityMapper::toModel)
                .toList();
        return Page.<Facility>builder()
                .content(content)
                .pageNumber(page.getNumber())
                .pageSize(page.getSize())
                .totalElements(page.getTotalElements())
                .totalPages(page.getTotalPages())
                .build();
    }

    @Override
    public Optional<Facility> findById(Long id) {
        return facilityJpaRepository.findById(id)
                .map(facilityMapper::toModel);
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
