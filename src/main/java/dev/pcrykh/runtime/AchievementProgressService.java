package dev.pcrykh.runtime;

import dev.pcrykh.domain.AchievementDefinition;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

public class AchievementProgressService {
    private final AchievementCatalog catalog;
    private final RuntimeConfig config;

    private final Map<String, CriteriaSpec> criteriaById = new HashMap<>();
    private final Map<String, List<AchievementDefinition>> blockBreakIndex = new HashMap<>();
    private final Map<String, List<AchievementDefinition>> itemCraftIndex = new HashMap<>();
    private final Map<String, List<AchievementDefinition>> entityKillIndex = new HashMap<>();
    private final Map<String, List<AchievementDefinition>> fishCatchIndex = new HashMap<>();

    private final Map<UUID, Map<String, Integer>> progress = new HashMap<>();
    private final Map<UUID, Set<String>> unlocked = new HashMap<>();

    public AchievementProgressService(AchievementCatalog catalog, RuntimeConfig config) {
        this.catalog = catalog;
        this.config = config;
        indexAchievements();
    }

    public int getProgress(Player player, AchievementDefinition achievement) {
        return progress
                .getOrDefault(player.getUniqueId(), Map.of())
                .getOrDefault(achievement.id(), 0);
    }

    public int getTarget(AchievementDefinition achievement) {
        CriteriaSpec spec = criteriaById.get(achievement.id());
        return spec == null ? 0 : spec.count();
    }

    public int getAp(AchievementDefinition achievement) {
        return achievement.rewards().get("ap").asInt();
    }

    public int getCompletedCount(Player player) {
        return unlocked.getOrDefault(player.getUniqueId(), Set.of()).size();
    }

    public int getTotalAp(Player player) {
        int total = 0;
        Set<String> completed = unlocked.getOrDefault(player.getUniqueId(), Set.of());
        for (AchievementDefinition achievement : catalog.achievements()) {
            if (completed.contains(achievement.id())) {
                total += getAp(achievement);
            }
        }
        return total;
    }

    public void onBlockBreak(Player player, Material material) {
        String key = normalizeMaterial(material);
        handleProgress(player, blockBreakIndex.getOrDefault(key, List.of()));
    }

    public void onItemCraft(Player player, Material material) {
        String key = normalizeMaterial(material);
        handleProgress(player, itemCraftIndex.getOrDefault(key, List.of()));
    }

    public void onEntityKill(Player player, EntityType type) {
        String key = normalizeEntity(type);
        handleProgress(player, entityKillIndex.getOrDefault(key, List.of()));
    }

    public void onFishCatch(Player player, Material material) {
        String key = normalizeMaterial(material);
        handleProgress(player, fishCatchIndex.getOrDefault(key, List.of()));
    }

    private void handleProgress(Player player, List<AchievementDefinition> achievements) {
        if (achievements.isEmpty()) {
            return;
        }
        UUID playerId = player.getUniqueId();
        Map<String, Integer> playerProgress = progress.computeIfAbsent(playerId, id -> new HashMap<>());
        Set<String> playerUnlocked = unlocked.computeIfAbsent(playerId, id -> new HashSet<>());

        for (AchievementDefinition achievement : achievements) {
            CriteriaSpec spec = criteriaById.get(achievement.id());
            if (spec == null) {
                continue;
            }
            int current = playerProgress.getOrDefault(achievement.id(), 0);
            int next = Math.min(spec.count(), current + 1);
            if (next == current) {
                continue;
            }
            playerProgress.put(achievement.id(), next);

            if (next >= spec.count() && !playerUnlocked.contains(achievement.id())) {
                playerUnlocked.add(achievement.id());
                broadcastUnlock(player, achievement);
            }

            notifyProgress(player, achievement, next, spec.count());
        }
    }

    private void broadcastUnlock(Player player, AchievementDefinition achievement) {
        if (!config.chat().announceAchievements()) {
            return;
        }
        String message = config.chat().prefix() + player.getName() + " unlocked " + achievement.title();
        Bukkit.getServer().broadcast(Component.text(message));
    }

    private void notifyProgress(Player player, AchievementDefinition achievement, int current, int target) {
        if (!config.actionBar().progressEnabled()) {
            return;
        }
        if (target <= 0) {
            return;
        }
        String meter = buildProgressMeter(current, target, 10);
        String message = achievement.title() + " " + meter + " " + current + "/" + target;
        player.sendActionBar(Component.text(message));
    }

    private String buildProgressMeter(int current, int target, int width) {
        if (width <= 0 || target <= 0) {
            return "[]";
        }
        double ratio = Math.min(1.0, Math.max(0.0, current / (double) target));
        int filled = (int) Math.floor(ratio * width);
        StringBuilder builder = new StringBuilder();
        builder.append("[");
        for (int i = 0; i < width; i++) {
            builder.append(i < filled ? "█" : "░");
        }
        builder.append("]");
        return builder.toString();
    }

    private void indexAchievements() {
        for (AchievementDefinition achievement : catalog.achievements()) {
            CriteriaSpec spec = CriteriaSpec.from(achievement.criteria());
            criteriaById.put(achievement.id(), spec);
            switch (spec.type()) {
                case "block_break" -> indexList(blockBreakIndex, spec.materials(), achievement);
                case "item_craft" -> indexSingle(itemCraftIndex, spec.item(), achievement);
                case "entity_kill" -> indexList(entityKillIndex, spec.entities(), achievement);
                case "fish_catch" -> indexList(fishCatchIndex, spec.items(), achievement);
                default -> {
                }
            }
        }
    }

    private void indexList(Map<String, List<AchievementDefinition>> index, Set<String> keys, AchievementDefinition achievement) {
        for (String key : keys) {
            index.computeIfAbsent(key, id -> new ArrayList<>()).add(achievement);
        }
    }

    private void indexSingle(Map<String, List<AchievementDefinition>> index, String key, AchievementDefinition achievement) {
        if (key == null || key.isBlank()) {
            return;
        }
        index.computeIfAbsent(key, id -> new ArrayList<>()).add(achievement);
    }

    private String normalizeMaterial(Material material) {
        return material.name().toLowerCase();
    }

    private String normalizeEntity(EntityType type) {
        return type.name().toLowerCase();
    }
}
