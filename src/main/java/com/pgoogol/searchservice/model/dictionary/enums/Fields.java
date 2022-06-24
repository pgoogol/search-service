package com.pgoogol.searchservice.model.dictionary.enums;

public enum Fields {

    INDEX_NAME("indexName"),
    MODEL_DICTIONARY("modelDictionary"),
    SEARCH_CONFIG("searchConfig");

    private final String name;

    Fields(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
