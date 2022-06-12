package com.pgoogol.searchservice.enums;

public enum CriteriaConfigType {

    ELASTICSEARCH(true),
    FILE(false);

    private final boolean isRequiredParams;

    CriteriaConfigType(boolean isRequiredParams) {
        this.isRequiredParams = isRequiredParams;
    }

    public boolean isEqual(String type) {
        return this.name().equalsIgnoreCase(type);
    }
}
