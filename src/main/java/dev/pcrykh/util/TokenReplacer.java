package dev.pcrykh.util;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.databind.node.TextNode;
import java.util.Iterator;
import java.util.Map;

public final class TokenReplacer {
    private TokenReplacer() {
    }

    public static JsonNode replace(JsonNode node, ObjectMapper mapper, TokenContext context) {
        if (node == null || node.isNull()) {
            return node;
        }
        if (node.isTextual()) {
            String replaced = replaceTokens(node.asText(), context);
            return TextNode.valueOf(replaced);
        }
        if (node.isArray()) {
            ArrayNode array = mapper.createArrayNode();
            for (JsonNode item : node) {
                array.add(replace(item, mapper, context));
            }
            return array;
        }
        if (node.isObject()) {
            ObjectNode object = mapper.createObjectNode();
            Iterator<Map.Entry<String, JsonNode>> fields = node.fields();
            while (fields.hasNext()) {
                Map.Entry<String, JsonNode> entry = fields.next();
                object.set(entry.getKey(), replace(entry.getValue(), mapper, context));
            }
            return object;
        }
        return node.deepCopy();
    }

    public static String replaceTokens(String input, TokenContext context) {
        return input
            .replace("{subject}", context.subject())
            .replace("{subject_id}", context.subjectId())
            .replace("{tier}", context.tier())
            .replace("{tier_index}", Integer.toString(context.tierIndex()))
            .replace("{count}", Integer.toString(context.count()))
            .replace("{ap}", Integer.toString(context.ap()));
    }
}
