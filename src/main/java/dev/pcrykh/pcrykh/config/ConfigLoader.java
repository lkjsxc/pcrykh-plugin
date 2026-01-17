package dev.pcrykh.pcrykh.config;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import dev.pcrykh.pcrykh.model.AchievementDefinition;
import dev.pcrykh.pcrykh.model.Criteria;
import dev.pcrykh.pcrykh.model.Reward;
import org.bukkit.Material;

import java.io.File;
import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class ConfigLoader {
    private final ObjectMapper mapper;

    public ConfigLoader() {
        this.mapper = new ObjectMapper();
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, true);
    }

    public PluginConfig load(File file) throws IOException {
        return mapper.readValue(file, PluginConfig.class);
    }

    public void validate(PluginConfig config) {
        if (config == null) {
            throw new IllegalArgumentException("config is null");
        }
        if (config.specVersion == null || config.specVersion.isBlank()) {
            throw new IllegalArgumentException("spec_version is required");
        }
        if (config.commands == null || !"pcrykh".equals(config.commands.root)) {
            throw new IllegalArgumentException("commands.root must be 'pcrykh'");
        }
        if (config.gui == null || config.gui.theme == null || config.gui.theme.isBlank()) {
            throw new IllegalArgumentException("gui.theme is required");
        }
        if (config.runtime == null || config.runtime.autosave == null) {
            throw new IllegalArgumentException("runtime.autosave is required");
        }
        if (config.runtime.chat == null) {
            throw new IllegalArgumentException("runtime.chat is required");
        }
        if (config.runtime.autosave.intervalSeconds <= 0) {
            throw new IllegalArgumentException("runtime.autosave.interval_seconds must be > 0");
        }
        if (config.runtime.chat.tipsIntervalSeconds <= 0) {
            throw new IllegalArgumentException("runtime.chat.tips_interval_seconds must be > 0");
        }
        if (config.runtime.chat.tipsPrefix == null || config.runtime.chat.tipsPrefix.isBlank()) {
            throw new IllegalArgumentException("runtime.chat.tips_prefix is required");
        }
        if (config.achievements == null) {
            throw new IllegalArgumentException("achievements is required");
        }
        if (config.tips == null) {
            throw new IllegalArgumentException("tips is required");
        }

        Set<String> ids = new HashSet<>();
        for (AchievementDefinition def : config.achievements) {
            validateAchievement(def, ids);
        }
    }

    private void validateAchievement(AchievementDefinition def, Set<String> ids) {
        if (def == null) {
            throw new IllegalArgumentException("achievement entry is null");
        }
        if (def.id == null || def.id.isBlank()) {
            throw new IllegalArgumentException("achievement.id is required");
        }
        if (!ids.add(def.id)) {
            throw new IllegalArgumentException("duplicate achievement id: " + def.id);
        }
        if (def.name == null || def.name.isBlank()) {
            throw new IllegalArgumentException("achievement.name is required: " + def.id);
        }
        if (def.categoryId == null || def.categoryId.isBlank()) {
            throw new IllegalArgumentException("achievement.category_id is required: " + def.id);
        }
        if (def.icon == null || def.icon.isBlank()) {
            throw new IllegalArgumentException("achievement.icon is required: " + def.id);
        }
        if (Material.matchMaterial(def.icon) == null) {
            throw new IllegalArgumentException("achievement.icon is invalid material: " + def.icon);
        }
        if (def.title == null || def.title.isBlank()) {
            throw new IllegalArgumentException("achievement.title is required: " + def.id);
        }
        if (def.description == null || def.description.isBlank()) {
            throw new IllegalArgumentException("achievement.description is required: " + def.id);
        }
        if (def.criteria == null) {
            throw new IllegalArgumentException("achievement.criteria required for achievement: " + def.id);
        }
        validateCriteria(def, def.criteria);
        Reward rewards = def.rewards;
        if (rewards == null) {
            throw new IllegalArgumentException("achievement.rewards required for achievement: " + def.id);
        }
        if (rewards.ap < 0) {
            throw new IllegalArgumentException("achievement.rewards.ap must be >= 0 for achievement: " + def.id);
        }
    }

    private void validateCriteria(AchievementDefinition def, Criteria criteria) {
        if (criteria.type == null || criteria.type.isBlank()) {
            throw new IllegalArgumentException("criteria.type required for achievement: " + def.id);
        }
        switch (criteria.type) {
            case "block_break" -> {
                requireList(criteria.materials, "criteria.materials", def);
                requireCount(criteria.count, def);
            }
            case "item_craft" -> {
                requireString(criteria.item, "criteria.item", def);
                requireCount(criteria.count, def);
            }
            case "entity_kill" -> {
                requireList(criteria.entities, "criteria.entities", def);
                requireCount(criteria.count, def);
            }
            case "fish_catch" -> {
                requireList(criteria.items, "criteria.items", def);
                requireCount(criteria.count, def);
            }
            case "travel" -> {
                if (criteria.distanceBlocks <= 0) {
                    throw new IllegalArgumentException("criteria.distance_blocks must be > 0 for achievement: " + def.id);
                }
            }
            case "travel_walk", "travel_sprint", "travel_swim", "travel_crouch", "travel_fly" -> {
                if (criteria.distanceBlocks <= 0) {
                    throw new IllegalArgumentException("criteria.distance_blocks must be > 0 for achievement: " + def.id);
                }
            }
            case "travel_mount" -> {
                if (criteria.distanceBlocks <= 0) {
                    throw new IllegalArgumentException("criteria.distance_blocks must be > 0 for achievement: " + def.id);
                }
                requireList(criteria.vehicles, "criteria.vehicles", def);
            }
            case "travel_boat" -> {
                if (criteria.distanceBlocks <= 0) {
                    throw new IllegalArgumentException("criteria.distance_blocks must be > 0 for achievement: " + def.id);
                }
                requireList(criteria.vehicles, "criteria.vehicles", def);
            }
            case "travel_boat_with_animal" -> {
                if (criteria.distanceBlocks <= 0) {
                    throw new IllegalArgumentException("criteria.distance_blocks must be > 0 for achievement: " + def.id);
                }
                requireList(criteria.vehicles, "criteria.vehicles", def);
                requireList(criteria.passengers, "criteria.passengers", def);
            }
            case "jump" -> {
                requireCount(criteria.count, def);
            }
            default -> throw new IllegalArgumentException("unsupported criteria.type: " + criteria.type + " for " + def.id);
        }
    }

    private void requireList(List<String> list, String field, AchievementDefinition def) {
        if (list == null || list.isEmpty()) {
            throw new IllegalArgumentException(field + " required for achievement: " + def.id);
        }
        for (String entry : list) {
            if (entry == null || entry.isBlank()) {
                throw new IllegalArgumentException(field + " contains blank entry for achievement: " + def.id);
            }
        }
    }

    private void requireString(String value, String field, AchievementDefinition def) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException(field + " required for achievement: " + def.id);
        }
    }

    private void requireCount(int count, AchievementDefinition def) {
        if (count <= 0) {
            throw new IllegalArgumentException("criteria.count must be > 0 for achievement: " + def.id);
        }
    }
}
