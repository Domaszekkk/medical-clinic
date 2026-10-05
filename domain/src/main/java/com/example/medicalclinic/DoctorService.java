package com.example.medicalclinic;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class DoctorService {
    private final DoctorJpaRepositoryPort doctorJpaRepositoryPort;
    private final UserJpaRepositoryPort userJpaRepositoryPort;
    private final FacilityJpaRepositoryPort facilityJpaRepositoryPort;

    public Page<Doctor> getDoctors(String specialization, Pageable pageable) {
        return doctorJpaRepositoryPort.findAll(specialization, pageable);
    }

    public Doctor addDoctor(AddDoctorCommand command) {
        User user = userJpaRepositoryPort
                .findById(command.getUserId())
                .orElseThrow(() -> new UserNotFoundException(command.getUserId()));
        Doctor doctor = Doctor.from(command);
        doctor.setUser(user);
        return doctorJpaRepositoryPort.save(doctor);
    }

    public Doctor getDoctorById(Long id) {
        return doctorJpaRepositoryPort
                .findById(id)
                .orElseThrow(() -> new DoctorNotFoundException(id));
    }

    public Doctor updateDoctor(Long id, UpdateDoctorRequest request) {
        Doctor doctor = doctorJpaRepositoryPort
                .findById(id)
                .orElseThrow(() -> new DoctorNotFoundException(id));
        doctor.update(request);
        return doctorJpaRepositoryPort.save(doctor);
    }

    public void deleteDoctor(Long id) {
        if (!doctorJpaRepositoryPort.existsById(id)) {
            throw new DoctorNotFoundException(id);
        }
        doctorJpaRepositoryPort.deleteById(id);
    }

    public Doctor assignDoctorToFacility(Long doctorId, Long facilityId) {
        Doctor doctor = doctorJpaRepositoryPort
                .findById(doctorId)
                .orElseThrow(() -> new DoctorNotFoundException(doctorId));
        Facility facility = facilityJpaRepositoryPort
                .findById(facilityId)
                .orElseThrow(() -> new FacilityNotFoundException(facilityId));
        validateFacilityNotAlreadyAssigned(doctor, facilityId);
        doctor.getFacilities().add(facility);
        return doctorJpaRepositoryPort.save(doctor);
    }

    private void validateFacilityNotAlreadyAssigned(Doctor doctor, Long facilityId) {
        boolean isAlreadyAssigned = doctor.getFacilities().stream()
                .anyMatch(assignedFacility -> assignedFacility.getId().equals(facilityId));
        if (isAlreadyAssigned) {
            throw new DoctorAlreadyAssignedToFacilityException(doctor.getId(), facilityId);
        }
    }
}
