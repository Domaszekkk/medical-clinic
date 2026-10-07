package com.example.medicalclinic;

import java.util.List;
import java.util.function.Function;

public final class PageFactory {

    private PageFactory() {
    }

    public static Pageable toPageable(org.springframework.data.domain.Pageable pageable) {
        return Pageable.builder()
                .pageNumber(pageable.getPageNumber())
                .pageSize(pageable.getPageSize())
                .build();
    }

    public static <T, R> Page<R> from(org.springframework.data.domain.Page<T> page, Function<T, R> mapper) {
        List<R> content = page.getContent().stream()
                .map(mapper)
                .toList();
        return Page.<R>builder()
                .content(content)
                .pageNumber(page.getNumber())
                .pageSize(page.getSize())
                .totalElements(page.getTotalElements())
                .totalPages(page.getTotalPages())
                .build();
    }
}
