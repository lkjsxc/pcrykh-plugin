package dev.pcrykh.runtime;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public final class AchievementSourceResolver {
    private AchievementSourceResolver() {
    }

    public static List<File> collectJsonFiles(File directory) throws ConfigException {
        List<File> files = new ArrayList<>();
        try {
            Files.walk(directory.toPath())
                .filter(path -> path.toFile().isFile())
                .filter(path -> path.getFileName().toString().endsWith(".json"))
                .sorted(Comparator.comparing(path -> path.toString()))
                .forEach(path -> files.add(path.toFile()));
        } catch (IOException ex) {
            throw new ConfigException("Failed to scan achievement source directory: " + directory.getPath());
        }
        return files;
    }
}
