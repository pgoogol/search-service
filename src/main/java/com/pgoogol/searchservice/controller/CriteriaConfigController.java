package com.pgoogol.searchservice.controller;

import com.pgoogol.dictionary.client.model.SearchConfig;
import com.pgoogol.searchservice.service.CriteriaConfigService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collection;
import java.util.List;

@RestController("/criteria")
@Tag(name = "criteria", description = "Criteria API")
public class CriteriaConfigController {

    private final CriteriaConfigService searchConfigService;

    public CriteriaConfigController(CriteriaConfigService searchConfigService) {
        this.searchConfigService = searchConfigService;
    }

    @Operation(summary = "Get Configuration For Criteria", tags = { "criteria" })
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successful operation",
                    content = @Content(array = @ArraySchema(schema = @Schema(implementation = SearchConfig.class)))) })
    @GetMapping("/config")
    public ResponseEntity<List<SearchConfig>> getSearchConfig() {
        return ResponseEntity.ok(searchConfigService.getConfigCriteria());
    }

    @Operation(summary = "Set Configuration For Criteria", tags = { "criteria" })
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successful operation",
                    content = @Content(array = @ArraySchema(schema = @Schema(implementation = SearchConfig.class)))) })
    @PostMapping("/config")
    public ResponseEntity<Collection<SearchConfig>> createSearchConfig(
            @Parameter(
                    description = "Configuration criteria",
                    array = @ArraySchema(schema = @Schema(implementation = SearchConfig.class))
            ) @RequestBody List<SearchConfig> searchConfigList) {
        return ResponseEntity.ok(searchConfigService.createConfigCriteria(searchConfigList).values());
    }

}
