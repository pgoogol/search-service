package com.pgoogol.searchservice.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.fasterxml.jackson.databind.type.CollectionType;
import com.pgoogol.searchservice.model.configurationcriteria.CriteriaConfig;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.function.Function;

import static java.util.stream.Collectors.toMap;

@Service
public class CriteriaConfigServiceFirst {

   /* private final String fileLocation;
    private Map<String, CriteriaConfig> criteriaConfigMap = new HashMap<>();

    public CriteriaConfigServiceFirst(@Value("${com.pgoogol.searchservice.criteria-config.file}") String fileLocation) {
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
        Map<String, CriteriaConfig> configMap = prepareCriteriaConfigMap(criteriaConfigList);
        criteriaConfigMap = configMap;
        return configMap;
    }

    public Optional<CriteriaConfig> getCriteriaConfig(String key) {
        return Optional.ofNullable(criteriaConfigMap.get(key));
    }

    private void writeToFile(List<CriteriaConfig> criteriaConfigList) throws IOException {
        ObjectWriter ow = new ObjectMapper().writer().withDefaultPrettyPrinter();
        File file = new File(fileLocation);
        ow.writeValue(file, criteriaConfigList);
    }

    private Map<String, CriteriaConfig> prepareCriteriaConfigMap(List<CriteriaConfig> criteriaConfigList) {
        return criteriaConfigList.stream().collect(toMap(CriteriaConfig::getField, Function.identity()));
    }

    public List<CriteriaConfig> getConfigCriteria() {
        return new ArrayList<>(criteriaConfigMap.values());
    }*/
}
