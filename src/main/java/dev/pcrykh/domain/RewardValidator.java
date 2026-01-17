package dev.pcrykh.domain;

import com.fasterxml.jackson.databind.JsonNode;
import dev.pcrykh.runtime.ConfigException;

public class RewardValidator {
    public void validate(JsonNode rewards) {
        if (rewards == null || !rewards.isObject()) {
            throw new ConfigException("rewards must be an object");
        }
        JsonNode ap = rewards.get("ap");
        if (ap == null || !ap.canConvertToInt()) {
            throw new ConfigException("rewards.ap must be an integer");
        }
        if (rewards.size() != 1) {
            throw new ConfigException("rewards must only contain ap");
        }
    }
}
