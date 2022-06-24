package com.pgoogol.searchservice.service;

import co.elastic.clients.elasticsearch._types.query_dsl.Query;
import com.pgoogol.searchservice.config.properties.CriteriaConfigProperties;
import com.pgoogol.searchservice.model.dictionary.enums.ListType;
import com.pgoogol.searchservice.model.dictionary.model.SearchConfig;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.toMap;

public abstract class AbstractCriteriaConfigStrategy implements CriteriaConfigStrategy {

    protected String indexName;

    protected Map<String, SearchConfig> criteriaConfigMap = new HashMap<>();
    protected Map<ListType, Set<String>> matchingQueryMap = new HashMap<>();

    protected final CriteriaConfigProperties criteriaConfigProperties;

    protected AbstractCriteriaConfigStrategy(CriteriaConfigProperties criteriaConfigProperties) {
        this.criteriaConfigProperties = criteriaConfigProperties;
    }

    protected void prepareCriteriaConfig(List<SearchConfig> criteriaConfigList) {
        prepareCriteriaConfigMap(criteriaConfigList);
        matchingQueryMap = criteriaConfigList.stream()
            .collect(
                Collectors.groupingBy(
                    SearchConfig::getListType,
                    Collectors.mapping(SearchConfig::getIndexField, Collectors.toSet())
                )
            );
    }

    protected void prepareCriteriaConfigMap(List<SearchConfig> criteriaConfigList) {
        criteriaConfigMap = criteriaConfigList.stream()
            .filter(criteriaConfig -> criteriaConfig.getField() != null && !Query.Kind.Nested.equals(criteriaConfig.getQueryKind()))
            .collect(toMap(SearchConfig::getField, Function.identity()));
    }

    public Optional<SearchConfig> getCriteriaConfig(String key) {
        return Optional.ofNullable(criteriaConfigMap.get(key));
    }

    public ListType getListTypeByField(String field) {
        return matchingQueryMap.entrySet()
                .stream()
                .filter(listTypeSetEntry -> listTypeSetEntry.getValue().contains(field))
                .findFirst()
                .map(Map.Entry::getKey)
                .orElse(ListType.FILTER);
    }

    public  String getIndexFieldFromCriteriaConfig(String key) {
        Optional<SearchConfig> criteriaConfig = this.getCriteriaConfig(key);
        if (criteriaConfig.isPresent()) {
            return criteriaConfig.get().getIndexField();
        }
        return key;
    }

    @Override
    public String getIndexName() {
        return indexName;
    }

}
