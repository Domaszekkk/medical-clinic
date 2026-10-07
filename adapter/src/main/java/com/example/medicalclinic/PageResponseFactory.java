package com.example.medicalclinic;

import java.util.List;
import java.util.function.Function;

public final class PageResponseFactory {

    private PageResponseFactory() {
    }

    public static <T, R> PageResponse<R> from(Page<T> page, Function<T, R> mapper) {
        List<R> content = page.getContent().stream()
                .map(mapper)
                .toList();
        return PageResponse.<R>builder()
                .content(content)
                .pageNumber(page.getPageNumber())
                .pageSize(page.getPageSize())
                .totalElements(page.getTotalElements())
                .totalPages(page.getTotalPages())
                .build();
    }
}
