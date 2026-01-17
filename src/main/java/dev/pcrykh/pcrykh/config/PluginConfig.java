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

    public static class Commands {
        public String root;
    }

    public static class Gui {
        public String theme;
    }

    public static class Runtime {
        public Autosave autosave;
    }

    public static class Autosave {
        public boolean enabled;
        @JsonProperty("interval_seconds")
        public int intervalSeconds;
    }
}
