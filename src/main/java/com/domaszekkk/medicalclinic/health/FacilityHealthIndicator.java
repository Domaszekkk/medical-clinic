//package com.domaszekkk.medicalclinic.health;
//
//import com.domaszekkk.medicalclinic.repository.FacilityJpaRepository;
//import lombok.RequiredArgsConstructor;
//import org.springframework.boot.actuate.health.Health;
//import org.springframework.boot.actuate.health.HealthIndicator;
//import org.springframework.stereotype.Component;
//
//@Component
//@RequiredArgsConstructor
//public class FacilityHealthIndicator implements HealthIndicator {
//
//    private final FacilityJpaRepository facilityJpaRepository;
//
//    @Override
//    public Health health() {
//        long facilityCount = facilityJpaRepository.count();
//
//        if (facilityCount > 0) {
//            return Health.up()
//                    .withDetail("facilityCount", facilityCount)
//                    .withDetail("message", "At least one facility is available")
//                    .build();
//        }
//
//        return Health.down()
//                .withDetail("facilityCount", 0)
//                .withDetail("message", "No facilities available in the system")
//                .build();
//    }
//}