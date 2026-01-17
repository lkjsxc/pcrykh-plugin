package dev.pcrykh.pcrykh.achievement;

import dev.pcrykh.pcrykh.config.PluginConfig;
import dev.pcrykh.pcrykh.model.AchievementDefinition;
import dev.pcrykh.pcrykh.model.Criteria;
import dev.pcrykh.pcrykh.storage.DataStore;
import dev.pcrykh.pcrykh.storage.DataStore.AchievementProgress;
import dev.pcrykh.pcrykh.storage.DataStore.PlayerState;
import dev.pcrykh.pcrykh.gui.GuiService;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Biome;
import org.bukkit.entity.FishHook;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.inventory.CraftItemEvent;
import org.bukkit.event.player.PlayerFishEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.Recipe;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.inventory.ShapelessRecipe;
import org.bukkit.plugin.java.JavaPlugin;

import java.time.Instant;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class AchievementService {
    private final JavaPlugin plugin;
    private final DataStore dataStore;
    private PluginConfig config;
    private GuiService guiService;

    private final Map<String, AchievementDefinition> achievementById = new HashMap<>();
    private final Map<String, List<TierRef>> tiersByType = new HashMap<>();
    private final Map<UUID, PlayerState> cache = new ConcurrentHashMap<>();
    private final Map<String, RateWindow> rateWindow = new ConcurrentHashMap<>();
    private final Map<String, Long> progressThrottle = new ConcurrentHashMap<>();

    public AchievementService(JavaPlugin plugin, DataStore dataStore, PluginConfig config, GuiService guiService) {
        this.plugin = plugin;
        this.dataStore = dataStore;
        this.config = config;
        this.guiService = guiService;
        indexAchievements();
    }

    public void setGuiService(GuiService guiService) {
        this.guiService = guiService;
    }

    public void reload() {
        achievementById.clear();
        tiersByType.clear();
        indexAchievements();
    }

    public void updateConfig(PluginConfig config) {
        this.config = config;
        reload();
    }

    private void indexAchievements() {
        for (AchievementDefinition def : config.achievements) {
            achievementById.put(def.id, def);
            for (AchievementDefinition.AchievementTier tier : def.tiers) {
                String type = tier.criteria.type;
                tiersByType.computeIfAbsent(type, k -> new ArrayList<>())
                        .add(new TierRef(def, tier));
            }
        }
    }

    public AchievementDefinition getAchievement(String id) {
        return achievementById.get(id);
    }

    public Collection<AchievementDefinition> getAchievements() {
        return config.achievements;
    }

    public PlayerState getOrLoad(Player player) {
        return cache.computeIfAbsent(player.getUniqueId(), id -> dataStore.loadPlayer(id, config.specVersion, config.achievements));
    }

    public PlayerState getOrLoad(UUID playerId) {
        return cache.computeIfAbsent(playerId, id -> dataStore.loadPlayer(id, config.specVersion, config.achievements));
    }

    public void flush(UUID playerId) {
        PlayerState state = cache.get(playerId);
        if (state != null) {
            dataStore.savePlayer(state, config.specVersion);
        }
    }

    public void flushAll() {
        for (UUID playerId : cache.keySet()) {
            flush(playerId);
        }
    }

    public List<Integer> getRecentHistory(UUID playerId, String achievementId, int limit) {
        return dataStore.getRecentTiers(playerId, achievementId, limit);
    }

    public void clearCache() {
        cache.clear();
    }

    public void resetPlayer(UUID playerId) {
        cache.remove(playerId);
        dataStore.deletePlayerProgress(playerId);
        cache.put(playerId, dataStore.loadPlayer(playerId, config.specVersion, config.achievements));
    }

    public void onBlockBreak(BlockBreakEvent event) {
        if (event.isCancelled()) {
            return;
        }
        handleEvent(event.getPlayer(), "block_break", event.getBlock().getType().name(), 1, event.getBlock().getLocation());
    }

    public void onEntityDeath(EntityDeathEvent event) {
        Player killer = event.getEntity().getKiller();
        if (killer == null) {
            return;
        }
        handleEvent(killer, "entity_kill", event.getEntityType().name(), 1, event.getEntity().getLocation());
    }

    public void onFish(PlayerFishEvent event) {
        if (event.getState() != PlayerFishEvent.State.CAUGHT_FISH) {
            return;
        }
        if (!(event.getCaught() instanceof Item itemEntity)) {
            return;
        }
        Material type = itemEntity.getItemStack().getType();
        handleEvent(event.getPlayer(), "fish_catch", type.name(), 1, event.getHook().getLocation());
    }

    public void onCraft(CraftItemEvent event) {
        if (event.isCancelled()) {
            return;
        }
        if (!(event.getWhoClicked() instanceof Player player)) {
            return;
        }
        ItemStack result = event.getRecipe().getResult();
        String item = result.getType().name();
        int crafts = event.isShiftClick() ? computeMaxCrafts(event.getInventory().getMatrix(), event.getRecipe()) : 1;
        int materialUnits = Math.max(1, countNonAir(event.getInventory().getMatrix())) * crafts;
        handleEvent(player, "item_craft", item, materialUnits, player.getLocation());
    }

    public void onTravel(PlayerMoveEvent event) {
        if (event.getFrom().getWorld() == null || event.getTo() == null) {
            return;
        }
        double distance = event.getFrom().distance(event.getTo());
        long delta = (long) Math.floor(distance);
        if (delta <= 0) {
            return;
        }
        handleTravel(event.getPlayer(), delta);
    }

    private void handleEvent(Player player, String type, String subject, long delta, Location location) {
        List<TierRef> refs = tiersByType.getOrDefault(type, Collections.emptyList());
        if (refs.isEmpty()) {
            return;
        }
        PlayerState state = getOrLoad(player);

        for (TierRef ref : refs) {
            AchievementProgress progress = state.achievementProgress.get(ref.definition.id);
            if (progress == null) {
                continue;
            }
            if (progress.nextTier > ref.definition.maxTier) {
                continue;
            }
            if (ref.tier.tier != progress.nextTier) {
                continue;
            }
            if (!criteriaMatches(ref.tier.criteria, subject, player, location)) {
                continue;
            }

            long applied = applyRateLimit(player.getUniqueId(), ref.definition.id, ref.tier.tier, delta, ref.tier.criteria);
            if (applied <= 0) {
                continue;
            }
            progress.progressAmount += applied;
            long required = requiredAmount(ref.tier.criteria);
            if (progress.progressAmount >= required) {
                completeTier(player, state, ref, progress);
            } else {
                if (shouldSendProgress(player.getUniqueId(), ref.definition.id)) {
                    sendProgressActionBar(player, ref, progress, required);
                }
            }
        }
    }

    private void handleTravel(Player player, long delta) {
        List<TierRef> refs = tiersByType.getOrDefault("travel", Collections.emptyList());
        if (refs.isEmpty()) {
            return;
        }
        PlayerState state = getOrLoad(player);

        for (TierRef ref : refs) {
            AchievementProgress progress = state.achievementProgress.get(ref.definition.id);
            if (progress == null) {
                continue;
            }
            if (progress.nextTier > ref.definition.maxTier) {
                continue;
            }
            if (ref.tier.tier != progress.nextTier) {
                continue;
            }
            if (!matchesBiome(ref.tier.criteria, player.getLocation()) || !matchesDimension(ref.tier.criteria, player.getLocation())) {
                continue;
            }
            long applied = applyRateLimit(player.getUniqueId(), ref.definition.id, ref.tier.tier, delta, ref.tier.criteria);
            if (applied <= 0) {
                continue;
            }
            progress.progressAmount += applied;
            long required = ref.tier.criteria.distanceBlocks;
            if (progress.progressAmount >= required) {
                completeTier(player, state, ref, progress);
            } else {
                if (shouldSendProgress(player.getUniqueId(), ref.definition.id)) {
                    sendProgressActionBar(player, ref, progress, required);
                }
            }
        }
    }

    private void completeTier(Player player, PlayerState state, TierRef ref, AchievementProgress progress) {
        int apAward = ref.tier.rewards.ap;
        progress.currentTier = ref.tier.tier;
        progress.nextTier = ref.tier.tier + 1;
        progress.progressAmount = 0;

        state.apTotal += apAward;
        state.achievementTierSum = computeTierSum(state);
        state.playerLevel = state.achievementTierSum;

        dataStore.insertObjectiveHistory(player.getUniqueId(), ref.definition.id, ref.tier.tier, apAward);

        sendAwardActionBar(player, ref, state, apAward);
        if (guiService != null) {
            guiService.refreshOpenMenus(player);
        }
    }

    public boolean adminComplete(Player player, AchievementDefinition def, int tier) {
        PlayerState state = getOrLoad(player);
        AchievementProgress progress = state.achievementProgress.get(def.id);
        if (progress == null || progress.nextTier != tier) {
            return false;
        }
        AchievementDefinition.AchievementTier tierDef = def.tiers.get(tier - 1);
        completeTier(player, state, new TierRef(def, tierDef), progress);
        return true;
    }

    private int computeTierSum(PlayerState state) {
        int sum = 0;
        for (AchievementProgress progress : state.achievementProgress.values()) {
            sum += Math.max(0, progress.currentTier);
        }
        return sum;
    }

    public int recomputeTierSum(PlayerState state) {
        return computeTierSum(state);
    }

    public long requiredAmount(Criteria criteria) {
        if ("travel".equals(criteria.type)) {
            return criteria.distanceBlocks;
        }
        return criteria.count;
    }

    private boolean criteriaMatches(Criteria criteria, String subject, Player player, Location location) {
        switch (criteria.type) {
            case "block_break" -> {
                if (!criteria.materials.contains(subject)) {
                    return false;
                }
                boolean toolOk = isWoodcuttingMaterial(subject) || toolMatches(criteria, player);
                return toolOk && matchesBiome(criteria, location) && matchesDimension(criteria, location);
            }
            case "item_craft" -> {
                return criteria.item.equals(subject);
            }
            case "entity_kill" -> {
                return criteria.entities.contains(subject) && matchesBiome(criteria, location) && matchesDimension(criteria, location);
            }
            case "fish_catch" -> {
                return criteria.items.contains(subject) && matchesFishConstraints(criteria, player, location);
            }
            default -> {
                return false;
            }
        }
    }

    private boolean matchesFishConstraints(Criteria criteria, Player player, Location location) {
        if (!matchesBiome(criteria, location) || !matchesDimension(criteria, location)) {
            return false;
        }
        if (criteria.constraints == null) {
            return true;
        }
        Criteria.Constraints constraints = criteria.constraints;
        if (Boolean.TRUE.equals(constraints.inRainOnly) && !player.getWorld().hasStorm()) {
            return false;
        }
        if (Boolean.TRUE.equals(constraints.openWaterOnly)) {
            FishHook hook = player.getFishHook();
            if (hook == null || !hook.isInOpenWater()) {
                return false;
            }
        }
        return true;
    }

    private boolean matchesBiome(Criteria criteria, Location location) {
        if (criteria.constraints == null || criteria.constraints.biomes == null || criteria.constraints.biomes.isEmpty()) {
            return true;
        }
        if (location == null) {
            return false;
        }
        Biome biome = location.getBlock().getBiome();
        return criteria.constraints.biomes.contains(biome.name());
    }

    private boolean matchesDimension(Criteria criteria, Location location) {
        if (criteria.constraints == null || criteria.constraints.dimensions == null || criteria.constraints.dimensions.isEmpty()) {
            return true;
        }
        if (location == null) {
            return false;
        }
        World.Environment env = location.getWorld().getEnvironment();
        String dimension = switch (env) {
            case NETHER -> "nether";
            case THE_END -> "end";
            default -> "overworld";
        };
        return criteria.constraints.dimensions.contains(dimension);
    }

    private boolean toolMatches(Criteria criteria, Player player) {
        if (criteria.constraints == null || criteria.constraints.tool == null) {
            return true;
        }
        Criteria.ToolConstraint tool = criteria.constraints.tool;
        if (tool.required == null || !tool.required) {
            return true;
        }
        Material held = player.getInventory().getItemInMainHand().getType();
        String type = tool.type == null ? "" : tool.type.toLowerCase(Locale.ROOT);
        return switch (type) {
            case "axe" -> held.name().endsWith("_AXE");
            case "pickaxe" -> held.name().endsWith("_PICKAXE");
            case "shovel" -> held.name().endsWith("_SHOVEL");
            case "hoe" -> held.name().endsWith("_HOE");
            default -> true;
        };
    }

    private boolean isWoodcuttingMaterial(String subject) {
        if (subject == null) {
            return false;
        }
        return subject.endsWith("_LOG")
                || subject.endsWith("_WOOD")
                || subject.endsWith("_STEM")
                || subject.endsWith("_HYPHAE")
                || subject.endsWith("_LOGS");
    }

    private long applyRateLimit(UUID playerId, String achievementId, int tier, long delta, Criteria criteria) {
        if (criteria.constraints == null || criteria.constraints.rateLimit == null) {
            return delta;
        }
        int maxPerMinute = criteria.constraints.rateLimit.maxProgressPerMinute;
        if (maxPerMinute <= 0) {
            return delta;
        }
        String key = playerId + ":" + achievementId + ":" + tier;
        long now = Instant.now().toEpochMilli();
        RateWindow window = rateWindow.computeIfAbsent(key, k -> new RateWindow());
        synchronized (window) {
            if (now - window.windowStart >= 60000) {
                window.windowStart = now;
                window.progress = 0;
            }
            long remaining = maxPerMinute - window.progress;
            long applied = Math.max(0, Math.min(delta, remaining));
            window.progress += applied;
            return applied;
        }
    }

    private void sendAwardActionBar(Player player, TierRef ref, PlayerState state, int apAward) {
        int nextTier = ref.tier.tier + 1;
        String next = nextTier > ref.definition.maxTier ? "MAX" : nextTier + ": " + truncate(ref.definition.tiers.get(nextTier - 1).title, 16);
        long target = nextTier > ref.definition.maxTier ? 0 : requiredAmount(ref.definition.tiers.get(nextTier - 1).criteria);
        String msg = "+" + apAward + " AP | " + ref.definition.name + " " + ref.tier.tier + " | AP " + state.apTotal + " | Prog 0/" + target + " | Next " + next;
        player.sendActionBar(Component.text(msg));
    }

    private void sendProgressActionBar(Player player, TierRef ref, AchievementProgress progress, long required) {
        String msg = ref.definition.name + " " + progress.currentTier + " | Prog " + progress.progressAmount + "/" + required + " | Obj " + truncate(ref.tier.title, 16);
        player.sendActionBar(Component.text(msg));
    }

    private boolean shouldSendProgress(UUID playerId, String achievementId) {
        String key = playerId + ":" + achievementId;
        long now = Instant.now().toEpochMilli();
        Long last = progressThrottle.get(key);
        if (last == null || now - last >= 250) {
            progressThrottle.put(key, now);
            return true;
        }
        return false;
    }

    private String truncate(String text, int max) {
        if (text == null) {
            return "";
        }
        if (text.length() <= max) {
            return text;
        }
        return text.substring(0, max);
    }

    private int countNonAir(ItemStack[] matrix) {
        int count = 0;
        for (ItemStack item : matrix) {
            if (item != null && item.getType() != Material.AIR) {
                count++;
            }
        }
        return count;
    }

    private int computeMaxCrafts(ItemStack[] matrix, Recipe recipe) {
        if (recipe instanceof ShapelessRecipe shapeless) {
            return maxCraftsForIngredients(matrix, shapeless.getIngredientList());
        }
        if (recipe instanceof ShapedRecipe shaped) {
            return maxCraftsForIngredients(matrix, shaped.getIngredientMap().values().stream().filter(Objects::nonNull).toList());
        }
        return 1;
    }

    private int maxCraftsForIngredients(ItemStack[] matrix, List<ItemStack> ingredients) {
        Map<Material, Integer> required = new HashMap<>();
        for (ItemStack ingredient : ingredients) {
            if (ingredient == null || ingredient.getType() == Material.AIR) {
                continue;
            }
            required.merge(ingredient.getType(), ingredient.getAmount(), Integer::sum);
        }
        Map<Material, Integer> available = new HashMap<>();
        for (ItemStack item : matrix) {
            if (item == null || item.getType() == Material.AIR) {
                continue;
            }
            available.merge(item.getType(), item.getAmount(), Integer::sum);
        }
        int crafts = Integer.MAX_VALUE;
        for (Map.Entry<Material, Integer> entry : required.entrySet()) {
            int have = available.getOrDefault(entry.getKey(), 0);
            crafts = Math.min(crafts, have / entry.getValue());
        }
        return crafts == Integer.MAX_VALUE ? 1 : Math.max(1, crafts);
    }

    public static class TierRef {
        public final AchievementDefinition definition;
        public final AchievementDefinition.AchievementTier tier;

        public TierRef(AchievementDefinition definition, AchievementDefinition.AchievementTier tier) {
            this.definition = definition;
            this.tier = tier;
        }
    }

    private static class RateWindow {
        long windowStart = 0;
        long progress = 0;
    }
}
