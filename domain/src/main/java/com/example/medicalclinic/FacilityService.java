package com.example.medicalclinic;


import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class FacilityService {
    private final FacilityJpaRepositoryPort facilityJpaRepositoryPort;

    public Page<Facility> getAllFacilities(Pageable pageable) {
        return facilityJpaRepositoryPort.findAll(pageable);
    }

    public Facility addFacility(AddFacilityCommand command) {
        Facility facility = Facility.builder()
                .name(command.getName())
                .city(command.getCity())
                .zipCode(command.getZipCode())
                .street(command.getStreet())
                .buildingNumber(command.getBuildingNumber())
                .build();
        return facilityJpaRepositoryPort.save(facility);
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
        facility.setName(command.getName());
        facility.setCity(command.getCity());
        facility.setZipCode(command.getZipCode());
        facility.setStreet(command.getStreet());
        facility.setBuildingNumber(command.getBuildingNumber());
        return facilityJpaRepositoryPort.save(facility);
    }

    public void deleteFacility(Long id) {
        if (!facilityJpaRepositoryPort.existsById(id)) {
            throw new FacilityNotFoundException(id);
        }
        facilityJpaRepositoryPort.deleteById(id);
    }
}