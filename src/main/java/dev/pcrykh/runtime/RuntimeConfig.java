package dev.pcrykh.runtime;

import java.util.List;

public class RuntimeConfig {
        private final String specVersion;
        private final String commandRoot;
        private final AutosaveConfig autosave;
        private final ChatConfig chat;
        private final ActionBarConfig actionBar;
        private final List<String> facts;
        private final List<String> achievementSources;

        public RuntimeConfig(
                        String specVersion,
                        String commandRoot,
                        AutosaveConfig autosave,
                        ChatConfig chat,
                        ActionBarConfig actionBar,
                        List<String> facts,
                        List<String> achievementSources
        ) {
                this.specVersion = specVersion;
                this.commandRoot = commandRoot;
                this.autosave = autosave;
                this.chat = chat;
                this.actionBar = actionBar;
                this.facts = facts;
                this.achievementSources = achievementSources;
        }

        public String specVersion() {
                return specVersion;
        }

        public String commandRoot() {
                return commandRoot;
        }

        public AutosaveConfig autosave() {
                return autosave;
        }

        public ChatConfig chat() {
                return chat;
        }

        public ActionBarConfig actionBar() {
                return actionBar;
        }

        public List<String> facts() {
                return facts;
        }

        public List<String> achievementSources() {
                return achievementSources;
        }

        public static class AutosaveConfig {
                private final boolean enabled;
                private final int intervalSeconds;

                public AutosaveConfig(boolean enabled, int intervalSeconds) {
                        this.enabled = enabled;
                        this.intervalSeconds = intervalSeconds;
                }

                public boolean enabled() {
                        return enabled;
                }

                public int intervalSeconds() {
                        return intervalSeconds;
                }
        }

        public static class ChatConfig {
                private boolean announceAchievements;
                private boolean factsEnabled;
                private final int factsIntervalSeconds;
                private final String prefix;

                public ChatConfig(boolean announceAchievements, boolean factsEnabled, int factsIntervalSeconds, String prefix) {
                        this.announceAchievements = announceAchievements;
                        this.factsEnabled = factsEnabled;
                        this.factsIntervalSeconds = factsIntervalSeconds;
                        this.prefix = prefix;
                }

                public boolean announceAchievements() {
                        return announceAchievements;
                }

                public void setAnnounceAchievements(boolean announceAchievements) {
                        this.announceAchievements = announceAchievements;
                }

                public boolean factsEnabled() {
                        return factsEnabled;
                }

                public void setFactsEnabled(boolean factsEnabled) {
                        this.factsEnabled = factsEnabled;
                }

                public int factsIntervalSeconds() {
                        return factsIntervalSeconds;
                }

                public String prefix() {
                        return prefix;
                }
        }

        public static class ActionBarConfig {
                private boolean progressEnabled;
                private final List<Double> milestoneThresholds;
                private final int cooldownSeconds;

                public ActionBarConfig(boolean progressEnabled, List<Double> milestoneThresholds, int cooldownSeconds) {
                        this.progressEnabled = progressEnabled;
                        this.milestoneThresholds = milestoneThresholds;
                        this.cooldownSeconds = cooldownSeconds;
                }

                public boolean progressEnabled() {
                        return progressEnabled;
                }

                public void setProgressEnabled(boolean progressEnabled) {
                        this.progressEnabled = progressEnabled;
                }

                public List<Double> milestoneThresholds() {
                        return milestoneThresholds;
                }

                public int cooldownSeconds() {
                        return cooldownSeconds;
                }
        }
}
