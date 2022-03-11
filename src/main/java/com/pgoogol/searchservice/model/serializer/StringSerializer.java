package com.pgoogol.searchservice.model.serializer;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import org.apache.commons.lang3.StringUtils;

import java.io.IOException;

public class StringSerializer extends JsonSerializer<String> {

    @Override
    public boolean isEmpty(SerializerProvider provider, String value) {
        return value != null && value.isBlank();
    }

    @Override
    public void serialize(String value, JsonGenerator jsonGenerator, SerializerProvider serializerProvider) throws IOException {
        if (!StringUtils.isEmpty(value)) {
            value = value.trim();
        }
        jsonGenerator.writeString(value);
    }
}
