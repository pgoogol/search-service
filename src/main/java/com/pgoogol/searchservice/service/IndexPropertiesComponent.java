package com.pgoogol.searchservice.service;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch._types.ElasticsearchException;
import co.elastic.clients.elasticsearch._types.mapping.NestedProperty;
import co.elastic.clients.elasticsearch._types.mapping.ObjectProperty;
import co.elastic.clients.elasticsearch._types.mapping.Property;
import co.elastic.clients.elasticsearch._types.query_dsl.BoolQuery;
import co.elastic.clients.transport.TransportException;
import com.pgoogol.searchservice.exception.ResourceNotFoundException;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

@Component
public class IndexPropertiesComponent {

    private static final String DOT = ".";

    private final ElasticsearchClient client;
    private final CriteriaConfigStrategy criteriaConfigService;

    public IndexPropertiesComponent(ElasticsearchClient client, List<CriteriaConfigStrategy> criteriaConfigStrategies,
                                    @Value("${com.pgoogol.searchservice.criteria-config.type}") String criteriaType) {
        this.criteriaConfigService = criteriaConfigStrategies
                .stream()
                .filter(criteriaConfigType -> criteriaConfigType.getType().isEqual(criteriaType))
                .findFirst()
                .orElseThrow(() -> new ResourceNotFoundException(""));
        this.client = client;
    }

    @SneakyThrows({ElasticsearchException.class, TransportException.class, IOException.class})
    public Map<String, BoolQuery.Builder> initProperties(Set<String> serarchCriteriaKeys) {
        Map<String, Property> properties = Objects.requireNonNull(client.indices()
                        .getMapping(builder -> builder.index(criteriaConfigService.getIndexName()))
                        .get(criteriaConfigService.getIndexName()))
                .mappings()
                .properties();

        Map<String, BoolQuery.Builder> nestedMap = new LinkedHashMap<>();

        serarchCriteriaKeys.stream()
                .map(criteriaConfigService::getIndexFieldFromCriteriaConfig)
                .forEach(value -> {
                    if (value.contains(DOT)) {
                        String[] split = value.split("\\.");
                        StringBuilder path = new StringBuilder(split[0]);
                        prepareNestedMap(
                                skipFirst(split),
                                path,
                                properties.get(split[0]),
                                nestedMap
                        );
                    }
                });
        return nestedMap;
    }

    private void prepareNestedMap(String[] splittedKeysWithoutFirst, StringBuilder path, Property property, Map<String, BoolQuery.Builder> nestedMap) {
        if (property != null && property.isNested()) {
            nestedMap.putIfAbsent(path.toString(), new BoolQuery.Builder());
        }
        if (splittedKeysWithoutFirst.length != 0 && property != null) {
            if (property._get() instanceof NestedProperty) {
                property = ((NestedProperty) property._get()).properties().get(splittedKeysWithoutFirst[0]);
            } else if (property._get() instanceof ObjectProperty) {
                property = ((ObjectProperty) property._get()).properties().get(splittedKeysWithoutFirst[0]);
            }
            path.append(DOT).append(splittedKeysWithoutFirst[0]);
            prepareNestedMap(skipFirst(splittedKeysWithoutFirst), path, property, nestedMap);
        }
    }

    private String[] skipFirst(String[] splittedString) {
        return Arrays.stream(splittedString).skip(1).toArray(String[]::new);
    }

}
