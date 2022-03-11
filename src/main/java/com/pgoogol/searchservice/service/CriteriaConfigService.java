package com.pgoogol.searchservice.service;

import co.elastic.clients.elasticsearch._types.query_dsl.Query;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.fasterxml.jackson.databind.type.CollectionType;
import com.pgoogol.searchservice.model.configurationcriteria.CriteriaConfig;
import com.pgoogol.searchservice.model.configurationcriteria.ListType;
import jakarta.json.Json;
import jakarta.json.JsonValue;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static java.util.stream.Collectors.toMap;

@Service
public class CriteriaConfigService {

    private final String fileLocation;
    private Map<String, CriteriaConfig> criteriaConfigMap = new HashMap<>();
    private Map<ListType, Set<String>> matchingQueryMap = new HashMap<>();

    public CriteriaConfigService(@Value("${com.pgoogol.searchservice.criteria-config.file}") String fileLocation) {
        this.fileLocation = fileLocation;
    }

    @SneakyThrows
    @PostConstruct
    public void init() {
        File file = new File(fileLocation);
        if (file.exists()) {
            ObjectMapper objectMapper = new ObjectMapper();
            CollectionType collectionType = objectMapper.getTypeFactory().constructCollectionType(List.class, CriteriaConfig.class);
            List<CriteriaConfig> criteriaConfigList = objectMapper.readValue(file, collectionType);
            criteriaConfigMap = prepareCriteriaConfigMap(criteriaConfigList);
        }
    }

    @SneakyThrows
    public Map<String, CriteriaConfig> createConfigCriteria(List<CriteriaConfig> criteriaConfigList) {
        writeToFile(criteriaConfigList);
        return prepareCriteriaConfigMap(criteriaConfigList);
    }

    public Optional<CriteriaConfig> getCriteriaConfig(String key) {
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

    private void writeToFile(List<CriteriaConfig> criteriaConfigList) throws Exception {
        File file = new File(fileLocation);
        if (!file.exists() && !file.createNewFile()) {
            throw new Exception();
        }
        try(BufferedWriter out = new BufferedWriter(new FileWriter(file,false))){
            ObjectMapper objectMapper = new ObjectMapper();
            String json = objectMapper.writeValueAsString(criteriaConfigList);
            out.write(json);
            out.newLine();
        }
    }

    private Map<String, CriteriaConfig> prepareCriteriaConfigMap(List<CriteriaConfig> criteriaConfigList) {
        criteriaConfigMap = criteriaConfigList.stream().filter(criteriaConfig -> criteriaConfig.getField() != null && !Query.Kind.Nested.equals(criteriaConfig.getQueryKind())).collect(toMap(CriteriaConfig::getField, Function.identity()));
        matchingQueryMap = criteriaConfigList.stream().collect(Collectors.groupingBy(CriteriaConfig::getListType, Collectors.mapping(CriteriaConfig::getIndexField, Collectors.toSet())));

        return criteriaConfigMap;
    }

    public List<CriteriaConfig> getConfigCriteria() {
        return new ArrayList<>(criteriaConfigMap.values());
    }
}
