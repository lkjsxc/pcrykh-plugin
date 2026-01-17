package dev.pcrykh.pcrykh.config;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import dev.pcrykh.pcrykh.achievement.AchievementDefinition;
import dev.pcrykh.pcrykh.achievement.AchievementPackExpander;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class ConfigLoader {
    public static final int CONFIG_LINE_LIMIT = 300;

    private final ObjectMapper mapper;

    public ConfigLoader() {
        this.mapper = new ObjectMapper()
                .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, true);
    }

    public RuntimeConfig loadConfig(Path dataDirectory) throws IOException {
        Path configPath = dataDirectory.resolve("config.json");
        enforceLineLimit(configPath);
        return mapper.readValue(configPath.toFile(), RuntimeConfig.class);
    }

    public List<AchievementDefinition> loadAchievements(RuntimeConfig config, Path dataDirectory) throws IOException {
        List<Path> sources = resolveSources(config.achievementSources(), dataDirectory);
        List<AchievementDefinition> achievements = new ArrayList<>();
        for (Path source : sources) {
            AchievementPack pack = mapper.readValue(source.toFile(), AchievementPack.class);
            achievements.addAll(AchievementPackExpander.expand(pack));
        }
        return achievements;
    }

    private void enforceLineLimit(Path configPath) throws IOException {
        long lines = Files.lines(configPath).count();
        if (lines > CONFIG_LINE_LIMIT) {
            throw new IllegalStateException("config.json exceeds line limit (" + lines + " > " + CONFIG_LINE_LIMIT + ")");
        }
    }

    private List<Path> resolveSources(List<String> sources, Path dataDirectory) throws IOException {
        List<Path> resolved = new ArrayList<>();
        for (String source : sources) {
            Path path = dataDirectory.resolve(source);
            if (Files.isDirectory(path)) {
                try (Stream<Path> stream = Files.walk(path)) {
                    resolved.addAll(stream
                            .filter(file -> file.toString().toLowerCase(Locale.ROOT).endsWith(".json"))
                            .sorted(Comparator.naturalOrder())
                            .collect(Collectors.toList()));
                }
            } else if (Files.exists(path)) {
                resolved.add(path);
            } else {
                throw new IllegalStateException("Achievement source not found: " + source);
            }
        }
        return resolved;
    }
}
