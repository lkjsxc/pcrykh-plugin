package dev.pcrykh.runtime;

import dev.pcrykh.domain.AchievementDefinition;
import dev.pcrykh.runtime.ConfigException;

import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class AchievementCatalog {
    private final List<AchievementDefinition> achievements;
    private final CategoryCatalog categories;

    public AchievementCatalog(List<AchievementDefinition> achievements, CategoryCatalog categories) {
        this.categories = categories;
        Set<String> seenIds = new HashSet<>();
        this.achievements = achievements.stream()
                .peek(achievement -> {
                    if (categories.get(achievement.categoryId()) == null) {
                        throw new ConfigException("Unknown category_id: " + achievement.categoryId());
                    }
                    if (!seenIds.add(achievement.id())) {
                        throw new ConfigException("Duplicate achievement id: " + achievement.id());
                    }
                })
                .sorted(Comparator
                        .comparing((AchievementDefinition achievement) -> categories.get(achievement.categoryId()).order())
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
