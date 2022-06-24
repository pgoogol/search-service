package com.pgoogol.searchservice.enums;

public enum CriteriaConfigType {

    ELASTICSEARCH,
    FILE;

    public boolean isEqual(String type) {
        return this.name().equalsIgnoreCase(type);
    }
}
