package dev.pcrykh.pcrykh.config;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public record RuntimeConfig(
        @JsonProperty("spec_version") String specVersion,
        Commands commands,
        Runtime runtime,
        List<String> tips,
        @JsonProperty("achievement_sources") List<String> achievementSources
) {
    public record Commands(String root) {
    }

    public record Runtime(Autosave autosave, Chat chat) {
    }

    public record Autosave(boolean enabled, @JsonProperty("interval_seconds") int intervalSeconds) {
    }

    public record Chat(
            @JsonProperty("announce_achievements") boolean announceAchievements,
            @JsonProperty("tips_enabled") boolean tipsEnabled,
            @JsonProperty("tips_interval_seconds") int tipsIntervalSeconds,
            @JsonProperty("tips_prefix") String tipsPrefix
    ) {
    }
}
