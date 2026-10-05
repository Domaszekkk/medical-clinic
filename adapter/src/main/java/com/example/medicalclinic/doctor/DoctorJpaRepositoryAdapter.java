package com.example.medicalclinic.doctor;

import com.example.medicalclinic.Doctor;
import com.example.medicalclinic.DoctorJpaRepositoryPort;
import com.example.medicalclinic.Page;
import com.example.medicalclinic.Pageable;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class DoctorJpaRepositoryAdapter implements DoctorJpaRepositoryPort {
    private final DoctorJpaRepository doctorJpaRepository;
    private final DoctorMapper doctorMapper;

    @Override
    public Page<Doctor> findAll(String specialization, Pageable pageable) {
        Specification<DoctorEntity> spec = DoctorSpecifications.hasSpecialization(specialization);
        org.springframework.data.domain.Page<DoctorEntity> page = doctorJpaRepository
                .findAll(spec, PageRequest.of(pageable.getPageNumber(), pageable.getPageSize()));
        List<Doctor> content = page.getContent().stream()
                .map(doctorMapper::toModel)
                .toList();
        return Page.<Doctor>builder()
                .content(content)
                .pageNumber(page.getNumber())
                .pageSize(page.getSize())
                .totalElements(page.getTotalElements())
                .totalPages(page.getTotalPages())
                .build();
    }

    @Override
    public Optional<Doctor> findById(Long id) {
        return doctorJpaRepository.findById(id).map(doctorMapper::toModel);
    }

    @Override
    public boolean existsById(Long id) {
        return doctorJpaRepository.existsById(id);
    }

    @Override
    public Doctor save(Doctor doctor) {
        return doctorMapper.toModel(doctorJpaRepository.save(doctorMapper.toEntity(doctor)));
    }

    @Override
    public void deleteById(Long id) {
        doctorJpaRepository.deleteById(id);
    }
}
