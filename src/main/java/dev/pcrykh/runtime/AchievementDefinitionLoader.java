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

            if (root.isObject() && root.has("series")) {
                return loadSeries(root.get("series"), path);
            }

            throw new ConfigException("Achievement source must be an array or series object: " + path);
        } catch (IOException ex) {
            throw new ConfigException("Failed to read achievement file: " + path, ex);
        }
    }

    private List<AchievementDefinition> loadSeries(JsonNode seriesNode, Path path) {
        if (seriesNode == null || !seriesNode.isArray() || seriesNode.size() == 0) {
            throw new ConfigException("series must be a non-empty array: " + path);
        }

        List<AchievementDefinition> achievements = new ArrayList<>();
        for (JsonNode series : seriesNode) {
            if (!series.isObject()) {
                throw new ConfigException("series entry must be an object: " + path);
            }

            require(series, "id_prefix");
            require(series, "count");
            require(series, "category_id");
            require(series, "icon");
            require(series, "title_template");
            require(series, "description_template");
            require(series, "amount_start");
            require(series, "amount_step");
            require(series, "reward_ap_start");
            require(series, "reward_ap_step");
            require(series, "criteria");

            String idPrefix = series.get("id_prefix").asText();
            int count = series.get("count").asInt();
            String categoryId = series.get("category_id").asText();
            String icon = series.get("icon").asText();
            String titleTemplate = series.get("title_template").asText();
            String descriptionTemplate = series.get("description_template").asText();
            String nameTemplate = series.has("name_template") ? series.get("name_template").asText() : null;
            int amountStart = series.get("amount_start").asInt();
            int amountStep = series.get("amount_step").asInt();
            int rewardApStart = series.get("reward_ap_start").asInt();
            int rewardApStep = series.get("reward_ap_step").asInt();

            if (idPrefix.isBlank()) {
                throw new ConfigException("series id_prefix must be non-empty: " + path);
            }
            if (count < 1) {
                throw new ConfigException("series count must be >= 1: " + path);
            }
            if (categoryId.isBlank()) {
                throw new ConfigException("series category_id must be non-empty: " + path);
            }
            if (icon.isBlank()) {
                throw new ConfigException("series icon must be non-empty: " + path);
            }
            if (titleTemplate.isBlank()) {
                throw new ConfigException("series title_template must be non-empty: " + path);
            }
            if (descriptionTemplate.isBlank()) {
                throw new ConfigException("series description_template must be non-empty: " + path);
            }
            if (amountStart < 1) {
                throw new ConfigException("series amount_start must be >= 1: " + path);
            }
            if (amountStep < 0) {
                throw new ConfigException("series amount_step must be >= 0: " + path);
            }
            if (rewardApStart < 0) {
                throw new ConfigException("series reward_ap_start must be >= 0: " + path);
            }
            if (rewardApStep < 0) {
                throw new ConfigException("series reward_ap_step must be >= 0: " + path);
            }

            JsonNode criteriaNode = series.get("criteria");
            if (criteriaNode == null || !criteriaNode.isObject()) {
                throw new ConfigException("series criteria must be an object: " + path);
            }

            for (int i = 1; i <= count; i++) {
                int amount = amountStart + (amountStep * (i - 1));
                String id = idPrefix + "_" + String.format("%03d", i);
                String title = renderTemplate(titleTemplate, i, amount);
                String description = renderTemplate(descriptionTemplate, i, amount);
                String name = nameTemplate == null ? title : renderTemplate(nameTemplate, i, amount);

                ObjectNode criteria = ((ObjectNode) criteriaNode).deepCopy();
                criteria.put("count", amount);
                criteriaValidator.validate(criteria);

                ObjectNode reward = mapper.createObjectNode();
                reward.put("ap", rewardApStart + (rewardApStep * (i - 1)));
                rewardValidator.validate(reward);

                achievements.add(new AchievementDefinition(
                        id,
                        name.isBlank() ? title : name,
                        categoryId,
                        icon,
                        title,
                        description,
                        criteria,
                        reward
                ));
            }
        }

        return achievements;
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

    private String renderTemplate(String template, int index, int amount) {
        return template
                .replace("{i}", String.valueOf(index))
                .replace("{amount}", String.valueOf(amount));
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
