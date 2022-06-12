package com.pgoogol.searchservice.service;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch._types.ElasticsearchException;
import co.elastic.clients.elasticsearch._types.mapping.NestedProperty;
import co.elastic.clients.elasticsearch._types.mapping.ObjectProperty;
import co.elastic.clients.elasticsearch._types.mapping.Property;
import co.elastic.clients.elasticsearch._types.query_dsl.BoolQuery;
import co.elastic.clients.elasticsearch._types.query_dsl.Query;
import co.elastic.clients.elasticsearch.core.SearchRequest;
import co.elastic.clients.elasticsearch.core.SearchResponse;
import co.elastic.clients.elasticsearch.core.search.Hit;
import co.elastic.clients.transport.TransportException;
import co.elastic.clients.util.ObjectBuilder;
import com.pgoogol.dictionary.client.enums.ListType;
import com.pgoogol.dictionary.client.model.SearchConfig;
import com.pgoogol.searchservice.model.ResultPage;
import com.pgoogol.searchservice.model.SearchCriteria;
import lombok.SneakyThrows;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;
import org.springframework.web.context.annotation.RequestScope;

import java.io.IOException;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

import static com.pgoogol.searchservice.model.Page.*;

@Repository
@RequestScope
public class ElasticsearchRepository {

    private static final Logger log = LogManager.getLogger(ElasticsearchRepository.class);
    private static final String DOT = ".";

    private final ElasticsearchClient client;
    private final CriteriaConfigStrategy criteriaConfigService;

    public ElasticsearchRepository(ElasticsearchClient client, List<CriteriaConfigStrategy> criteriaConfigStrategies,
                                   @Value("${com.pgoogol.searchservice.criteria-config.type}") String criteriaType) {
        this.criteriaConfigService = criteriaConfigStrategies
                .stream()
                .filter(criteriaConfigType -> criteriaConfigType.getType().isEqual(criteriaType))
                .findFirst()
                .orElseThrow(() -> new ResourceNotFoundException(""));
        this.client = client;
    }

    public ResultPage<Object> search(String dictionaryCode) {
        return this.search(dictionaryCode, null, Object.class);
    }

    public <T> ResultPage<T> search(String dictionaryCode, Class<T> clazz) {
        return this.search(dictionaryCode, null, clazz);
    }

    public ResultPage<Object> search(String dictionaryCode, SearchCriteria searchCriteria) {
        return this.search(dictionaryCode, searchCriteria, Object.class);
    }

    @SneakyThrows({ElasticsearchException.class, TransportException.class, IOException.class})
    public <T> ResultPage<T> search(String dictionaryCode, SearchCriteria searchCriteria, Class<T> clazz) {
        SearchResponse<T> searchResponse = null;
        Function<SearchRequest.Builder, ObjectBuilder<SearchRequest>> searchBuilder;
        if (searchCriteria == null) {
            searchCriteria = new SearchCriteria();
            searchBuilder = searchAll();
        } else {
            criteriaConfigService.prepareCriteriaConfig(dictionaryCode);
            Map<String, BoolQuery.Builder> nestedMap = new LinkedHashMap<>();
            initProperties(searchCriteria.getCriteriaMap().keySet(), nestedMap);

            List<Query> searchCollection = searchCriteria.getCriteriaMap().entrySet().stream().map(es -> {
                Optional<SearchConfig> criteriaConfigOptional = criteriaConfigService.getCriteriaConfig(es.getKey());
                if (criteriaConfigOptional.isPresent()) {
                    SearchConfig criteriaConfig = criteriaConfigOptional.get();
                    return QueryFactory.createQuery(criteriaConfig.getQueryKind(), criteriaConfig.getIndexField(), es.getValue());
                } else {
                    return QueryFactory.createQuery(SearchConfig.WILDCARD, es.getKey(), es.getValue());
                }
            }).collect(Collectors.toList());

            List<Query> nestedQueries = searchCollection.stream()
                    .filter(query -> this.verifyNested(query, nestedMap))
                    .collect(Collectors.toList());
            searchCollection.removeAll(nestedQueries);

            BoolQuery boolQuery = prepareBoolQuery(nestedMap, searchCollection, nestedQueries);

            searchBuilder = searchByCriteria(searchCriteria, boolQuery);
        }
        searchResponse = client.search(searchBuilder, clazz);
        return getResultPage(searchCriteria, searchResponse);
    }

    private BoolQuery prepareBoolQuery(Map<String, BoolQuery.Builder> nestedMap, List<Query> searchCollection, List<Query> nestedQueries) {
        BoolQuery.Builder boolQuery = new BoolQuery.Builder();
        searchCollection.forEach(query -> {
            String field = QueryFactory.getField(query);
            ListType listType = criteriaConfigService.getListTypeByField(field);
            QueryFactory.addToMatchingQuery(query, boolQuery, listType);
        });
        if (!nestedQueries.isEmpty()) {
            joinNestedMap(boolQuery, nestedMap);
        }
        return boolQuery.build();
    }

