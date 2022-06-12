package com.pgoogol.searchservice.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.CollectionType;
import com.pgoogol.dictionary.client.model.SearchConfig;
import com.pgoogol.searchservice.config.properties.CriteriaConfigProperties;
import com.pgoogol.searchservice.enums.CriteriaConfigType;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Service;
import org.springframework.web.context.annotation.RequestScope;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static com.pgoogol.searchservice.enums.CriteriaConfigType.FILE;

@Service
@RequestScope
public class FileCriteriaConfigStrategy extends AbstractCriteriaConfigStrategy {

    public FileCriteriaConfigStrategy(
            @Value("com.pgoogol.searchservice.criteria-config.index-name") String indexName,
            CriteriaConfigProperties criteriaConfigProperties
    ) {
        super(criteriaConfigProperties);
        super.indexName = indexName;
    }

    @Override
    public CriteriaConfigType getType() {
        return FILE;
    }

    @SneakyThrows({IOException.class})
    @Override
    public void prepareCriteriaConfig(@Nullable String dictionaryCode) {
        File file = new File(criteriaConfigProperties.getFile());
        if (file.exists()) {
            ObjectMapper objectMapper = new ObjectMapper();
            CollectionType collectionType = objectMapper.getTypeFactory().constructCollectionType(List.class, SearchConfig.class);
            List<SearchConfig> criteriaConfigList = objectMapper.readValue(file, collectionType);
            indexName = criteriaConfigProperties.getIndexName();
            prepareCriteriaConfig(criteriaConfigList);
        }
    }

    @SneakyThrows({IOException.class})
    @Override
    public List<SearchConfig> getConfigCriteria(@Nullable String dictionaryCode) {
        File file = new File(criteriaConfigProperties.getFile());
        if (file.exists()) {
            ObjectMapper objectMapper = new ObjectMapper();
            CollectionType collectionType = objectMapper.getTypeFactory().constructCollectionType(List.class, SearchConfig.class);
            List<SearchConfig> criteriaConfigList = objectMapper.readValue(file, collectionType);
            prepareCriteriaConfigMap(criteriaConfigList);
        }
        return new ArrayList<>(criteriaConfigMap.values());
    }

    public Map<String, SearchConfig> createConfigCriteria(List<SearchConfig> criteriaConfigList) {
        writeToFile(criteriaConfigList);
        prepareCriteriaConfig(criteriaConfigList);
        return criteriaConfigMap;
    }

    @SneakyThrows(IOException.class)
    private void writeToFile(List<SearchConfig> criteriaConfigList) {
        File file = new File(criteriaConfigProperties.getFile());
        if (!file.exists() && !file.createNewFile()) {
            throw new IOException("File Cannot Created");
        }
        try (BufferedWriter out = new BufferedWriter(new FileWriter(file, false))) {
            ObjectMapper objectMapper = new ObjectMapper();
            String json = objectMapper.writeValueAsString(criteriaConfigList);
            out.write(json);
            out.newLine();
        }
    }

}
