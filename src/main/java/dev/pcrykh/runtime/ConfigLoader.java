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
            require(root, "tips");
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

            JsonNode tips = root.get("tips");
            JsonNode achievementSources = root.get("achievement_sources");
            if (!achievementSources.isArray() || achievementSources.size() == 0) {
                throw new ConfigException("achievement_sources must be a non-empty array");
            }

            List<String> sources = new ArrayList<>();
            ArrayNode sourceArray = (ArrayNode) achievementSources;
            for (JsonNode entry : sourceArray) {
                sources.add(entry.asText());
            }

            return new RuntimeConfig(
                    specVersion,
                    commandRoot,
                    runtime.get("autosave"),
                    runtime.get("chat"),
                    tips,
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
}
