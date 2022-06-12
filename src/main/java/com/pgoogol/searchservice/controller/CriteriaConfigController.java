package com.pgoogol.searchservice.controller;

import com.pgoogol.dictionary.client.model.SearchConfig;
import com.pgoogol.searchservice.enums.CriteriaConfigType;
import com.pgoogol.searchservice.service.CriteriaConfigStrategy;
import com.pgoogol.searchservice.service.FileCriteriaConfigStrategy;
import com.pgoogol.searchservice.service.ResourceNotFoundException;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.annotation.RequestScope;

import java.util.Collection;
import java.util.List;

@RestController
@RequestMapping("criteria")
@RequestScope
@Tag(name = "criteria", description = "Criteria API")
public class CriteriaConfigController {

    private final CriteriaConfigStrategy criteriaConfigService;
    private final String criteriaType;

    public CriteriaConfigController(List<CriteriaConfigStrategy> criteriaConfigStrategies,
                                    @Value("${com.pgoogol.searchservice.criteria-config.type}") String criteriaType
    ) {
        this.criteriaConfigService = criteriaConfigStrategies
                .stream()
                .filter(criteriaConfigType -> criteriaConfigType.getType().isEqual(criteriaType))
                .findFirst()
                .orElseThrow(() -> new ResourceNotFoundException(""));
        this.criteriaType = criteriaType;
    }

    @Operation(summary = "Get Configuration For Criteria", tags = { "criteria" })
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successful operation",
                    content = @Content(array = @ArraySchema(schema = @Schema(implementation = SearchConfig.class)))) })
    @GetMapping("config")
    public ResponseEntity<List<SearchConfig>> getSearchConfig(
            @Parameter(
                    description = "Dictionary Code",
                    schema = @Schema(implementation = String.class)
            ) @RequestParam (name = "dictionaryCode", required = false) String dictionaryCode
    ) {
        return ResponseEntity.ok(criteriaConfigService.getConfigCriteria(dictionaryCode));
    }

    @Operation(summary = "Set Configuration For Criteria", tags = { "criteria" })
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successful operation",
                    content = @Content(array = @ArraySchema(schema = @Schema(implementation = SearchConfig.class)))) })
    @PostMapping("config")
    public ResponseEntity<Collection<SearchConfig>> createSearchConfig(
            @Parameter(
                    description = "Configuration criteria",
                    array = @ArraySchema(schema = @Schema(implementation = SearchConfig.class))
            ) @RequestBody List<SearchConfig> searchConfigList) {
        if (CriteriaConfigType.FILE.isEqual(criteriaType)) {
            return ResponseEntity.ok(
                    ((FileCriteriaConfigStrategy) criteriaConfigService)
                            .createConfigCriteria(searchConfigList).values()
            );
        }
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
    }

}
