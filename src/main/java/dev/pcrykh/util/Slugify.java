package dev.pcrykh.util;

public class Slugify {
    public static String slugify(String input) {
        if (input == null || input.isBlank()) {
            return "";
        }
        String lower = input.toLowerCase();
        String replaced = lower.replaceAll("[^a-z0-9]+", "_");
        String collapsed = replaced.replaceAll("_+", "_");
        return collapsed.replaceAll("^_+|_+$", "");
    }
}
