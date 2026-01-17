package dev.pcrykh.util;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.databind.node.TextNode;

import java.util.Iterator;
import java.util.Map;

public class TokenReplacer {
    public static String replace(String input, TokenContext context) {
        if (input == null) {
            return "";
        }
        return input
                .replace("{subject}", context.subject())
                .replace("{subject_id}", context.subjectId())
                .replace("{tier}", context.tier())
                .replace("{tier_index}", Integer.toString(context.tierIndex()))
                .replace("{count}", Integer.toString(context.count()))
                .replace("{ap}", Integer.toString(context.ap()));
    }

    public static JsonNode replaceJson(JsonNode node, TokenContext context, ObjectMapper mapper) {
        if (node == null) {
            return null;
        }
        if (node.isTextual()) {
            return new TextNode(replace(node.asText(), context));
        }
        if (node.isObject()) {
            ObjectNode copy = mapper.createObjectNode();
            Iterator<Map.Entry<String, JsonNode>> fields = node.fields();
            while (fields.hasNext()) {
                Map.Entry<String, JsonNode> entry = fields.next();
                copy.set(entry.getKey(), replaceJson(entry.getValue(), context, mapper));
            }
            return copy;
        }
        if (node.isArray()) {
            ArrayNode copy = mapper.createArrayNode();
            for (JsonNode child : node) {
                copy.add(replaceJson(child, context, mapper));
            }
            return copy;
        }
        return node.deepCopy();
    }
}
