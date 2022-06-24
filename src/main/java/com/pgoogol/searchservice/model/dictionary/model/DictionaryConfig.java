package com.pgoogol.searchservice.model.dictionary.model;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotNull;
import java.util.List;

@Getter
@Setter
public class DictionaryConfig {

    @NotNull
    private String code;

    private String indexName;

    @NotNull
    private boolean isActive;

    private List<SearchConfig> searchConfig;

    private List<ModelDictionary> modelDictionary;

}


