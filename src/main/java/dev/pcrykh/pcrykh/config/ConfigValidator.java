package dev.pcrykh.pcrykh.config;

import java.util.ArrayList;
import java.util.List;

public class ConfigValidator {
    public List<String> validate(RuntimeConfig config) {
        List<String> errors = new ArrayList<>();
        if (config == null) {
            errors.add("config: null");
            return errors;
        }
        if (config.specVersion() == null || config.specVersion().isBlank()) {
            errors.add("spec_version: required");
        } else if (!config.specVersion().startsWith("4.")) {
            errors.add("spec_version: unsupported (expected 4.x)");
        }
        if (config.commands() == null || config.commands().root() == null) {
            errors.add("commands.root: required");
        } else if (!"pcrykh".equals(config.commands().root())) {
            errors.add("commands.root: must be 'pcrykh'");
        }
        if (config.runtime() == null || config.runtime().autosave() == null || config.runtime().chat() == null) {
            errors.add("runtime.autosave + runtime.chat: required");
        }
        if (config.tips() == null) {
            errors.add("tips: required");
        }
        if (config.achievementSources() == null || config.achievementSources().isEmpty()) {
            errors.add("achievement_sources: required (non-empty)");
        }
        return errors;
    }
}
