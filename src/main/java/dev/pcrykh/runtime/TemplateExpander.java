package dev.pcrykh.runtime;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import dev.pcrykh.domain.AchievementDefinition;
import dev.pcrykh.domain.CriteriaValidator;
import dev.pcrykh.domain.RewardValidator;
import dev.pcrykh.util.JsonUtils;
import dev.pcrykh.util.RomanNumerals;
import dev.pcrykh.util.Slugify;
import dev.pcrykh.util.TokenContext;
import dev.pcrykh.util.TokenReplacer;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public final class TemplateExpander {
    private TemplateExpander() {
    }

    public static List<AchievementDefinition> expand(JsonNode template, ObjectMapper mapper) throws ConfigException {
        String idTemplate;
        String nameTemplate;
        String titleTemplate;
        String descriptionTemplate;
        String categoryTemplate;
        String iconTemplate;
        JsonNode criteriaTemplate;
        JsonNode rewardsTemplate;
        ArrayNode subjectsNode;
        ObjectNode tiers;
        try {
            idTemplate = JsonUtils.requireText(template, "id_template");
            nameTemplate = JsonUtils.requireText(template, "name_template");
            titleTemplate = JsonUtils.requireText(template, "title_template");
            descriptionTemplate = JsonUtils.requireText(template, "description_template");
            categoryTemplate = JsonUtils.requireText(template, "category_id");
            iconTemplate = JsonUtils.requireText(template, "icon_template");
            criteriaTemplate = JsonUtils.requireField(template, "criteria_template");
            rewardsTemplate = JsonUtils.requireField(template, "rewards_template");
            subjectsNode = JsonUtils.requireArray(template, "subjects");
            tiers = JsonUtils.requireObject(template, "tiers");
        } catch (IllegalArgumentException ex) {
            throw new ConfigException(ex.getMessage());
        }
        if (subjectsNode.isEmpty()) {
            throw new ConfigException("subjects must be a non-empty array.");
        }
        int levels = requirePositiveInt(tiers, "levels");
        int countStart = requirePositiveInt(tiers, "count_start");
        double countMultiplier = requirePositiveNumber(tiers, "count_multiplier");
        int apStart = requirePositiveInt(tiers, "ap_start");
        double apMultiplier = requirePositiveNumber(tiers, "ap_multiplier");

        List<AchievementDefinition> results = new ArrayList<>();
        for (JsonNode subjectNode : subjectsNode) {
            if (!subjectNode.isTextual() || subjectNode.asText().isBlank()) {
                throw new ConfigException("subjects must contain non-empty strings.");
            }
            String subject = subjectNode.asText();
            String subjectId = Slugify.slugify(subject);
            for (int t = 1; t <= levels; t++) {
                int count = (int) Math.round(countStart * Math.pow(countMultiplier, t - 1));
                int ap = (int) Math.round(apStart * Math.pow(apMultiplier, t - 1));
                TokenContext context = new TokenContext(subject, subjectId, RomanNumerals.toRoman(t), t, count, ap);

                String id = TokenReplacer.replaceTokens(idTemplate, context);
                String name = TokenReplacer.replaceTokens(nameTemplate, context);
                String title = TokenReplacer.replaceTokens(titleTemplate, context);
                String description = TokenReplacer.replaceTokens(descriptionTemplate, context);
                String categoryId = TokenReplacer.replaceTokens(categoryTemplate, context);
                String icon = TokenReplacer.replaceTokens(iconTemplate, context);

                JsonNode criteria = TokenReplacer.replace(criteriaTemplate, mapper, context);
                JsonNode rewards = TokenReplacer.replace(rewardsTemplate, mapper, context);
                criteria = coerceNumericFields(criteria, mapper);
                rewards = coerceNumericFields(rewards, mapper);

                validateAchievementFields(id, name, title, description, categoryId, icon, criteria, rewards);
                results.add(new AchievementDefinition(id, name, categoryId, icon, title, description, criteria, rewards));
            }
        }
        return results;
    }

    private static void validateAchievementFields(
        String id,
        String name,
        String title,
        String description,
        String categoryId,
        String icon,
        JsonNode criteria,
        JsonNode rewards
    ) throws ConfigException {
        if (id.isBlank() || name.isBlank() || title.isBlank() || description.isBlank() || categoryId.isBlank() || icon.isBlank()) {
            throw new ConfigException("Achievement fields must be non-empty strings.");
        }
        CriteriaValidator.validateCriteria(criteria);
        RewardValidator.validateRewards(rewards);
    }

    private static int requirePositiveInt(ObjectNode node, String field) throws ConfigException {
        int value;
        try {
            value = JsonUtils.requireInt(node, field);
        } catch (IllegalArgumentException ex) {
            throw new ConfigException(ex.getMessage());
        }
        if (value < 1) {
            throw new ConfigException("tiers." + field + " must be >= 1.");
        }
        return value;
    }

    private static double requirePositiveNumber(ObjectNode node, String field) throws ConfigException {
        JsonNode value = JsonUtils.requireField(node, field);
        if (!value.isNumber()) {
            throw new ConfigException("tiers." + field + " must be a number.");
        }
        double number = value.asDouble();
        if (number <= 0.0) {
            throw new ConfigException("tiers." + field + " must be > 0.");
        }
        return number;
    }

    private static JsonNode coerceNumericFields(JsonNode node, ObjectMapper mapper) {
        if (node == null || node.isNull()) {
            return node;
        }
        if (node.isObject()) {
            ObjectNode object = mapper.createObjectNode();
            Iterator<Map.Entry<String, JsonNode>> fields = node.fields();
            while (fields.hasNext()) {
                Map.Entry<String, JsonNode> entry = fields.next();
                String key = entry.getKey();
                JsonNode value = coerceNumericFields(entry.getValue(), mapper);
                if (("count".equals(key) || "ap".equals(key)) && value.isTextual()) {
                    String text = value.asText();
                    if (text.matches("-?\\d+")) {
                        object.put(key, Integer.parseInt(text));
                        continue;
                    }
                }
                object.set(key, value);
            }
            return object;
        }
        if (node.isArray()) {
            ArrayNode array = mapper.createArrayNode();
            for (JsonNode item : node) {
                array.add(coerceNumericFields(item, mapper));
            }
            return array;
        }
        return node.deepCopy();
    }
}
