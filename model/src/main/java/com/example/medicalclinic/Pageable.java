package com.example.medicalclinic;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Pageable {
    private int pageNumber;
    private int pageSize;
}
