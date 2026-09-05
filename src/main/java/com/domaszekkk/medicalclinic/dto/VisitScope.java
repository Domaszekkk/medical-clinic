package com.domaszekkk.medicalclinic.dto;

import com.fasterxml.jackson.annotation.JsonCreator;

public enum VisitScope {
    PAST,
    UPCOMING,
    ALL;

    @JsonCreator
    public static VisitScope fromString(String value) {
        return VisitScope.valueOf(value.toUpperCase());
    }
}