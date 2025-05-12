package com.yogiBooking.common.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import java.io.IOException;

public class JsonUtil {

    // ObjectMapper instance is thread-safe and is configured to handle Java 8 time types.
    private static final ObjectMapper objectMapper;

    static {
        objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());  // Register the JavaTimeModule to handle Java 8 date/time types
        objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);  // Disable writing dates as timestamps
    }

    /**
     * Converts a Java object to a JSON string.
     *
     * @param object The Java object to convert to JSON.
     * @return A JSON string representing the given Java object.
     * @throws RuntimeException if there's an error during serialization.
     */
    public static String toJson(Object object) {
        return writeValueAsString(object, false);
    }

    /**
     * Converts a JSON string to a Java object of the specified class.
     *
     * @param json The JSON string to convert to a Java object.
     * @param clazz The class type to convert the JSON string to.
     * @param <T> The type of the Java object.
     * @return The Java object represented by the JSON string.
     * @throws RuntimeException if there's an error during deserialization.
     */
    public static <T> T fromJson(String json, Class<T> clazz) {
        try {
            return objectMapper.readValue(json, clazz);
        } catch (IOException e) {
            throw handleException("Error parsing JSON to object", e);
        }
    }

    /**
     * Converts a Java object to a pretty-printed (formatted) JSON string.
     *
     * @param object The Java object to convert to a formatted JSON.
     * @return A pretty-printed JSON string representing the given Java object.
     * @throws RuntimeException if there's an error during serialization.
     */
    public static String toPrettyJson(Object object) {
        return writeValueAsString(object, true);
    }

    /**
     * Helper method to serialize a Java object into a JSON string, with optional pretty printing.
     *
     * @param object The Java object to serialize.
     * @param pretty Whether to pretty-print the JSON.
     * @return A JSON string representing the Java object.
     * @throws RuntimeException if there's an error during serialization.
     */
    private static String writeValueAsString(Object object, boolean pretty) {
        try {
            return pretty
                    ? objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(object)
                    : objectMapper.writeValueAsString(object);
        } catch (JsonProcessingException e) {
            throw handleException("Error converting object to JSON", e);
        }
    }

    /**
     * Custom exception handler to throw a RuntimeException with a custom message.
     *
     * @param message The custom message for the exception.
     * @param e The original exception that caused the error.
     * @return A RuntimeException with the custom message and the original cause.
     */
    private static RuntimeException handleException(String message, Exception e) {
        return new RuntimeException(message, e);
    }
}
