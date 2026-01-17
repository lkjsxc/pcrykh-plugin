package dev.pcrykh.pcrykh.config;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public record AchievementPack(
        @JsonProperty("pack_id") String packId,
        List<AchievementTemplate> templates
) {
}
