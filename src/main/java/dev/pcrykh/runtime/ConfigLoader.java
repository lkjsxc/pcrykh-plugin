package dev.pcrykh.runtime;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class ConfigLoader {
    private final ObjectMapper mapper;

    public ConfigLoader(ObjectMapper mapper) {
        this.mapper = mapper;
    }

    public RuntimeConfig load(Path dataFolder) {
        Path configPath = dataFolder.resolve("config.json");
        if (!Files.exists(configPath)) {
            throw new ConfigException("Missing config.json");
        }

        try {
            long lineCount = Files.readAllLines(configPath, StandardCharsets.UTF_8).size();
            if (lineCount > 300) {
                throw new ConfigException("config.json exceeds 300 lines");
            }

            JsonNode root = mapper.readTree(configPath.toFile());
            require(root, "spec_version");
            require(root, "commands");
            require(root, "runtime");
            require(root, "facts");
            require(root, "achievement_sources");

            JsonNode commands = root.get("commands");
            require(commands, "root");
            String commandRoot = commands.get("root").asText();
            if (!"pcrykh".equals(commandRoot)) {
                throw new ConfigException("commands.root must be pcrykh");
            }

            String specVersion = root.get("spec_version").asText();
            if (!specVersion.startsWith("4.")) {
                throw new ConfigException("spec_version must start with 4.");
            }

            JsonNode runtime = root.get("runtime");
            require(runtime, "autosave");
            require(runtime, "chat");
            require(runtime, "action_bar");

            JsonNode factsNode = root.get("facts");
            JsonNode achievementSources = root.get("achievement_sources");
            if (!factsNode.isArray() || factsNode.size() == 0) {
                throw new ConfigException("facts must be a non-empty array");
            }
            if (!achievementSources.isArray() || achievementSources.size() == 0) {
                throw new ConfigException("achievement_sources must be a non-empty array");
            }

            List<String> sources = new ArrayList<>();
            ArrayNode sourceArray = (ArrayNode) achievementSources;
            for (JsonNode entry : sourceArray) {
                sources.add(entry.asText());
            }

            RuntimeConfig.AutosaveConfig autosave = parseAutosave(runtime.get("autosave"));
            RuntimeConfig.ChatConfig chat = parseChat(runtime.get("chat"));
            RuntimeConfig.ActionBarConfig actionBar = parseActionBar(runtime.get("action_bar"));

            List<String> facts = new ArrayList<>();
            for (JsonNode entry : factsNode) {
                facts.add(entry.asText());
            }

            return new RuntimeConfig(
                    specVersion,
                    commandRoot,
                    autosave,
                    chat,
                    actionBar,
                    facts,
                    sources
            );
        } catch (IOException ex) {
            throw new ConfigException("Failed to read config.json", ex);
        }
    }

    private void require(JsonNode node, String field) {
        if (node == null || node.get(field) == null || node.get(field).isNull()) {
            throw new ConfigException("Missing required field: " + field);
        }
    }

    private RuntimeConfig.AutosaveConfig parseAutosave(JsonNode autosave) {
        require(autosave, "enabled");
        require(autosave, "interval_seconds");
        return new RuntimeConfig.AutosaveConfig(
                autosave.get("enabled").asBoolean(),
                autosave.get("interval_seconds").asInt()
        );
    }

    private RuntimeConfig.ChatConfig parseChat(JsonNode chat) {
        require(chat, "announce_achievements");
        require(chat, "facts_enabled");
        require(chat, "facts_interval_seconds");
        require(chat, "prefix");
        return new RuntimeConfig.ChatConfig(
                chat.get("announce_achievements").asBoolean(),
                chat.get("facts_enabled").asBoolean(),
                chat.get("facts_interval_seconds").asInt(),
                chat.get("prefix").asText()
        );
    }

    private RuntimeConfig.ActionBarConfig parseActionBar(JsonNode actionBar) {
        require(actionBar, "progress_enabled");
        require(actionBar, "milestone_thresholds");
        require(actionBar, "cooldown_seconds");
        JsonNode thresholds = actionBar.get("milestone_thresholds");
        if (!thresholds.isArray() || thresholds.size() == 0) {
            throw new ConfigException("milestone_thresholds must be a non-empty array");
        }
        List<Double> values = new ArrayList<>();
        for (JsonNode entry : thresholds) {
            values.add(entry.asDouble());
        }
        return new RuntimeConfig.ActionBarConfig(
                actionBar.get("progress_enabled").asBoolean(),
                values,
                actionBar.get("cooldown_seconds").asInt()
        );
    }
}
