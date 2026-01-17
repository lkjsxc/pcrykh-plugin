package dev.pcrykh.util;

public final class Slugify {
    private Slugify() {
    }

    public static String slugify(String input) {
        String lower = input.toLowerCase();
        StringBuilder builder = new StringBuilder();
        boolean lastUnderscore = false;
        for (int i = 0; i < lower.length(); i++) {
            char c = lower.charAt(i);
            if ((c >= 'a' && c <= 'z') || (c >= '0' && c <= '9')) {
                builder.append(c);
                lastUnderscore = false;
            } else {
                if (!lastUnderscore) {
                    builder.append('_');
                    lastUnderscore = true;
                }
            }
        }
        String result = builder.toString();
        result = result.replaceAll("_+", "_");
        result = result.replaceAll("^_+", "");
        result = result.replaceAll("_+$", "");
        return result;
    }
}
