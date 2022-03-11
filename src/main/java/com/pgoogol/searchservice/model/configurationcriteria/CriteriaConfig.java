package com.pgoogol.searchservice.model.configurationcriteria;

import co.elastic.clients.elasticsearch._types.query_dsl.Query;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotBlank;

@Getter
@Setter
public class CriteriaConfig {

    public static final ListType FILTER = ListType.FILTER;
    public static final Query.Kind WILDCARD = Query.Kind.Wildcard;

    @Schema(description = "Field from Search criteria", example = "id")
    @NotBlank
    private String field;
    @Schema(description = "Field from Elasticsearch index", example = "id", required = true)
    @NotBlank
    private String indexField;
    @Schema(description = "Type of matching query", example = "FILTER")
    private ListType listType = FILTER;
    @Schema(description = "Query variant kinds", example = "Wildcard")
    private Query.Kind queryKind = WILDCARD;

}
