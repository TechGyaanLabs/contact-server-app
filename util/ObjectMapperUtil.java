package com.careerit.cbook.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import lombok.experimental.UtilityClass;

@UtilityClass
public class ObjectMapperUtil {
    
    private static final ObjectMapper objectMapper = new ObjectMapper();
    
    static {
        // Register JavaTimeModule to handle LocalDate serialization/deserialization
        objectMapper.registerModule(new JavaTimeModule());
    }
    
    /**
     * Convert any object to another class using ObjectMapper
     * @param source Source object
     * @param targetClass Target class
     * @return Converted object
     */
    public static <T> T convert(Object source, Class<T> targetClass) {
        try {
            return objectMapper.convertValue(source, targetClass);
        } catch (Exception e) {
            throw new RuntimeException("Failed to convert object to " + targetClass.getSimpleName(), e);
        }
    }
    
    /**
     * Convert object to JSON string
     * @param object Object to convert
     * @return JSON string
     */
    public static String toJson(Object object) {
        try {
            return objectMapper.writeValueAsString(object);
        } catch (Exception e) {
            throw new RuntimeException("Failed to convert object to JSON", e);
        }
    }
    
    /**
     * Convert JSON string to object
     * @param json JSON string
     * @param targetClass Target class
     * @return Converted object
     */
    public static <T> T fromJson(String json, Class<T> targetClass) {
        try {
            return objectMapper.readValue(json, targetClass);
        } catch (Exception e) {
            throw new RuntimeException("Failed to convert JSON to " + targetClass.getSimpleName(), e);
        }
    }
}
