package dev.pcrykh.util;

public final class RomanNumerals {
    private RomanNumerals() {
    }

    public static String toRoman(int value) {
        if (value <= 0) {
            return "";
        }
        int[] nums = {1000, 900, 500, 400, 100, 90, 50, 40, 10, 9, 5, 4, 1};
        String[] romans = {"M", "CM", "D", "CD", "C", "XC", "L", "XL", "X", "IX", "V", "IV", "I"};
        StringBuilder builder = new StringBuilder();
        int remaining = value;
        for (int i = 0; i < nums.length; i++) {
            while (remaining >= nums[i]) {
                builder.append(romans[i]);
                remaining -= nums[i];
            }
        }
        return builder.toString();
    }
}
