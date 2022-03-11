package com.pgoogol.searchservice.service;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch._types.ElasticsearchException;
import co.elastic.clients.elasticsearch._types.mapping.NestedProperty;
import co.elastic.clients.elasticsearch._types.mapping.ObjectProperty;
import co.elastic.clients.elasticsearch._types.mapping.Property;
import co.elastic.clients.elasticsearch._types.query_dsl.BoolQuery;
import co.elastic.clients.elasticsearch._types.query_dsl.Query;
import co.elastic.clients.elasticsearch.core.SearchResponse;
import co.elastic.clients.elasticsearch.core.search.Hit;
import com.pgoogol.searchservice.model.ResultPage;
import com.pgoogol.searchservice.model.SearchCriteria;
import com.pgoogol.searchservice.model.configurationcriteria.CriteriaConfig;
import com.pgoogol.searchservice.model.configurationcriteria.ListType;
import lombok.SneakyThrows;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

import static com.pgoogol.searchservice.model.Page.getTotalPages;

public class BaseService<T> {

    private final Logger log = LogManager.getLogger(BaseService.class);
    private static final String DOT = ".";
    private final ElasticsearchClient client;
    private final CriteriaConfigService criteriaConfigService;
    private final String indexName;
    private final Class<T> clazz;
    protected final Map<String, String> translations = new HashMap<>();

    public BaseService(ElasticsearchClient client, CriteriaConfigService criteriaConfigService, String indexName, Class<T> clazz) {
        this.client = client;
        this.criteriaConfigService = criteriaConfigService;
        this.indexName = indexName;
        this.clazz = clazz;
    }

    public List<T> searchAll() {
        List<T> search = null;
        try {
            SearchResponse<T> searchResponse = client.search(builder -> builder.index(indexName).trackTotalHits(builder1 -> builder1.enabled(true)), clazz);
            search = getSource(searchResponse);
        } catch (IOException | ElasticsearchException e) {
            log.error("get error");
        }
        return search;
    }

    @SneakyThrows
    public ResultPage<T> search(SearchCriteria<T> searchCriteria) {
        Map<String, String> searchCriteriaMap = ObjectMapperUtils.convertToMapWithValue(searchCriteria.getCriteria());
        Map<String, BoolQuery.Builder> nestedMap = new LinkedHashMap<>();
        initProperties(searchCriteriaMap.keySet(), nestedMap);

        List<Query> searchCollection = searchCriteriaMap.entrySet().stream().map(es -> {
            Optional<CriteriaConfig> criteriaConfigOptional = criteriaConfigService.getCriteriaConfig(es.getKey());
            if (criteriaConfigOptional.isPresent()) {
                CriteriaConfig criteriaConfig = criteriaConfigOptional.get();
                return QueryFactory.createQuery(criteriaConfig.getQueryKind(), criteriaConfig.getIndexField(), es.getValue());
            } else {
                return QueryFactory.createQuery(CriteriaConfig.WILDCARD, es.getKey(), es.getValue());
            }
        }).collect(Collectors.toList());

        List<Query> nestedQueries = searchCollection.stream().filter(query -> this.verifyNested(query, nestedMap)).collect(Collectors.toList());
        searchCollection.removeAll(nestedQueries);

        BoolQuery.Builder boolQuery = new BoolQuery.Builder();
        searchCollection.forEach(query -> {
            String field = QueryFactory.getField(query);
            ListType listType = criteriaConfigService.getListTypeByField(field);
            QueryFactory.addToMatchingQuery(query, boolQuery, listType);
        });

        if (!nestedQueries.isEmpty()) {
            joinNestedMap(boolQuery, nestedMap);
        }

        SearchResponse<T> searchResponse = null;
        try {
            searchResponse = client.search(
                    builder -> builder.index(indexName)
                            .trackTotalHits(builder1 -> builder1.enabled(true))
                            .from(searchCriteria.getPageable().getFrom())
                            .size(searchCriteria.getPageable().getSize())
                            .query(builder1 -> builder1.bool(boolQuery.build())),
                    clazz
            );
        } catch (IOException | ElasticsearchException e) {
            log.error("get error");
        }
        return getResultPage(searchCriteria, searchResponse);
    }

    private ResultPage<T> getResultPage(SearchCriteria<T> searchCriteria, SearchResponse<T> search) {
        return ResultPage.<T>builder()
                .pageable(searchCriteria.getPageable())
                .totalElements(search.hits().total().value())
                .totalPages(getTotalPages(search.hits().total().value(), searchCriteria.getPageable().getSize()))
                .data(getSource(search))
                .build();
    }


