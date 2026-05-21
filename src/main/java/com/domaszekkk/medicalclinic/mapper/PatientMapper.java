package com.domaszekkk.medicalclinic.mapper;

import com.domaszekkk.medicalclinic.dto.AddPatientRequest;
import com.domaszekkk.medicalclinic.dto.PatientDto;
import com.domaszekkk.medicalclinic.dto.UpdatePatientRequest;
import com.domaszekkk.medicalclinic.entity.Patient;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class PatientMapper {
    public PatientDto mapToDto(Patient patient) {
        PatientDto dto = new PatientDto();
        dto.setEmail(patient.getEmail());
        dto.setFirstName(patient.getFirstName());
        dto.setLastName(patient.getLastName());
        dto.setPhoneNumber(patient.getPhoneNumber());
        return dto;
    }

    public List<PatientDto> mapToDtoList(List<Patient> patients) {
        return patients.stream()
                .map(patient -> mapToDto(patient))
                .toList();
    }

    public Patient mapToEntity(AddPatientRequest request) {
        Patient patient = new Patient();
        patient.setEmail(request.getEmail());
        patient.setPassword(request.getPassword());
        patient.setIdCardNo(request.getIdCardNo());
        patient.setFirstName(request.getFirstName());
        patient.setLastName(request.getLastName());
        patient.setPhoneNumber(request.getPhoneNumber());
        patient.setBirthday(request.getBirthday());
        return patient;
    }

    public Patient mapToEntity(UpdatePatientRequest request) {
        Patient patient = new Patient();
        patient.setEmail(request.getEmail());
        patient.setPassword(request.getPassword());
        patient.setIdCardNo(request.getIdCardNo());
        patient.setFirstName(request.getFirstName());
        patient.setLastName(request.getLastName());
        patient.setPhoneNumber(request.getPhoneNumber());
        patient.setBirthday(request.getBirthday());
        return patient;
    }
}