package com.pgoogol.searchservice.service;

import co.elastic.clients.elasticsearch._types.query_dsl.BoolQuery;
import co.elastic.clients.elasticsearch._types.query_dsl.Query;
import co.elastic.clients.elasticsearch._types.query_dsl.QueryBase;
import com.pgoogol.searchservice.model.dictionary.enums.ListType;

public final class QueryFactory {

    private static final QueryService queryService = new QueryService();

    private QueryFactory() {}

    public static QueryBase createQueryBase(Query.Kind queryType, String field, Object... value) {
        switch (queryType) {
            case Wildcard:
                return queryService.prepareWildcard(field, value);
            case Range:
                return queryService.prepareRange(field, value);
            case Exists:
                return queryService.prepareExists(field);
            case QueryString:
                return queryService.prepareQueryString(field, value);
            default:
                return queryService.prepareWildcard(field, value);
        }
    }

    public static Query createQuery(Query.Kind queryType, String field, Object... value) {
        switch (queryType) {
            case Wildcard:
                return new Query.Builder().wildcard(queryService.prepareWildcard(field, value)).build();
            case Range:
                return new Query.Builder().range(queryService.prepareRange(field, value)).build();
            case Exists:
                return new Query.Builder().exists(queryService.prepareExists(field)).build();
            case QueryString:
                return new Query.Builder().queryString(queryService.prepareQueryString(field, value)).build();
            default:
                return new Query.Builder().wildcard(queryService.prepareWildcard(field, value)).build();
        }
    }

    public static String getField(Query query) {
        switch (query._kind()) {
            case Wildcard:
                return query.wildcard().field();
            case Range:
                return query.range().field();
            case Exists:
                return query.exists().field();
            case QueryString:
                return query.queryString().query().split(":")[0];
            default:
                return "";
        }
    }

    public static void addToMatchingQuery(Query query, BoolQuery.Builder boolQueryBuilder, ListType listType) {
        switch (listType) {
            case FILTER:
                boolQueryBuilder.filter(query);
                break;
            case MUST:
                boolQueryBuilder.must(query);
                break;
            case MUST_NOT:
                boolQueryBuilder.mustNot(query);
                break;
            case SHOULD:
                boolQueryBuilder.should(query);
                break;
            default:
        }
    }

}