    @SneakyThrows
    public void initProperties(Set<String> strings, Map<String, BoolQuery.Builder> nestedMap) {
        Map<String, Property> properties = client.indices().getMapping(builder -> builder.index(indexName)).get(indexName).mappings().properties();

        strings.stream().map(s -> {
            Optional<CriteriaConfig> criteriaConfigOptional = criteriaConfigService.getCriteriaConfig(s);
            if (criteriaConfigOptional.isPresent()) {
                return criteriaConfigOptional.get().getIndexField();
            } else {
                return s;
            }
        }).forEach(value -> {
            if (value.contains(".")) {
                String[] split = value.split("\\.");
                StringBuilder path = new StringBuilder(split[0]);
                extracted(Arrays.stream(split).skip(1).toArray(String[]::new), path, properties.get(split[0]), nestedMap);
            }
        });
    }

    private void extracted(String[] split, StringBuilder path, Property property, Map<String, BoolQuery.Builder> nestedMap) {
        if (property != null && property.isNested()) {
            nestedMap.putIfAbsent(path.toString(), new BoolQuery.Builder());
        }
        if (split.length != 0) {
            path.append(DOT).append(split[0]);
            if (property._get() instanceof NestedProperty) {
                property = ((NestedProperty) property._get()).properties().get(split[0]);
            } else if (property._get() instanceof ObjectProperty) {
                property = ((ObjectProperty) property._get()).properties().get(split[0]);
            }
            extracted(Arrays.stream(split).skip(1).toArray(String[]::new), path, property, nestedMap);
        }
    }

    private boolean verifyNested(Query query, Map<String, BoolQuery.Builder> nestedMap) {
        String field = QueryFactory.getField(query);
        String[] split = field.split("\\.");
        for (int i = split.length - 1; i > 0; i--) {
            String join = String.join(".", Arrays.asList(split).subList(0, i));
            BoolQuery.Builder builder1 = nestedMap.computeIfPresent(
                    join,
                    (key, value) -> {
                        String fieldFromQuery = QueryFactory.getField(query);
                        ListType listType = criteriaConfigService.getListTypeByField(fieldFromQuery);
                        QueryFactory.addToMatchingQuery(query, value, listType);
                        return value;
                    });
            if (builder1 != null) {
                return true;
            }
        }
        return false;
    }

    private void joinNestedMap(BoolQuery.Builder boolQuery, Map<String, BoolQuery.Builder> nestedMap) {
        if (nestedMap.size() == 1) {
            Map.Entry<String, BoolQuery.Builder> query = nestedMap.entrySet().iterator().next();
            Query nested = new Query.Builder().nested(builder1 -> builder1.path(query.getKey()).query(query.getValue().build()._toQuery())).build();
            ListType listType = criteriaConfigService.getListTypeByField(query.getKey());
            QueryFactory.addToMatchingQuery(nested, boolQuery, listType);
            return;
        }
        List<String> list = new LinkedList<>(nestedMap.keySet());
        Collections.reverse(list);
        list.forEach(field -> {
            BoolQuery builder11 = nestedMap.get(field).build();
            String[] split = field.split("\\.");
            if (split.length > 1 && isNotEmptyQuery(builder11)) {
                for (int i = split.length - 1; i > 0; i--) {
                    String join = String.join(".", Arrays.asList(split).subList(0, i));
                    if (nestedMap.containsKey(join)) {
                        BoolQuery.Builder builder = nestedMap.getOrDefault(join, new BoolQuery.Builder());
                        Query nested = new Query.Builder().nested(builder2 -> builder2.path(field).query(builder3 -> builder3.bool(builder11))).build();
                        ListType listType = criteriaConfigService.getListTypeByField(field);
                        QueryFactory.addToMatchingQuery(nested, builder, listType);
                        nestedMap.put(join, builder);
                        nestedMap.remove(field);
                        break;
                    }
                }
            } else if (split.length == 1) {
                Query nested = new Query.Builder().nested(builder1 -> builder1.path(field).query(builder2 -> builder2.bool(builder11))).build();
                ListType listType = criteriaConfigService.getListTypeByField(field);
                QueryFactory.addToMatchingQuery(nested, boolQuery, listType);
            }
        });
    }

    private boolean isNotEmptyQuery(BoolQuery query) {
        return !(query.filter().isEmpty() && query.must().isEmpty() && query.mustNot().isEmpty() && query.should().isEmpty());
    }

    protected final List<T> getSource(SearchResponse<T> searchResponse) {
        return searchResponse.hits().hits().stream().map(Hit::source).collect(Collectors.toList());
    }

}
