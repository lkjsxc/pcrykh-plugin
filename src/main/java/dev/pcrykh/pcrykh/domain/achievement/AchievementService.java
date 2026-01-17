package dev.pcrykh.pcrykh.domain.achievement;

import dev.pcrykh.pcrykh.config.PluginConfig;
import dev.pcrykh.pcrykh.domain.model.AchievementDefinition;
import dev.pcrykh.pcrykh.domain.model.Criteria;
import dev.pcrykh.pcrykh.data.storage.DataStore;
import dev.pcrykh.pcrykh.data.storage.DataStore.AchievementProgress;
import dev.pcrykh.pcrykh.data.storage.DataStore.PlayerState;
import dev.pcrykh.pcrykh.interaction.gui.GuiService;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Biome;
import org.bukkit.entity.Boat;
import org.bukkit.entity.Entity;
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
    private final Map<String, List<AchievementDefinition>> achievementsByType = new HashMap<>();
    private final Map<UUID, PlayerState> cache = new ConcurrentHashMap<>();
    private final Map<String, RateWindow> rateWindow = new ConcurrentHashMap<>();
    private final Map<String, Long> progressThrottle = new ConcurrentHashMap<>();
    private final Map<UUID, Double> travelRemainder = new ConcurrentHashMap<>();

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
        achievementsByType.clear();
        indexAchievements();
    }

    public void updateConfig(PluginConfig config) {
        this.config = config;
        reload();
    }

    private void indexAchievements() {
        for (AchievementDefinition def : config.achievements) {
            achievementById.put(def.id, def);
            String type = def.criteria.type;
            achievementsByType.computeIfAbsent(type, k -> new ArrayList<>())
                    .add(def);
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
        UUID playerId = event.getPlayer().getUniqueId();
        double remainder = travelRemainder.getOrDefault(playerId, 0.0);
        remainder += distance;
        long delta = (long) Math.floor(remainder);
        if (delta <= 0) {
            travelRemainder.put(playerId, remainder);
            return;
        }
        travelRemainder.put(playerId, remainder - delta);
        Player player = event.getPlayer();
        handleTravelType("travel", player, delta);
        handleTravelType("travel_walk", player, delta);
        handleTravelType("travel_sprint", player, delta);
        handleTravelType("travel_swim", player, delta);
        handleTravelType("travel_crouch", player, delta);
        handleTravelType("travel_fly", player, delta);
        handleTravelType("travel_mount", player, delta);
        handleTravelType("travel_boat", player, delta);
        handleTravelType("travel_boat_with_animal", player, delta);
    }
    
    public void onJump(Player player) {
        handleEvent(player, "jump", "jump", 1, player.getLocation());
    }

    private void handleEvent(Player player, String type, String subject, long delta, Location location) {
        List<AchievementDefinition> defs = achievementsByType.getOrDefault(type, Collections.emptyList());
        if (defs.isEmpty()) {
            return;
        }
        PlayerState state = getOrLoad(player);

        for (AchievementDefinition def : defs) {
            AchievementProgress progress = state.achievementProgress.get(def.id);
            if (progress == null) {
                continue;
            }
            if (progress.completed) {
                continue;
            }
            if (!criteriaMatches(def.criteria, subject, player, location)) {
                continue;
            }

            long applied = applyRateLimit(player.getUniqueId(), def.id, delta, def.criteria);
            if (applied <= 0) {
                continue;
            }
            progress.progressAmount += applied;
            long required = requiredAmount(def.criteria);
            if (progress.progressAmount >= required) {
                completeAchievement(player, state, def, progress, required);
            } else {
                if (shouldSendProgress(player.getUniqueId(), def.id)) {
                    sendProgressActionBar(player, def, progress, required);
                }
            }
        }
    }

    private void handleTravelType(String type, Player player, long delta) {
        List<AchievementDefinition> defs = achievementsByType.getOrDefault(type, Collections.emptyList());
        if (defs.isEmpty()) {
            return;
        }
        PlayerState state = getOrLoad(player);

        for (AchievementDefinition def : defs) {
            AchievementProgress progress = state.achievementProgress.get(def.id);
            if (progress == null) {
                continue;
            }
            if (progress.completed) {
                continue;
            }
            if (!matchesTravelState(type, def.criteria, player)) {
                continue;
            }
            if (!matchesBiome(def.criteria, player.getLocation()) || !matchesDimension(def.criteria, player.getLocation())) {
                continue;
            }
            long applied = applyRateLimit(player.getUniqueId(), def.id, delta, def.criteria);
            if (applied <= 0) {
                continue;
            }
            progress.progressAmount += applied;
            long required = def.criteria.distanceBlocks;
            if (progress.progressAmount >= required) {
                completeAchievement(player, state, def, progress, required);
            } else {
                if (shouldSendProgress(player.getUniqueId(), def.id)) {
                    sendProgressActionBar(player, def, progress, required);
                }
            }
        }
    }

    private boolean matchesTravelState(String type, Criteria criteria, Player player) {
        return switch (type) {
            case "travel" -> true;
            case "travel_walk" -> isWalking(player);
            case "travel_sprint" -> isSprinting(player);
            case "travel_swim" -> isSwimming(player);
            case "travel_crouch" -> isCrouching(player);
            case "travel_fly" -> isFlying(player);
            case "travel_mount" -> isMounted(player) && vehicleMatches(criteria, player.getVehicle());
            case "travel_boat" -> isBoating(player) && vehicleMatches(criteria, player.getVehicle());
            case "travel_boat_with_animal" -> isBoating(player) && vehicleMatches(criteria, player.getVehicle()) && passengerMatches(criteria, player.getVehicle());
            default -> false;
        };
    }

    private boolean isWalking(Player player) {
        return !player.isSprinting() && !player.isSneaking() && !player.isSwimming() && !player.isFlying() && !player.isGliding() && !player.isInsideVehicle();
    }

    private boolean isSprinting(Player player) {
        return player.isSprinting() && !player.isSwimming() && !player.isFlying() && !player.isGliding() && !player.isInsideVehicle();
    }

    private boolean isSwimming(Player player) {
        return player.isSwimming() && !player.isInsideVehicle();
    }

    private boolean isCrouching(Player player) {
        return player.isSneaking() && !player.isSwimming() && !player.isFlying() && !player.isGliding() && !player.isInsideVehicle();
    }

    private boolean isFlying(Player player) {
        return (player.isFlying() || player.isGliding()) && !player.isInsideVehicle();
    }

    private boolean isBoating(Player player) {
        return player.isInsideVehicle() && player.getVehicle() instanceof Boat;
    }

    private boolean isMounted(Player player) {
        return player.isInsideVehicle() && !(player.getVehicle() instanceof Boat);
    }

    private boolean vehicleMatches(Criteria criteria, Entity vehicle) {
        if (vehicle == null) {
            return false;
        }
        if (criteria.vehicles == null || criteria.vehicles.isEmpty()) {
            return true;
        }
        return criteria.vehicles.contains(vehicle.getType().name());
    }

    private boolean passengerMatches(Criteria criteria, Entity vehicle) {
        if (vehicle == null) {
            return false;
        }
        if (criteria.passengers == null || criteria.passengers.isEmpty()) {
            return false;
        }
        for (Entity passenger : vehicle.getPassengers()) {
            if (passenger instanceof Player) {
                continue;
            }
            if (criteria.passengers.contains(passenger.getType().name())) {
                return true;
            }
        }
        return false;
    }

    private void completeAchievement(Player player, PlayerState state, AchievementDefinition def, AchievementProgress progress, long required) {
        int apAward = def.rewards.ap;
        progress.completed = true;
        progress.progressAmount = required;

        state.apTotal += apAward;
        state.achievementTierSum = computeCompletionSum(state);
        state.playerLevel = state.achievementTierSum;

        dataStore.insertObjectiveHistory(player.getUniqueId(), def.id, apAward);

        sendAwardActionBar(player, def, state, apAward);
        if (config.runtime != null && config.runtime.chat != null && config.runtime.chat.announceAchievements) {
            String msg = "Achievement completed: " + def.name + " (+" + apAward + " AP, Total AP " + state.apTotal + ")";
            Bukkit.getServer().broadcast(Component.text(msg));
        }
        if (guiService != null) {
            guiService.refreshOpenMenus(player);
        }
    }

    public boolean adminComplete(Player player, AchievementDefinition def) {
        PlayerState state = getOrLoad(player);
        AchievementProgress progress = state.achievementProgress.get(def.id);
        if (progress == null || progress.completed) {
            return false;
        }
        long required = requiredAmount(def.criteria);
        completeAchievement(player, state, def, progress, required);
        return true;
    }

    private int computeCompletionSum(PlayerState state) {
        int sum = 0;
        for (AchievementProgress progress : state.achievementProgress.values()) {
            if (progress.completed) {
                sum += 1;
            }
        }
        return sum;
    }

    public int recomputeCompletionSum(PlayerState state) {
        return computeCompletionSum(state);
    }

    public long requiredAmount(Criteria criteria) {
        if (criteria.type != null && criteria.type.startsWith("travel")) {
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
            case "jump" -> {
                return true;
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

    private long applyRateLimit(UUID playerId, String achievementId, long delta, Criteria criteria) {
        if (criteria.constraints == null || criteria.constraints.rateLimit == null) {
            return delta;
        }
        int maxPerMinute = criteria.constraints.rateLimit.maxProgressPerMinute;
        if (maxPerMinute <= 0) {
            return delta;
        }
        String key = playerId + ":" + achievementId;
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

    private void sendAwardActionBar(Player player, AchievementDefinition def, PlayerState state, int apAward) {
        String msg = "+" + apAward + " AP | " + def.name + " | AP " + state.apTotal + " | Completed";
        player.sendActionBar(Component.text(msg));
    }

    private void sendProgressActionBar(Player player, AchievementDefinition def, AchievementProgress progress, long required) {
        String msg = def.name + " | Prog " + progress.progressAmount + "/" + required + " | Obj " + truncate(def.title, 16);
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

    private static class RateWindow {
        long windowStart = 0;
        long progress = 0;
    }
}
