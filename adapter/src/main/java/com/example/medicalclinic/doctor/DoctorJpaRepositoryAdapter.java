package com.example.medicalclinic.doctor;

import com.example.medicalclinic.Doctor;
import com.example.medicalclinic.DoctorJpaRepositoryPort;
import com.example.medicalclinic.Page;
import com.example.medicalclinic.PageFactory;
import com.example.medicalclinic.Pageable;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
@RequiredArgsConstructor
public class DoctorJpaRepositoryAdapter implements DoctorJpaRepositoryPort {
    private final DoctorJpaRepository doctorJpaRepository;
    private final DoctorMapper doctorMapper;

    @Override
    public Page<Doctor> findAll(String specialization, Pageable pageable) {
        Specification<DoctorEntity> spec = DoctorSpecifications.hasSpecialization(specialization);
        return PageFactory.from(
                doctorJpaRepository.findAll(spec, PageRequest.of(pageable.getPageNumber(), pageable.getPageSize())),
                doctorMapper::toModel);
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
