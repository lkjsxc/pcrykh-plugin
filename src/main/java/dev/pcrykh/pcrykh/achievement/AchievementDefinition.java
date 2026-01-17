package dev.pcrykh.pcrykh.achievement;

import java.util.Map;

public record AchievementDefinition(
        String id,
        String name,
        String categoryId,
        String icon,
        String title,
        String description,
        Map<String, Object> criteria,
        Map<String, Object> rewards
) {
}
