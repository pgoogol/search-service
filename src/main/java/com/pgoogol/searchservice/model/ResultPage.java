package com.pgoogol.searchservice.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
@NoArgsConstructor
@AllArgsConstructor
public class ResultPage<T> {

    private int totalPages = 0;
    private long totalElements = 0;
    private Page pageable = new Page();
    private List<T> data = new ArrayList<>();
}
