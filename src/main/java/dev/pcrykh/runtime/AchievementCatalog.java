package dev.pcrykh.runtime;

import dev.pcrykh.domain.AchievementDefinition;
import dev.pcrykh.runtime.ConfigException;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class AchievementCatalog {
    private final List<AchievementDefinition> achievements;
    private final CategoryCatalog categories;

    public AchievementCatalog(List<AchievementDefinition> achievements, CategoryCatalog categories) {
        this.categories = categories;
        this.achievements = achievements.stream()
                .peek(achievement -> {
                    if (categories.get(achievement.categoryId()) == null) {
                        throw new ConfigException("Unknown category_id: " + achievement.categoryId());
                    }
                })
                .sorted(Comparator
                        .comparing((AchievementDefinition achievement) -> categories.get(achievement.categoryId()).order())
                        .thenComparing(achievement -> categories.get(achievement.categoryId()).name())
                        .thenComparing(AchievementDefinition::id))
                .toList();
    }

    public List<AchievementDefinition> achievements() {
        return Collections.unmodifiableList(achievements);
    }

    public CategoryCatalog categories() {
        return categories;
    }
}
