package dev.pcrykh.pcrykh.config;

import com.fasterxml.jackson.annotation.JsonProperty;

public record TierSpec(
        int levels,
        @JsonProperty("count_start") int countStart,
        @JsonProperty("count_multiplier") double countMultiplier,
        @JsonProperty("ap_start") int apStart,
        @JsonProperty("ap_multiplier") double apMultiplier
) {
    public int countForTier(int tierIndex) {
        return (int) Math.round(countStart * Math.pow(countMultiplier, tierIndex - 1));
    }

    public int apForTier(int tierIndex) {
        return (int) Math.round(apStart * Math.pow(apMultiplier, tierIndex - 1));
    }
}
