package com.pgoogol.searchservice.controller;

import com.pgoogol.searchservice.model.AdressesReadEntity;
import com.pgoogol.searchservice.model.configurationcriteria.CriteriaConfig;
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

    private final CriteriaConfigService criteriaConfigService;

    public CriteriaConfigController(CriteriaConfigService criteriaConfigService) {
        this.criteriaConfigService = criteriaConfigService;
    }

    @Operation(summary = "Get Configuration For Criteria", tags = { "criteria" })
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successful operation",
                    content = @Content(array = @ArraySchema(schema = @Schema(implementation = CriteriaConfig.class)))) })
    @GetMapping("/config")
    public ResponseEntity<List<CriteriaConfig>> getCriteriaConfig() {
        return ResponseEntity.ok(criteriaConfigService.getConfigCriteria());
    }

    @Operation(summary = "Set Configuration For Criteria", tags = { "criteria" })
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successful operation",
                    content = @Content(array = @ArraySchema(schema = @Schema(implementation = CriteriaConfig.class)))) })
    @PostMapping("/config")
    public ResponseEntity<Collection<CriteriaConfig>> createCriteriaConfig(
            @Parameter(
                    description = "Configuration criteria",
                    array = @ArraySchema(schema = @Schema(implementation = CriteriaConfig.class))
            ) @RequestBody List<CriteriaConfig> criteriaConfigList) {
        return ResponseEntity.ok(criteriaConfigService.createConfigCriteria(criteriaConfigList).values());
    }

}
