package dev.pcrykh.runtime;

import dev.pcrykh.domain.AchievementDefinition;
import java.util.List;

public final class AchievementCatalog {
    private final List<AchievementDefinition> achievements;

    public AchievementCatalog(List<AchievementDefinition> achievements) {
        this.achievements = List.copyOf(achievements);
    }

    public int count() {
        return achievements.size();
    }

    public List<AchievementDefinition> all() {
        return achievements;
    }
}
