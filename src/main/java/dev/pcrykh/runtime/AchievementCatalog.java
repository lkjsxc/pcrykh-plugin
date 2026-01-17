package dev.pcrykh.runtime;

import dev.pcrykh.domain.AchievementDefinition;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class AchievementCatalog {
    private final List<AchievementDefinition> achievements;

    public AchievementCatalog(List<AchievementDefinition> achievements) {
        this.achievements = achievements.stream()
                .sorted(Comparator.comparing(AchievementDefinition::id))
                .toList();
    }

    public List<AchievementDefinition> achievements() {
        return Collections.unmodifiableList(achievements);
    }
}
