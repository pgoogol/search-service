package com.pgoogol.searchservice.model;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.autoconfigure.data.web.SpringDataWebProperties;

import java.util.Map;

@Getter
@Setter
public class SearchCriteria {

    @Schema(description = "Pageable")
    private Page pageable = new Page();
    @Schema(description = "Search criteria")
    private Map<String, String> criteriaMap;

}
