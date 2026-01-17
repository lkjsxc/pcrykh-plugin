package dev.pcrykh.runtime;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.IntNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.databind.node.TextNode;
import dev.pcrykh.domain.AchievementDefinition;
import dev.pcrykh.domain.CriteriaValidator;
import dev.pcrykh.domain.RewardValidator;
import dev.pcrykh.util.RomanNumerals;
import dev.pcrykh.util.Slugify;
import dev.pcrykh.util.TokenContext;
import dev.pcrykh.util.TokenReplacer;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class TemplateExpander {
    private final ObjectMapper mapper;
    private final CriteriaValidator criteriaValidator = new CriteriaValidator();
    private final RewardValidator rewardValidator = new RewardValidator();

    public TemplateExpander(ObjectMapper mapper) {
        this.mapper = mapper;
    }

    public List<AchievementDefinition> expandAll(List<PackDefinition> packs) {
        List<AchievementDefinition> results = new ArrayList<>();
        Set<String> ids = new HashSet<>();

        for (PackDefinition pack : packs) {
            for (TemplateDefinition template : pack.templates()) {
                results.addAll(expandTemplate(template, ids));
            }
        }

        return results;
    }

    private List<AchievementDefinition> expandTemplate(TemplateDefinition template, Set<String> ids) {
        List<AchievementDefinition> results = new ArrayList<>();
        TierSpec tiers = template.tiers();

        for (String subject : template.subjects()) {
            for (int tierIndex = 1; tierIndex <= tiers.levels(); tierIndex++) {
                int count = (int) Math.round(tiers.countStart() * Math.pow(tiers.countMultiplier(), tierIndex - 1));
                int ap = (int) Math.round(tiers.apStart() * Math.pow(tiers.apMultiplier(), tierIndex - 1));

                TokenContext context = new TokenContext(
                        subject,
                        Slugify.slugify(subject),
                        tierIndex,
                        RomanNumerals.toRoman(tierIndex),
                        count,
                        ap
                );

                String id = TokenReplacer.replace(template.idTemplate(), context);
                if (id.isBlank()) {
                    throw new ConfigException("Generated achievement id is blank");
                }
                if (!ids.add(id)) {
                    throw new ConfigException("Duplicate achievement id: " + id);
                }

                String name = TokenReplacer.replace(template.nameTemplate(), context);
                String title = TokenReplacer.replace(template.titleTemplate(), context);
                String description = TokenReplacer.replace(template.descriptionTemplate(), context);
                String icon = TokenReplacer.replace(template.iconTemplate(), context);

                JsonNode criteria = TokenReplacer.replaceJson(template.criteriaTemplate(), context, mapper);
                JsonNode rewards = TokenReplacer.replaceJson(template.rewardsTemplate(), context, mapper);

                criteria = normalizeCriteria(criteria);
                rewards = normalizeRewards(rewards);

                criteriaValidator.validate(criteria);
                rewardValidator.validate(rewards);

                results.add(new AchievementDefinition(
                        id,
                        name,
                        template.categoryId(),
                        icon,
                        title,
                        description,
                        criteria,
                        rewards
                ));
            }
        }

        return results;
    }

    private JsonNode normalizeCriteria(JsonNode criteria) {
        if (criteria == null || !criteria.isObject()) {
            return criteria;
        }
        ObjectNode objectNode = (ObjectNode) criteria;
        JsonNode countNode = objectNode.get("count");
        if (countNode != null && countNode.isTextual() && countNode.asText().matches("^-?\\d+$")) {
            objectNode.set("count", new IntNode(Integer.parseInt(countNode.asText())));
        }
        if (objectNode.get("constraints") == null) {
            objectNode.set("constraints", mapper.createObjectNode());
        }
        return objectNode;
    }

    private JsonNode normalizeRewards(JsonNode rewards) {
        if (rewards == null || !rewards.isObject()) {
            return rewards;
        }
        ObjectNode objectNode = (ObjectNode) rewards;
        JsonNode apNode = objectNode.get("ap");
        if (apNode != null && apNode.isTextual() && apNode.asText().matches("^-?\\d+$")) {
            objectNode.set("ap", new IntNode(Integer.parseInt(apNode.asText())));
        }
        return objectNode;
    }
}
