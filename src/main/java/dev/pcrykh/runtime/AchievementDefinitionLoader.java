package dev.pcrykh.runtime;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import dev.pcrykh.domain.AchievementDefinition;
import dev.pcrykh.domain.CriteriaValidator;
import dev.pcrykh.domain.RewardValidator;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class AchievementDefinitionLoader {
    private final ObjectMapper mapper;
    private final CriteriaValidator criteriaValidator = new CriteriaValidator();
    private final RewardValidator rewardValidator = new RewardValidator();

    public AchievementDefinitionLoader(ObjectMapper mapper) {
        this.mapper = mapper;
    }

    public List<AchievementDefinition> loadAll(List<Path> files) {
        List<AchievementDefinition> achievements = new ArrayList<>();
        for (Path path : files) {
            achievements.addAll(loadFile(path));
        }
        return achievements;
    }

    public List<AchievementDefinition> loadFile(Path path) {
        enforceLineLimit(path);
        try {
            JsonNode root = mapper.readTree(path.toFile());
            if (root.isArray()) {
                List<AchievementDefinition> achievements = new ArrayList<>();
                for (JsonNode node : root) {
                    achievements.add(parseAchievement(node, path.toString()));
                }
                return achievements;
            }
            if (root.isObject()) {
                return List.of(parseAchievement(root, path.toString()));
            }

            throw new ConfigException("Achievement source must be an object or array: " + path);
        } catch (IOException ex) {
            throw new ConfigException("Failed to read achievement file: " + path, ex);
        }
    }

    private AchievementDefinition parseAchievement(JsonNode root, String sourceLabel) {
        if (root == null || !root.isObject()) {
            throw new ConfigException("achievement entry must be an object: " + sourceLabel);
        }

        require(root, "id");
        require(root, "category_id");
        require(root, "icon");
        require(root, "title");
        require(root, "description");
        require(root, "amount");
        require(root, "criteria");
        require(root, "reward");

        String id = root.get("id").asText();
        String categoryId = root.get("category_id").asText();
        String icon = root.get("icon").asText();
        String title = root.get("title").asText();
        String description = root.get("description").asText();
        String name = root.has("name") ? root.get("name").asText() : title;
        int amount = root.get("amount").asInt();

        if (id.isBlank()) {
            throw new ConfigException("achievement id must be non-empty: " + sourceLabel);
        }
        if (categoryId.isBlank()) {
            throw new ConfigException("achievement category_id must be non-empty: " + sourceLabel);
        }
        if (icon.isBlank()) {
            throw new ConfigException("achievement icon must be non-empty: " + sourceLabel);
        }
        if (title.isBlank()) {
            throw new ConfigException("achievement title must be non-empty: " + sourceLabel);
        }
        if (description.isBlank()) {
            throw new ConfigException("achievement description must be non-empty: " + sourceLabel);
        }
        if (amount < 1) {
            throw new ConfigException("achievement amount must be >= 1: " + sourceLabel);
        }

        JsonNode criteriaNode = root.get("criteria");
        if (criteriaNode == null || !criteriaNode.isObject()) {
            throw new ConfigException("criteria must be an object: " + sourceLabel);
        }
        ObjectNode criteria = ((ObjectNode) criteriaNode).deepCopy();
        if (criteria.has("count") && criteria.get("count").asInt() != amount) {
            throw new ConfigException("criteria.count must match amount: " + sourceLabel);
        }
        criteria.put("count", amount);
        criteriaValidator.validate(criteria);

        JsonNode reward = root.get("reward");
        rewardValidator.validate(reward);

        return new AchievementDefinition(
                id,
                name.isBlank() ? title : name,
                categoryId,
                icon,
                title,
                description,
                criteria,
                reward
        );
    }

    private void require(JsonNode node, String field) {
        if (node == null || node.get(field) == null || node.get(field).isNull()) {
            throw new ConfigException("Missing achievement field: " + field);
        }
    }

    private void enforceLineLimit(Path path) {
        try {
            long lineCount = Files.readAllLines(path, StandardCharsets.UTF_8).size();
            if (lineCount > 300) {
                throw new ConfigException("Achievement file exceeds 300 lines: " + path);
            }
        } catch (IOException ex) {
            throw new ConfigException("Failed to read achievement file: " + path, ex);
        }
    }
}
