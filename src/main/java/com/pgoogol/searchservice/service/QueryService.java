package com.pgoogol.searchservice.service;

import co.elastic.clients.elasticsearch._types.query_dsl.*;
import co.elastic.clients.json.JsonData;
import org.springframework.stereotype.Service;

import java.util.Arrays;

@Service
public class QueryService {

    private static final String QUERY_STRING_FORMAT = "%s:%s";

    public WildcardQuery prepareWildcard(String field, Object[] value) {
        return new WildcardQuery.Builder().queryName(getQueryName(field, value))
                                          .field(field)
                                          .value(String.valueOf(value[0]))
                                          .build();
    }

    public RangeQuery prepareRange(String field, Object[] value) {
        return new RangeQuery.Builder().queryName(getQueryName(field, value))
                                      .field(field).gte(JsonData.of(value[0]))
                                      .lte(JsonData.of(value[1]))
                                      .build();
    }

    public ExistsQuery prepareExists(String field) {
        return new ExistsQuery.Builder().queryName(getQueryName(field, null)).field(field).build();
    }

    public QueryStringQuery prepareQueryString(String field, Object[] value) {
        String query = String.format(QUERY_STRING_FORMAT, field, value[0]);
        return new QueryStringQuery.Builder().queryName(getQueryName(field, value))
                                             .query(query)
                                             .defaultOperator(Operator.And)
                                             .analyzeWildcard(true)
                                             .build();
    }

    private String getQueryName(String field, Object[] value) {
        StringBuilder queryName = new StringBuilder().append(field);
        if (value != null) {
            Arrays.asList(value).forEach(v -> queryName.append("_").append(v));
        }
        return queryName.toString();
    }

}