    //rename to 'prepareNested'
    private boolean verifyNested(Query query, Map<String, BoolQuery.Builder> nestedMap) {
        String field = QueryFactory.getField(query);
        String[] split = field.split("\\.");
        for (int i = split.length - 1; i > 0; i--) {
            String path = String.join(".", Arrays.asList(split).subList(0, i));
            BoolQuery.Builder boolQueryBuilder = nestedMap.computeIfPresent(path, (key, value) -> {
                ListType listType = criteriaConfigService.getListTypeByField(field);
                QueryFactory.addToMatchingQuery(query, value, listType);
                return value;
            });
            if (boolQueryBuilder != null) {
                return true;
            }
        }
        return false;
    }

    private void joinNestedMap(BoolQuery.Builder boolQueryBuilder, Map<String, BoolQuery.Builder> nestedMap) {
        if (nestedMap.size() == 1) {
            Map.Entry<String, BoolQuery.Builder> query = nestedMap.entrySet().iterator().next();
            String field = query.getKey();
            BoolQuery boolQuery = query.getValue().build();
            addQueryToBuilder(field, boolQuery, boolQueryBuilder);
            return;
        }
        List<String> list = new LinkedList<>(nestedMap.keySet());
        Collections.reverse(list);
        list.forEach(field -> {
            BoolQuery boolQuery = nestedMap.get(field).build();
            String[] split = field.split("\\.");
            if (split.length > 1 && isNotEmptyQuery(boolQuery)) {
                for (int i = split.length - 1; i > 0; i--) {
                    String join = String.join(".", Arrays.asList(split).subList(0, i));
                    if (nestedMap.containsKey(join)) {
                        BoolQuery.Builder builder = nestedMap.getOrDefault(join, new BoolQuery.Builder());
                        addQueryToBuilder(field, boolQuery, builder);
                        nestedMap.put(join, builder);
                        nestedMap.remove(field);
                        break;
                    }
                }
            } else if (split.length == 1) {
                addQueryToBuilder(field, boolQuery, boolQueryBuilder);
            }
        });
    }

    private void addQueryToBuilder(String field, BoolQuery boolQuery, BoolQuery.Builder boolQueryBuilder) {
        Query nested = new Query.Builder().nested(nb -> nb.path(field).query(q -> q.bool(boolQuery))).build();
        ListType listType = criteriaConfigService.getListTypeByField(field);
        QueryFactory.addToMatchingQuery(nested, boolQueryBuilder, listType);
    }

    private boolean isNotEmptyQuery(BoolQuery query) {
        return !(
                query.filter().isEmpty() && query.must().isEmpty() &&
                        query.mustNot().isEmpty() && query.should().isEmpty()
        );
    }
//as new component
    @SneakyThrows({ElasticsearchException.class, TransportException.class, IOException.class})
    public void initProperties(Set<String> serarchCriteriaKeys, Map<String, BoolQuery.Builder> nestedMap) {
        Map<String, Property> properties = client.indices()
                .getMapping(builder -> builder.index(criteriaConfigService.getIndexName()))
                .get(criteriaConfigService.getIndexName())
                .mappings()
                .properties();

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

    private Function<SearchRequest.Builder, ObjectBuilder<SearchRequest>> searchAll() {
        return builder -> builder.index(criteriaConfigService.getIndexName())
                .trackTotalHits(tth -> tth.enabled(true))
                .from(PAGE_NUMBER)
                .size(SIZE_NUMBER);
    }

    private Function<SearchRequest.Builder, ObjectBuilder<SearchRequest>> searchByCriteria(SearchCriteria searchCriteria, BoolQuery boolQuery) {
        return builder -> builder.index(criteriaConfigService.getIndexName())
                .trackTotalHits(tth -> tth.enabled(true))
                .from(searchCriteria.getPageable().getFrom())
                .size(searchCriteria.getPageable().getSize())
                .query(boolQuery._toQuery());
    }

    private <T> ResultPage<T> getResultPage(SearchCriteria searchCriteria, SearchResponse<T> search) {
        long totalElements = search.hits().total() != null ? search.hits().total().value() : 0;
        return ResultPage.<T>builder()
                .pageable(searchCriteria.getPageable())
                .totalElements(totalElements)
                .totalPages(
                        getTotalPages(
                                totalElements,
                                searchCriteria.getPageable().getSize()
                        )
                )
                .data(getSource(search))
                .build();
    }

    private <T> List<T> getSource(SearchResponse<T> searchResponse) {
        return searchResponse.hits()
                .hits()
                .stream()
                .map(Hit::source)
                .collect(Collectors.toList());
    }
}
