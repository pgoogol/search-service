package com.pgoogol.searchservice.service;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.databind.ser.std.StringSerializer;
import com.pgoogol.searchservice.model.serializer.LocalDateSerializer;

import java.lang.reflect.Field;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

public interface ObjectMapperUtils {

    static Map<String, String> convertToMapWithValue(Object criteria) {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
        objectMapper.setSerializationInclusion(JsonInclude.Include.NON_EMPTY);
        objectMapper.registerModule(new SimpleModule().addSerializer(String.class, new StringSerializer())
                .addSerializer(LocalDate.class, new LocalDateSerializer())
        );
        Map<String, String> map = objectMapper.convertValue(criteria, new TypeReference<>() {
        });
        return Optional.ofNullable(map).orElse(new HashMap<>());
    }

    static List<String> convertClassToMap(Class<?> clazz) {
        return Arrays.stream(clazz.getDeclaredFields()).map(Field::getName).collect(Collectors.toList());
    }

}
