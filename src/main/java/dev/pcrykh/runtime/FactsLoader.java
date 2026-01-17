package dev.pcrykh.runtime;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class FactsLoader {
    private final ObjectMapper mapper;

    public FactsLoader(ObjectMapper mapper) {
        this.mapper = mapper;
    }

    public List<String> loadAll(List<Path> factFiles) {
        List<String> facts = new ArrayList<>();
        for (Path path : factFiles) {
            facts.addAll(loadPack(path));
        }
        return facts;
    }

    private List<String> loadPack(Path path) {
        try {
            long lineCount = Files.readAllLines(path, StandardCharsets.UTF_8).size();
            if (lineCount > 300) {
                throw new ConfigException("fact pack exceeds 300 lines: " + path);
            }
            JsonNode root = mapper.readTree(path.toFile());
            require(root, "pack_id");
            require(root, "facts");
            JsonNode factsNode = root.get("facts");
            if (!factsNode.isArray() || factsNode.size() == 0) {
                throw new ConfigException("facts must be a non-empty array: " + path);
            }
            List<String> facts = new ArrayList<>();
            for (JsonNode entry : factsNode) {
                String fact = entry.asText();
                if (!fact.isBlank()) {
                    facts.add(fact);
                }
            }
            if (facts.isEmpty()) {
                throw new ConfigException("fact pack contains only blank facts: " + path);
            }
            return facts;
        } catch (IOException ex) {
            throw new ConfigException("Failed to read fact pack: " + path, ex);
        }
    }

    private void require(JsonNode node, String field) {
        if (node == null || node.get(field) == null || node.get(field).isNull()) {
            throw new ConfigException("Missing required field: " + field);
        }
    }
}
