package com.pgoogol.searchservice.model.dictionary.model;

import co.elastic.clients.elasticsearch._types.query_dsl.Query;
import com.pgoogol.searchservice.model.dictionary.enums.ListType;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotBlank;

@Getter
@Setter
public class SearchConfig {

    public static final ListType FILTER = ListType.FILTER;
    public static final Query.Kind WILDCARD = Query.Kind.Wildcard;

    @Schema(description = "Field in request")
    @NotBlank
    private String field;

    @Schema(description = "Field in ElasticSearch")
    @NotBlank
    private String indexField;

    @Schema(description = "Search type")
    private ListType listType = FILTER;

    @Schema(description = "Query Kind Type")
    private Query.Kind queryKind = WILDCARD;


}
