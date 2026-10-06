package com.example.medicalclinic.visit;

import com.example.medicalclinic.Page;
import com.example.medicalclinic.PageFactory;
import com.example.medicalclinic.Pageable;
import com.example.medicalclinic.Visit;
import com.example.medicalclinic.VisitFilter;
import com.example.medicalclinic.VisitJpaRepositoryPort;
import com.example.medicalclinic.VisitScope;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class VisitJpaRepositoryAdapter implements VisitJpaRepositoryPort {
    private final VisitJpaRepository visitJpaRepository;
    private final VisitMapper visitMapper;

    @Override
    public Page<Visit> findAll(VisitFilter filter, Pageable pageable) {
        Specification<VisitEntity> spec = Specification.allOf(
                VisitSpecifications.hasPatientId(filter.patientId()),
                VisitSpecifications.hasDoctorId(filter.doctorId()),
                VisitSpecifications.startsAtOrAfter(filter.from()),
                VisitSpecifications.startsBefore(filter.to()),
                VisitSpecifications.hasSpecialization(filter.specialization()),
                Boolean.TRUE.equals(filter.available()) ? VisitSpecifications.isAvailable() : null,
                scopeSpecification(filter.scope())
        );
        return PageFactory.from(
                visitJpaRepository.findAll(spec, PageRequest.of(pageable.getPageNumber(), pageable.getPageSize())),
                visitMapper::toModel);
    }

    @Override
    public Optional<Visit> findById(Long id) {
        return visitJpaRepository.findById(id).map(visitMapper::toModel);
    }

    @Override
    public Visit save(Visit visit) {
        return visitMapper.toModel(visitJpaRepository.save(visitMapper.toEntity(visit)));
    }

    @Override
    public void delete(Visit visit) {
        visitJpaRepository.deleteById(visit.getId());
    }

    @Override
    public List<Visit> findConflictingVisits(Long doctorId, LocalDateTime startDateTime, LocalDateTime endDateTime) {
        return visitJpaRepository.findConflictingVisits(doctorId, startDateTime, endDateTime).stream()
                .map(visitMapper::toModel)
                .toList();
    }

    private Specification<VisitEntity> scopeSpecification(VisitScope scope) {
        if (scope == null) {
            return null;
        }
        LocalDateTime now = LocalDateTime.now();
        return switch (scope) {
            case PAST -> VisitSpecifications.endsBefore(now);
            case UPCOMING -> VisitSpecifications.startsAfter(now);
            case ALL -> null;
        };
    }
}
