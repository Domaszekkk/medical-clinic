package com.domaszekkk.medicalclinic.service;

import com.domaszekkk.medicalclinic.dto.AddDoctorCommand;
import com.domaszekkk.medicalclinic.dto.DoctorDto;
import com.domaszekkk.medicalclinic.entity.Doctor;
import com.domaszekkk.medicalclinic.entity.Facility;
import com.domaszekkk.medicalclinic.exception.DoctorNotFoundException;
import com.domaszekkk.medicalclinic.exception.FacilityNotFoundException;
import com.domaszekkk.medicalclinic.mapper.DoctorMapper;
import com.domaszekkk.medicalclinic.repository.DoctorJpaRepository;
import com.domaszekkk.medicalclinic.repository.FacilityJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@RequiredArgsConstructor
@Service
public class DoctorService {
    private final DoctorJpaRepository doctorJpaRepository;
    private final DoctorMapper doctorMapper;
    private final FacilityJpaRepository facilityJpaRepository;

    public List<DoctorDto> getAllDoctors() {
        return doctorMapper.mapToDtoList(doctorJpaRepository.findAll());
    }

    public DoctorDto addDoctor(AddDoctorCommand command) {
        Doctor doctor = doctorMapper.mapToEntity(command);
        return doctorMapper.mapToDto(doctorJpaRepository.save(doctor));
    }

    public DoctorDto getDoctorById(Long id) {
        Doctor doctor = doctorJpaRepository
                .findById(id)
                .orElseThrow(() -> new DoctorNotFoundException(id));
        return doctorMapper.mapToDto(doctor);
    }

    public DoctorDto updateDoctor(Long id, AddDoctorCommand command) {
        Doctor doctor = doctorJpaRepository
                .findById(id)
                .orElseThrow(() -> new DoctorNotFoundException(id));
        doctor.update(doctorMapper.mapToEntity(command));
        return doctorMapper.mapToDto(doctorJpaRepository.save(doctor));
    }

    @Transactional
    public void deleteDoctor(Long id) {
        doctorJpaRepository.deleteById(id);
    }

    public DoctorDto assignDoctorToFacility(Long doctorId, Long facilityId) {
        Doctor doctor = doctorJpaRepository
                .findById(doctorId)
                .orElseThrow(() -> new DoctorNotFoundException(doctorId));
        Facility facility = facilityJpaRepository
                .findById(facilityId)
                .orElseThrow(() -> new FacilityNotFoundException(facilityId));
        doctor.getFacilities().add(facility);
        return doctorMapper.mapToDto(doctorJpaRepository.save(doctor));
    }
}