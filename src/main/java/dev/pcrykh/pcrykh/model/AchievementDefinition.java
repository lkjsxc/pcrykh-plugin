package dev.pcrykh.pcrykh.model;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public class AchievementDefinition {
    public String id;
    public String name;
    @JsonProperty("category_id")
    public String categoryId;
    public String icon;
    @JsonProperty("max_tier")
    public int maxTier;
    public List<AchievementTier> tiers;

    public static class AchievementTier {
        public int tier;
        public String title;
        public String description;
        public Criteria criteria;
        public Reward rewards;
    }
}
