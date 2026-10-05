package com.example.medicalclinic;


import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class FacilityService {
    private final FacilityJpaRepositoryPort facilityJpaRepositoryPort;

    public Page<Facility> getAllFacilities(Pageable pageable) {
        return facilityJpaRepositoryPort.findAll(pageable);
    }

    public Facility addFacility(AddFacilityCommand command) {
        return facilityJpaRepositoryPort.save(Facility.from(command));
    }

    public Facility getFacilityById(Long id) {
        return facilityJpaRepositoryPort
                .findById(id)
                .orElseThrow(() -> new FacilityNotFoundException(id));
    }

    public Facility updateFacility(Long id, AddFacilityCommand command) {
        Facility facility = facilityJpaRepositoryPort
                .findById(id)
                .orElseThrow(() -> new FacilityNotFoundException(id));
        facility.update(command);
        return facilityJpaRepositoryPort.save(facility);
    }

    public void deleteFacility(Long id) {
        if (!facilityJpaRepositoryPort.existsById(id)) {
            throw new FacilityNotFoundException(id);
        }
        facilityJpaRepositoryPort.deleteById(id);
    }
}