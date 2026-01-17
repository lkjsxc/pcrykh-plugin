package dev.pcrykh.pcrykh.config;

import com.fasterxml.jackson.annotation.JsonProperty;
import dev.pcrykh.pcrykh.model.AchievementDefinition;

import java.util.List;

public class PluginConfig {
    @JsonProperty("spec_version")
    public String specVersion;

    public Commands commands;
    public Gui gui;
    public Runtime runtime;

    public List<AchievementDefinition> achievements;
    public List<String> tips;

    public static class Commands {
        public String root;
    }

    public static class Gui {
        public String theme;
    }

    public static class Runtime {
        public Autosave autosave;
        public Chat chat;
    }

    public static class Autosave {
        public boolean enabled;
        @JsonProperty("interval_seconds")
        public int intervalSeconds;
    }

    public static class Chat {
        @JsonProperty("announce_achievements")
        public boolean announceAchievements;
        @JsonProperty("tips_enabled")
        public boolean tipsEnabled;
        @JsonProperty("tips_interval_seconds")
        public int tipsIntervalSeconds;
        @JsonProperty("tips_prefix")
        public String tipsPrefix;
    }
}
