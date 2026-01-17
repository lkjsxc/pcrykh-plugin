package dev.pcrykh.domain;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import dev.pcrykh.runtime.ConfigException;
import dev.pcrykh.util.JsonUtils;
import java.util.Iterator;
import java.util.Map;

public final class RewardValidator {
    private RewardValidator() {
    }

    public static void validateRewards(JsonNode rewards) throws ConfigException {
        if (rewards == null || !rewards.isObject()) {
            throw new ConfigException("rewards must be an object.");
        }
        ObjectNode object = (ObjectNode) rewards;
        Iterator<Map.Entry<String, JsonNode>> fields = object.fields();
        while (fields.hasNext()) {
            Map.Entry<String, JsonNode> entry = fields.next();
            validateRewardDefinition(entry.getKey(), entry.getValue());
        }
    }

    private static void validateRewardDefinition(String key, JsonNode node) throws ConfigException {
        if (node == null || !node.isObject()) {
            throw new ConfigException("reward '" + key + "' must be an object.");
        }
        try {
            int ap = JsonUtils.requireInt(node, "ap");
            if (ap < 0) {
                throw new ConfigException("reward '" + key + "' ap must be >= 0.");
            }
        } catch (IllegalArgumentException ex) {
            throw new ConfigException(ex.getMessage());
        }
    }
}
