package dev.pcrykh.pcrykh.achievement;

import dev.pcrykh.pcrykh.config.AchievementPack;
import dev.pcrykh.pcrykh.config.AchievementTemplate;
import dev.pcrykh.pcrykh.config.TierSpec;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

public final class AchievementPackExpander {
    private AchievementPackExpander() {
    }

    public static List<AchievementDefinition> expand(AchievementPack pack) {
        List<AchievementDefinition> results = new ArrayList<>();
        Set<String> ids = new HashSet<>();
        for (AchievementTemplate template : pack.templates()) {
            results.addAll(expandTemplate(template, ids));
        }
        return results;
    }

    private static List<AchievementDefinition> expandTemplate(AchievementTemplate template, Set<String> ids) {
        List<AchievementDefinition> definitions = new ArrayList<>();
        TierSpec tiers = template.tiers();
        List<String> subjects = template.subjects();
        for (String subject : subjects) {
            String subjectId = slugify(subject);
            for (int tierIndex = 1; tierIndex <= tiers.levels(); tierIndex++) {
                Map<String, String> tokens = new HashMap<>();
                tokens.put("subject", subject);
                tokens.put("subject_id", subjectId);
                tokens.put("tier_index", String.valueOf(tierIndex));
                tokens.put("tier", toRoman(tierIndex));
                tokens.put("count", String.valueOf(tiers.countForTier(tierIndex)));
                tokens.put("ap", String.valueOf(tiers.apForTier(tierIndex)));

                String id = replaceTokens(template.idTemplate(), tokens);
                if (!ids.add(id)) {
                    throw new IllegalStateException("Duplicate achievement id: " + id);
                }

                definitions.add(new AchievementDefinition(
                        id,
                        replaceTokens(template.nameTemplate(), tokens),
                        template.categoryId(),
                        replaceTokens(template.iconTemplate(), tokens),
                        replaceTokens(template.titleTemplate(), tokens),
                        replaceTokens(template.descriptionTemplate(), tokens),
                        replaceTokensInObject(template.criteriaTemplate(), tokens),
                        replaceTokensInObject(template.rewardsTemplate(), tokens)
                ));
            }
        }
        return definitions;
    }

    private static String replaceTokens(String input, Map<String, String> tokens) {
        String output = input;
        for (Map.Entry<String, String> entry : tokens.entrySet()) {
            output = output.replace("{" + entry.getKey() + "}", entry.getValue());
        }
        return output;
    }

    @SuppressWarnings("unchecked")
    private static Map<String, Object> replaceTokensInObject(Map<String, Object> template, Map<String, String> tokens) {
        Map<String, Object> result = new HashMap<>();
        for (Map.Entry<String, Object> entry : template.entrySet()) {
            result.put(entry.getKey(), replaceInValue(entry.getValue(), tokens));
        }
        return result;
    }

    @SuppressWarnings("unchecked")
    private static Object replaceInValue(Object value, Map<String, String> tokens) {
        if (value instanceof String text) {
            return coerceNumber(replaceTokens(text, tokens));
        }
        if (value instanceof List<?> list) {
            List<Object> replaced = new ArrayList<>();
            for (Object item : list) {
                replaced.add(replaceInValue(item, tokens));
            }
            return replaced;
        }
        if (value instanceof Map<?, ?> map) {
            Map<String, Object> replaced = new HashMap<>();
            for (Map.Entry<?, ?> entry : map.entrySet()) {
                replaced.put(String.valueOf(entry.getKey()), replaceInValue(entry.getValue(), tokens));
            }
            return replaced;
        }
        return value;
    }

    private static Object coerceNumber(String value) {
        if (value.matches("^-?\\d+$")) {
            return Integer.parseInt(value);
        }
        return value;
    }

    private static String slugify(String raw) {
        return raw.toLowerCase(Locale.ROOT).replaceAll("[^a-z0-9]+", "_").replaceAll("^_+|_+$", "");
    }

    private static String toRoman(int number) {
        int[] values = {50, 40, 10, 9, 5, 4, 1};
        String[] numerals = {"L", "XL", "X", "IX", "V", "IV", "I"};
        StringBuilder builder = new StringBuilder();
        int remaining = number;
        for (int i = 0; i < values.length; i++) {
            while (remaining >= values[i]) {
                builder.append(numerals[i]);
                remaining -= values[i];
            }
        }
        return builder.toString();
    }
}
