package dev.pcrykh.runtime;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class CategoryLoader {
    private final ObjectMapper mapper;

    public CategoryLoader(ObjectMapper mapper) {
        this.mapper = mapper;
    }

    public List<CategoryDefinition> loadAll(List<Path> files) {
        List<CategoryDefinition> categories = new ArrayList<>();
        for (Path path : files) {
            categories.add(loadCategory(path));
        }
        return categories;
    }

    public CategoryDefinition loadCategory(Path path) {
        enforceLineLimit(path);
        try {
            JsonNode root = mapper.readTree(path.toFile());
            require(root, "id");
            require(root, "name");
            require(root, "order");
            require(root, "icon");

            String id = root.get("id").asText();
            String name = root.get("name").asText();
            int order = root.get("order").asInt();
            String icon = root.get("icon").asText();

            if (id.isBlank()) {
                throw new ConfigException("category id must be non-empty: " + path);
            }
            if (name.isBlank()) {
                throw new ConfigException("category name must be non-empty: " + path);
            }
            if (icon.isBlank()) {
                throw new ConfigException("category icon must be non-empty: " + path);
            }

            return new CategoryDefinition(id, name, order, icon);
        } catch (IOException ex) {
            throw new ConfigException("Failed to read category file: " + path, ex);
        }
    }

    private void require(JsonNode node, String field) {
        if (node == null || node.get(field) == null || node.get(field).isNull()) {
            throw new ConfigException("Missing category field: " + field);
        }
    }

    private void enforceLineLimit(Path path) {
        try {
            long lineCount = Files.readAllLines(path, StandardCharsets.UTF_8).size();
            if (lineCount > 300) {
                throw new ConfigException("Category file exceeds 300 lines: " + path);
            }
        } catch (IOException ex) {
            throw new ConfigException("Failed to read category file: " + path, ex);
        }
    }
}
