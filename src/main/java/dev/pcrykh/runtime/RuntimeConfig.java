package dev.pcrykh.runtime;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import java.util.List;

public record RuntimeConfig(
    String specVersion,
    String commandsRoot,
    JsonNode runtimeAutosave,
    JsonNode runtimeChat,
    ArrayNode tips,
    List<String> achievementSources
) {}
