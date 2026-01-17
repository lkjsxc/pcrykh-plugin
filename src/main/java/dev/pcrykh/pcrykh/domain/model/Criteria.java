package dev.pcrykh.pcrykh.model;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public class Criteria {
    public String type;
    public int count;
    public List<String> materials;
    public String item;
    public List<String> entities;
    public List<String> items;
    public List<String> vehicles;
    public List<String> passengers;
    @JsonProperty("distance_blocks")
    public int distanceBlocks;
    public Constraints constraints;

    public static class Constraints {
        public ToolConstraint tool;
        @JsonProperty("rate_limit")
        public RateLimit rateLimit;
        @JsonProperty("by_player_only")
        public Boolean byPlayerOnly;
        @JsonProperty("open_water_only")
        public Boolean openWaterOnly;
        @JsonProperty("in_rain_only")
        public Boolean inRainOnly;
        public List<String> biomes;
        public List<String> dimensions;
    }

    public static class ToolConstraint {
        public Boolean required;
        public String type;
    }

    public static class RateLimit {
        @JsonProperty("max_progress_per_minute")
        public int maxProgressPerMinute;
    }
}
