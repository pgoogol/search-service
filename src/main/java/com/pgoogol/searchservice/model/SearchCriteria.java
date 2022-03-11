package com.pgoogol.searchservice.model;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.autoconfigure.data.web.SpringDataWebProperties;

@Getter
@Setter
public class SearchCriteria<T> {

    @Schema(description = "Pageable")
    private Page pageable = new Page();
    @Schema(description = "Search criteria")
    private T criteria;

}
