package dev.pcrykh.runtime;

import com.fasterxml.jackson.databind.JsonNode;

import java.util.HashSet;
import java.util.Set;

public record CriteriaSpec(
        String type,
        int count,
        Set<String> materials,
        String item,
        Set<String> entities,
        Set<String> items
) {
    public static CriteriaSpec from(JsonNode node) {
        if (node == null || !node.isObject()) {
            return new CriteriaSpec("", 0, Set.of(), "", Set.of(), Set.of());
        }
        String type = node.get("type").asText();
        int count = node.get("count").asInt();
        Set<String> materials = readSet(node.get("materials"));
        String item = node.has("item") ? node.get("item").asText() : "";
        Set<String> entities = readSet(node.get("entities"));
        Set<String> items = readSet(node.get("items"));

        return new CriteriaSpec(
                type,
                count,
                normalizeSet(materials),
                normalize(item),
                normalizeSet(entities),
                normalizeSet(items)
        );
    }

    private static Set<String> readSet(JsonNode node) {
        if (node == null || !node.isArray()) {
            return Set.of();
        }
        Set<String> values = new HashSet<>();
        for (JsonNode entry : node) {
            values.add(entry.asText());
        }
        return values;
    }

    private static String normalize(String value) {
        return value == null ? "" : value.toLowerCase();
    }

    private static Set<String> normalizeSet(Set<String> values) {
        Set<String> normalized = new HashSet<>();
        for (String value : values) {
            normalized.add(normalize(value));
        }
        return normalized;
    }
}
