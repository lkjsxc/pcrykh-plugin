package dev.pcrykh.domain;

import com.fasterxml.jackson.databind.JsonNode;

public record AchievementDefinition(
    String id,
    String name,
    String categoryId,
    String icon,
    String title,
    String description,
    JsonNode criteria,
    JsonNode rewards
) {}
