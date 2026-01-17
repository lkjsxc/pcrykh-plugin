package dev.pcrykh.runtime;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import java.io.IOException;
import java.nio.file.Path;

public class ConfigSaver {
    private final ObjectMapper mapper;

    public ConfigSaver(ObjectMapper mapper) {
        this.mapper = mapper;
    }

    public void save(Path dataFolder, RuntimeConfig config) {
        Path configPath = dataFolder.resolve("config.json");
        ObjectNode root = mapper.createObjectNode();
        root.put("spec_version", config.specVersion());

        ObjectNode commands = mapper.createObjectNode();
        commands.put("root", config.commandRoot());
        root.set("commands", commands);

        ObjectNode runtime = mapper.createObjectNode();
        ObjectNode autosave = mapper.createObjectNode();
        autosave.put("enabled", config.autosave().enabled());
        autosave.put("interval_seconds", config.autosave().intervalSeconds());
        runtime.set("autosave", autosave);

        ObjectNode chat = mapper.createObjectNode();
        chat.put("announce_achievements", config.chat().announceAchievements());
        chat.put("facts_enabled", config.chat().factsEnabled());
        chat.put("facts_interval_seconds", config.chat().factsIntervalSeconds());
        chat.put("prefix", config.chat().prefix());
        runtime.set("chat", chat);

        ObjectNode actionBar = mapper.createObjectNode();
        actionBar.put("progress_enabled", config.actionBar().progressEnabled());
        runtime.set("action_bar", actionBar);

        root.set("runtime", runtime);

        ArrayNode factsSources = mapper.createArrayNode();
        for (String source : config.factsSources()) {
            factsSources.add(source);
        }
        root.set("facts_sources", factsSources);

        ArrayNode categorySources = mapper.createArrayNode();
        for (String source : config.categorySources()) {
            categorySources.add(source);
        }
        root.set("category_sources", categorySources);

        ArrayNode sources = mapper.createArrayNode();
        for (String source : config.achievementSources()) {
            sources.add(source);
        }
        root.set("achievement_sources", sources);

        try {
            mapper.writerWithDefaultPrettyPrinter().writeValue(configPath.toFile(), root);
        } catch (IOException ex) {
            throw new ConfigException("Failed to write config.json", ex);
        }
    }
}
