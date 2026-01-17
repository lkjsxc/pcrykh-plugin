package dev.pcrykh.util;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

public final class JsonUtils {
    private JsonUtils() {
    }

    public static ObjectNode requireObject(JsonNode node, String field) {
        JsonNode value = requireField(node, field);
        if (!value.isObject()) {
            throw new IllegalArgumentException("Field '" + field + "' must be an object.");
        }
        return (ObjectNode) value;
    }

    public static ArrayNode requireArray(JsonNode node, String field) {
        JsonNode value = requireField(node, field);
        if (!value.isArray()) {
            throw new IllegalArgumentException("Field '" + field + "' must be an array.");
        }
        return (ArrayNode) value;
    }

    public static String requireText(JsonNode node, String field) {
        JsonNode value = requireField(node, field);
        if (!value.isTextual()) {
            throw new IllegalArgumentException("Field '" + field + "' must be a string.");
        }
        String text = value.asText();
        if (text.isBlank()) {
            throw new IllegalArgumentException("Field '" + field + "' must be non-empty.");
        }
        return text;
    }

    public static int requireInt(JsonNode node, String field) {
        JsonNode value = requireField(node, field);
        if (!value.isInt()) {
            throw new IllegalArgumentException("Field '" + field + "' must be an integer.");
        }
        return value.asInt();
    }

    public static JsonNode requireField(JsonNode node, String field) {
        if (node == null || node.isNull()) {
            throw new IllegalArgumentException("Missing object for required field '" + field + "'.");
        }
        JsonNode value = node.get(field);
        if (value == null || value.isNull()) {
            throw new IllegalArgumentException("Missing required field '" + field + "'.");
        }
        return value;
    }
}
