package dev.pcrykh.runtime;

import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;

import java.util.List;
import java.util.Random;

public class FactsBroadcaster {
    private final Plugin plugin;
    private final RuntimeConfig config;
    private final Random random = new Random();
    private String lastFact;

    public FactsBroadcaster(Plugin plugin, RuntimeConfig config) {
        this.plugin = plugin;
        this.config = config;
    }

    public void start() {
        int intervalTicks = Math.max(20, config.chat().factsIntervalSeconds() * 20);
        plugin.getServer().getScheduler().runTaskTimer(plugin, this::broadcastIfEnabled, intervalTicks, intervalTicks);
    }

    private void broadcastIfEnabled() {
        if (!config.chat().factsEnabled()) {
            return;
        }
        List<String> facts = config.facts();
        if (facts == null || facts.isEmpty()) {
            return;
        }
        String fact = pickFact(facts);
        if (fact == null || fact.isBlank()) {
            return;
        }
        Bukkit.getServer().broadcast(Component.text(config.chat().prefix() + fact));
    }

    private String pickFact(List<String> facts) {
        if (facts.size() == 1) {
            return facts.get(0);
        }
        String selected = facts.get(random.nextInt(facts.size()));
        if (selected.equals(lastFact)) {
            selected = facts.get((facts.indexOf(selected) + 1) % facts.size());
        }
        lastFact = selected;
        return selected;
    }
}
