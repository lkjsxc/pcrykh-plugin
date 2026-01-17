package dev.pcrykh.pcrykh.domain.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public class AchievementDefinition {
    public String id;
    public String name;
    @JsonProperty("category_id")
    public String categoryId;
    public String icon;
    public String title;
    public String description;
    public Criteria criteria;
    public Reward rewards;
}
