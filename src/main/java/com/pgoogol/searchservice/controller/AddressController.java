package com.pgoogol.searchservice.controller;

import com.pgoogol.searchservice.model.AddressSearchCriteria;
import com.pgoogol.searchservice.model.AdressesReadEntity;
import com.pgoogol.searchservice.model.SearchCriteria;
import com.pgoogol.searchservice.service.AddressService;
import io.swagger.v3.oas.annotations.Operation;
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

@RestController
@Tag(name = "address", description = "Address API")
public class AddressController {

    private final AddressService addressService;

    public AddressController(AddressService addressService) {
        this.addressService = addressService;
    }

    @Operation(summary = "Find all Addresses", tags = { "address" })
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successful operation",
                    content = @Content(array = @ArraySchema(schema = @Schema(implementation = AdressesReadEntity.class)))) })
    @PostMapping("/search/all")
    public ResponseEntity searchAll() {
        return ResponseEntity.ok(addressService.searchAll());
    }

    @Operation(summary = "Find addresses by search criteria", tags = { "address" })
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successful operation",
                    content = @Content(array = @ArraySchema(schema = @Schema(implementation = AdressesReadEntity.class)))) })
    @PostMapping("/search")
    public ResponseEntity search(@RequestBody SearchCriteria<Object> searchCriteria) {
        return ResponseEntity.ok(addressService.search(searchCriteria));
    }

    @Operation(summary = "Autocompleat (Not Working)", tags = { "address" })
    @GetMapping("/autocomplete")
    public ResponseEntity autocomplete() {
        return null;
    }



}
