package com.pgoogol.searchservice.model.dictionary.model;

import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

import java.util.Collections;
import java.util.List;

@Getter
@Setter
public class ModelDictionary {

    @Schema(description = "Field name")
    private String field;

    @Schema(description = "Field type")
    private String fieldType;

    @Schema(description = "Field label")
    private String label;

    @Schema(description = "Is Required?")
    private boolean required;

    @ArraySchema(arraySchema = @Schema(description = "Array SubModel(ModelDictionary.class)", implementation = ModelDictionary.class))
    private List<ModelDictionary> items = Collections.emptyList();

    @ArraySchema(arraySchema = @Schema(description = "SubModel(ModelDictionary.class)", implementation = ModelDictionary.class))
    private List<ModelDictionary> properties = Collections.emptyList();

}
