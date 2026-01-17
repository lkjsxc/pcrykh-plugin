package dev.pcrykh.runtime;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import dev.pcrykh.domain.AchievementDefinition;
import dev.pcrykh.util.JsonUtils;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;

public final class ConfigLoader {
    private final ObjectMapper mapper = new ObjectMapper();

    public LoadResult load(File dataFolder) throws ConfigException {
        File configFile = new File(dataFolder, "config.json");
        if (!configFile.exists()) {
            throw new ConfigException("config.json not found.");
        }
        enforceLineLimit(configFile);

        JsonNode root = parseJson(configFile);
        if (!root.isObject()) {
            throw new ConfigException("config.json root must be an object.");
        }

        ObjectNode commands;
        String commandsRoot;
        JsonNode runtime;
        JsonNode autosave;
        JsonNode chat;
        ArrayNode tips;
        String specVersion;
        ArrayNode sourcesNode;
        try {
            commands = JsonUtils.requireObject(root, "commands");
            commandsRoot = JsonUtils.requireText(commands, "root");
            runtime = JsonUtils.requireObject(root, "runtime");
            autosave = JsonUtils.requireObject(runtime, "autosave");
            chat = JsonUtils.requireObject(runtime, "chat");
            tips = JsonUtils.requireArray(root, "tips");
            specVersion = JsonUtils.requireText(root, "spec_version");
            sourcesNode = JsonUtils.requireArray(root, "achievement_sources");
        } catch (IllegalArgumentException ex) {
            throw new ConfigException(ex.getMessage());
        }
        if (!"pcrykh".equals(commandsRoot)) {
            throw new ConfigException("commands.root must be 'pcrykh'.");
        }
        if (!specVersion.startsWith("4.")) {
            throw new ConfigException("Unsupported spec_version: " + specVersion);
        }
        if (sourcesNode.isEmpty()) {
            throw new ConfigException("achievement_sources must be a non-empty array.");
        }
        List<String> sources = new ArrayList<>();
        for (JsonNode entry : sourcesNode) {
            if (!entry.isTextual() || entry.asText().isBlank()) {
                throw new ConfigException("achievement_sources entries must be non-empty strings.");
            }
            sources.add(entry.asText());
        }

        List<File> packFiles = resolveAchievementSources(dataFolder, sources);
        if (packFiles.isEmpty()) {
            throw new ConfigException("No achievement pack files resolved.");
        }

        PackLoader packLoader = new PackLoader(mapper);
        List<AchievementDefinition> achievements = packLoader.loadPacks(packFiles);
        AchievementCatalog catalog = new AchievementCatalog(achievements);
        RuntimeConfig config = new RuntimeConfig(specVersion, commandsRoot, autosave, chat, tips, sources);
        return new LoadResult(config, catalog);
    }

    private void enforceLineLimit(File configFile) throws ConfigException {
        try {
            long lines = Files.readAllLines(configFile.toPath()).size();
            if (lines > 300) {
                throw new ConfigException("config.json exceeds 300 lines.");
            }
        } catch (IOException ex) {
            throw new ConfigException("Failed to read config.json line count.");
        }
    }

    private JsonNode parseJson(File configFile) throws ConfigException {
        try {
            return mapper.readTree(configFile);
        } catch (IOException ex) {
            throw new ConfigException("config.json is not valid JSON.");
        }
    }

    private List<File> resolveAchievementSources(File dataFolder, List<String> sources) throws ConfigException {
        List<File> files = new ArrayList<>();
        for (String source : sources) {
            File entry = new File(dataFolder, source);
            if (!entry.exists()) {
                throw new ConfigException("Achievement source not found: " + source);
            }
            if (entry.isDirectory()) {
                files.addAll(AchievementSourceResolver.collectJsonFiles(entry));
            } else {
                if (!entry.getName().endsWith(".json")) {
                    throw new ConfigException("Achievement source file must be .json: " + source);
                }
                files.add(entry);
            }
        }
        return files;
    }

    public record LoadResult(RuntimeConfig config, AchievementCatalog catalog) {}
}
