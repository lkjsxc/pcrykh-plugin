package dev.pcrykh.domain;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import dev.pcrykh.runtime.ConfigException;
import dev.pcrykh.util.JsonUtils;
import java.util.Iterator;
import java.util.Map;

public final class CriteriaValidator {
    private CriteriaValidator() {
    }

    public static void validateCriteria(JsonNode criteria) throws ConfigException {
        if (criteria == null || !criteria.isObject()) {
            throw new ConfigException("criteria must be an object.");
        }
        ObjectNode object = (ObjectNode) criteria;
        Iterator<Map.Entry<String, JsonNode>> fields = object.fields();
        while (fields.hasNext()) {
            Map.Entry<String, JsonNode> entry = fields.next();
            validateCriteriaDefinition(entry.getKey(), entry.getValue());
        }
    }

    private static void validateCriteriaDefinition(String key, JsonNode node) throws ConfigException {
        if (node == null || !node.isObject()) {
            throw new ConfigException("criteria '" + key + "' must be an object.");
        }
        String type;
        int count;
        try {
            type = JsonUtils.requireText(node, "type");
            count = JsonUtils.requireInt(node, "count");
        } catch (IllegalArgumentException ex) {
            throw new ConfigException(ex.getMessage());
        }
        if (count < 1) {
            throw new ConfigException("criteria '" + key + "' count must be >= 1.");
        }
        JsonNode constraints;
        try {
            constraints = JsonUtils.requireField(node, "constraints");
        } catch (IllegalArgumentException ex) {
            throw new ConfigException(ex.getMessage());
        }
        if (!constraints.isObject()) {
            throw new ConfigException("criteria '" + key + "' constraints must be an object.");
        }

        switch (type) {
            case "block_break" -> validateStringArray(node, "materials", key);
            case "item_craft" -> validateStringField(node, "item", key);
            case "entity_kill" -> validateStringArray(node, "entities", key);
            case "fish_catch" -> validateStringArray(node, "items", key);
            default -> throw new ConfigException("criteria '" + key + "' has unsupported type: " + type);
        }
    }

    private static void validateStringArray(JsonNode node, String field, String key) throws ConfigException {
        JsonNode value;
        try {
            value = JsonUtils.requireField(node, field);
        } catch (IllegalArgumentException ex) {
            throw new ConfigException(ex.getMessage());
        }
        if (!value.isArray() || value.isEmpty()) {
            throw new ConfigException("criteria '" + key + "' field '" + field + "' must be a non-empty array.");
        }
        for (JsonNode item : value) {
            if (!item.isTextual() || item.asText().isBlank()) {
                throw new ConfigException("criteria '" + key + "' field '" + field + "' must contain non-empty strings.");
            }
        }
    }

    private static void validateStringField(JsonNode node, String field, String key) throws ConfigException {
        try {
            String value = JsonUtils.requireText(node, field);
            if (value.isBlank()) {
                throw new ConfigException("criteria '" + key + "' field '" + field + "' must be non-empty.");
            }
        } catch (IllegalArgumentException ex) {
            throw new ConfigException(ex.getMessage());
        }
    }
}
