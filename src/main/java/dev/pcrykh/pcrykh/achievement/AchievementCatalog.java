package dev.pcrykh.pcrykh.achievement;

import java.util.Collections;
import java.util.List;

public class AchievementCatalog {
    private final List<AchievementDefinition> achievements;

    public AchievementCatalog(List<AchievementDefinition> achievements) {
        this.achievements = List.copyOf(achievements);
    }

    public int size() {
        return achievements.size();
    }

    public List<AchievementDefinition> all() {
        return Collections.unmodifiableList(achievements);
    }
}
