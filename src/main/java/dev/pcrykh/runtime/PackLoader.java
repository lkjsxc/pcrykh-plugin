package dev.pcrykh.runtime;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import dev.pcrykh.domain.AchievementDefinition;
import dev.pcrykh.util.JsonUtils;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public final class PackLoader {
    private final ObjectMapper mapper;

    public PackLoader(ObjectMapper mapper) {
        this.mapper = mapper;
    }

    public List<AchievementDefinition> loadPacks(List<File> packFiles) throws ConfigException {
        List<AchievementDefinition> achievements = new ArrayList<>();
        Set<String> ids = new HashSet<>();
        for (File file : packFiles) {
            JsonNode pack = readPack(file);
            String packId;
            ArrayNode templates;
            try {
                packId = JsonUtils.requireText(pack, "pack_id");
                templates = JsonUtils.requireArray(pack, "templates");
            } catch (IllegalArgumentException ex) {
                throw new ConfigException(ex.getMessage());
            }
            if (templates.isEmpty()) {
                throw new ConfigException("Pack '" + packId + "' templates must be non-empty.");
            }

            Set<String> templateIds = new HashSet<>();
            for (JsonNode template : templates) {
                String templateId = JsonUtils.requireText(template, "template_id");
                if (!templateIds.add(templateId)) {
                    throw new ConfigException("Duplicate template_id in pack '" + packId + "': " + templateId);
                }
                List<AchievementDefinition> expanded = TemplateExpander.expand(template, mapper);
                for (AchievementDefinition def : expanded) {
                    if (!ids.add(def.id())) {
                        throw new ConfigException("Duplicate achievement id: " + def.id());
                    }
                    achievements.add(def);
                }
            }
        }
        return achievements;
    }

    private JsonNode readPack(File file) throws ConfigException {
        try {
            JsonNode root = mapper.readTree(file);
            if (!root.isObject()) {
                throw new ConfigException("Pack file must contain a JSON object: " + file.getName());
            }
            return root;
        } catch (IOException ex) {
            throw new ConfigException("Failed to read pack file: " + file.getName());
        }
    }
}
