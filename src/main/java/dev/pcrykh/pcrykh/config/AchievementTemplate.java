package dev.pcrykh.pcrykh.config;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;
import java.util.Map;

public record AchievementTemplate(
        @JsonProperty("template_id") String templateId,
        @JsonProperty("id_template") String idTemplate,
        @JsonProperty("name_template") String nameTemplate,
        @JsonProperty("title_template") String titleTemplate,
        @JsonProperty("description_template") String descriptionTemplate,
        @JsonProperty("category_id") String categoryId,
        @JsonProperty("icon_template") String iconTemplate,
        @JsonProperty("criteria_template") Map<String, Object> criteriaTemplate,
        @JsonProperty("rewards_template") Map<String, Object> rewardsTemplate,
        List<String> subjects,
        TierSpec tiers
) {
}
