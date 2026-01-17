package dev.pcrykh.runtime;

import com.fasterxml.jackson.databind.JsonNode;

import java.util.List;

public record TemplateDefinition(
        String templateId,
        String idTemplate,
        String nameTemplate,
        String titleTemplate,
        String descriptionTemplate,
        String categoryId,
        String iconTemplate,
        JsonNode criteriaTemplate,
        JsonNode rewardsTemplate,
        List<String> subjects,
        TierSpec tiers
) {
}
