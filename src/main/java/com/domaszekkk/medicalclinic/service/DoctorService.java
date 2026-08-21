package com.domaszekkk.medicalclinic.service;

import com.domaszekkk.medicalclinic.dto.AddDoctorCommand;
import com.domaszekkk.medicalclinic.dto.DoctorDto;
import com.domaszekkk.medicalclinic.dto.UpdateDoctorRequest;
import com.domaszekkk.medicalclinic.entity.Doctor;
import com.domaszekkk.medicalclinic.entity.Facility;
import com.domaszekkk.medicalclinic.entity.User;
import com.domaszekkk.medicalclinic.exception.DoctorAlreadyAssignedToFacilityException;
import com.domaszekkk.medicalclinic.exception.DoctorNotFoundException;
import com.domaszekkk.medicalclinic.exception.FacilityNotFoundException;
import com.domaszekkk.medicalclinic.exception.UserNotFoundException;
import com.domaszekkk.medicalclinic.mapper.DoctorMapper;
import com.domaszekkk.medicalclinic.repository.DoctorJpaRepository;
import com.domaszekkk.medicalclinic.repository.FacilityJpaRepository;
import com.domaszekkk.medicalclinic.repository.UserJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@RequiredArgsConstructor
@Service
public class DoctorService {
    private final DoctorJpaRepository doctorJpaRepository;
    private final DoctorMapper doctorMapper;
    private final FacilityJpaRepository facilityJpaRepository;
    private final UserJpaRepository userJpaRepository;

    public Page<DoctorDto> getDoctors(String specialization, Pageable pageable) {
        Page<Doctor> doctors = (specialization == null)
                ? doctorJpaRepository.findAll(pageable)
                : doctorJpaRepository.findBySpecialization(specialization, pageable);
        return doctors.map(doctorMapper::mapToDto);
    }

    public DoctorDto addDoctor(AddDoctorCommand command) {
        User user = userJpaRepository
                .findById(command.getUserId())
                .orElseThrow(() -> new UserNotFoundException(command.getUserId()));
        Doctor doctor = doctorMapper.mapToEntity(command);
        doctor.setUser(user);
        return doctorMapper.mapToDto(doctorJpaRepository.save(doctor));
    }

    public DoctorDto getDoctorById(Long id) {
        Doctor doctor = doctorJpaRepository
                .findById(id)
                .orElseThrow(() -> new DoctorNotFoundException(id));
        return doctorMapper.mapToDto(doctor);
    }

    public DoctorDto updateDoctor(Long id, UpdateDoctorRequest request) {
        Doctor doctor = doctorJpaRepository
                .findById(id)
                .orElseThrow(() -> new DoctorNotFoundException(id));
        doctorMapper.updateDoctorFromRequest(request, doctor);
        return doctorMapper.mapToDto(doctorJpaRepository.save(doctor));
    }

    @Transactional
    public void deleteDoctor(Long id) {
        if (!doctorJpaRepository.existsById(id)) {
            throw new DoctorNotFoundException(id);
        }
        doctorJpaRepository.deleteById(id);
    }

    @Transactional
    public DoctorDto assignDoctorToFacility(Long doctorId, Long facilityId) {
        Doctor doctor = doctorJpaRepository
                .findById(doctorId)
                .orElseThrow(() -> new DoctorNotFoundException(doctorId));
        Facility facility = facilityJpaRepository
                .findById(facilityId)
                .orElseThrow(() -> new FacilityNotFoundException(facilityId));
        validateFacilityNotAlreadyAssigned(doctor, facilityId);
        doctor.getFacilities().add(facility);
        return doctorMapper.mapToDto(doctorJpaRepository.save(doctor));
    }

    private void validateFacilityNotAlreadyAssigned(Doctor doctor, Long facilityId) {
        boolean isAlreadyAssigned = doctor.getFacilities().stream()
                .anyMatch(assignedFacility -> assignedFacility.getId().equals(facilityId));
        if (isAlreadyAssigned) {
            throw new DoctorAlreadyAssignedToFacilityException(doctor.getId(), facilityId);
        }
    }
}