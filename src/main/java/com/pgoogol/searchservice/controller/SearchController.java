package com.pgoogol.searchservice.controller;

import com.pgoogol.searchservice.model.ResultPage;
import com.pgoogol.searchservice.model.SearchCriteria;
import com.pgoogol.searchservice.service.ElasticsearchRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.constraints.NotNull;

@RestController
@RequestMapping("/search")
@Tag(name = "Search", description = "Search API")
public class SearchController {

    private final ElasticsearchRepository repository;

    public SearchController(ElasticsearchRepository repository) {
        this.repository = repository;
    }

    @PostMapping("{dictionaryCode}")
    @Operation(summary = "Get All Data from Dictionary By Search criteria", tags = {"Search"})
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Successful operation"
            )
    })
    public ResponseEntity<ResultPage<Object>> getAll(
            @Parameter(
                    description = "Dictionary Code",
                    schema = @Schema(implementation = String.class)
            ) @PathVariable(name = "dictionaryCode") @NotNull String dictionaryCode,
            @Parameter(
                    description = "Search Criteria and Pagination",
                    schema = @Schema(implementation = SearchCriteria.class)
            ) @RequestBody SearchCriteria searchCriteria
    ) {
        return ResponseEntity.ok(repository.search(dictionaryCode, searchCriteria));
    }

    @Operation(summary = "Autocompleat (Not Working)", tags = { "address" })
    @GetMapping("/autocomplete")
    public ResponseEntity autocomplete() {
        return null;
    }

}
