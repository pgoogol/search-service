package com.pgoogol.searchservice.service;

import com.pgoogol.searchservice.enums.CriteriaConfigType;
import com.pgoogol.searchservice.model.dictionary.enums.ListType;
import com.pgoogol.searchservice.model.dictionary.model.SearchConfig;

import java.util.List;
import java.util.Optional;

public interface CriteriaConfigStrategy {

    CriteriaConfigType getType();
    void prepareCriteriaConfig(String dictionaryCode);
    List<SearchConfig> getConfigCriteria(String dictionaryCode);
    Optional<SearchConfig> getCriteriaConfig(String key);
    ListType getListTypeByField(String field);
    String getIndexFieldFromCriteriaConfig(String key);
    String getIndexName();

}
