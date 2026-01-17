package dev.pcrykh.domain;

import com.fasterxml.jackson.databind.JsonNode;
import dev.pcrykh.runtime.ConfigException;

import java.util.Set;

public class CriteriaValidator {
    private static final Set<String> TYPES = Set.of(
            "block_break",
            "item_craft",
            "entity_kill",
            "fish_catch"
    );

    public void validate(JsonNode criteria) {
        if (criteria == null || !criteria.isObject()) {
            throw new ConfigException("criteria must be an object");
        }
        require(criteria, "type");
        require(criteria, "count");
        require(criteria, "constraints");

        String type = criteria.get("type").asText();
        if (!TYPES.contains(type)) {
            throw new ConfigException("Unsupported criteria type: " + type);
        }
        if (!criteria.get("constraints").isObject()) {
            throw new ConfigException("constraints must be an object");
        }
        if (!criteria.get("count").canConvertToInt() || criteria.get("count").asInt() < 1) {
            throw new ConfigException("criteria count must be an integer >= 1");
        }

        switch (type) {
            case "block_break" -> requireArray(criteria, "materials");
            case "item_craft" -> requireText(criteria, "item");
            case "entity_kill" -> requireArray(criteria, "entities");
            case "fish_catch" -> requireArray(criteria, "items");
            default -> {
            }
        }
    }

    private void require(JsonNode node, String field) {
        if (node.get(field) == null || node.get(field).isNull()) {
            throw new ConfigException("Missing criteria field: " + field);
        }
    }

    private void requireArray(JsonNode node, String field) {
        require(node, field);
        JsonNode array = node.get(field);
        if (!array.isArray() || array.size() == 0) {
            throw new ConfigException("criteria field must be a non-empty array: " + field);
        }
    }

    private void requireText(JsonNode node, String field) {
        require(node, field);
        JsonNode text = node.get(field);
        if (!text.isTextual() || text.asText().isBlank()) {
            throw new ConfigException("criteria field must be a non-empty string: " + field);
        }
    }
}
