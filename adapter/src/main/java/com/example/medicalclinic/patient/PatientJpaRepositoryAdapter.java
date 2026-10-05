package com.example.medicalclinic.patient;

import com.example.medicalclinic.Page;
import com.example.medicalclinic.Pageable;
import com.example.medicalclinic.Patient;
import com.example.medicalclinic.PatientJpaRepositoryPort;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class PatientJpaRepositoryAdapter implements PatientJpaRepositoryPort {
    private final PatientJpaRepository patientJpaRepository;
    private final PatientMapper patientMapper;

    @Override
    public Page<Patient> findAll(Pageable pageable) {
        org.springframework.data.domain.Page<PatientEntity> page = patientJpaRepository
                .findAll(PageRequest.of(pageable.getPageNumber(), pageable.getPageSize()));
        List<Patient> content = page.getContent().stream()
                .map(patientMapper::toModel)
                .toList();
        return Page.<Patient>builder()
                .content(content)
                .pageNumber(page.getNumber())
                .pageSize(page.getSize())
                .totalElements(page.getTotalElements())
                .totalPages(page.getTotalPages())
                .build();
    }

    @Override
    public Optional<Patient> findById(Long id) {
        return patientJpaRepository.findById(id).map(patientMapper::toModel);
    }

    @Override
    public boolean existsById(Long id) {
        return patientJpaRepository.existsById(id);
    }

    @Override
    public Optional<Patient> findByUserEmail(String email) {
        return patientJpaRepository.findByUserEmail(email).map(patientMapper::toModel);
    }

    @Override
    public Patient save(Patient patient) {
        return patientMapper.toModel(patientJpaRepository.save(patientMapper.toEntity(patient)));
    }

    @Override
    @Transactional
    public void deleteByUserEmail(String email) {
        patientJpaRepository.deleteByUserEmail(email);
    }
}
