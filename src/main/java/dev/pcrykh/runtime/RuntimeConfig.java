package dev.pcrykh.runtime;

import com.fasterxml.jackson.databind.JsonNode;

import java.util.List;

public record RuntimeConfig(
        String specVersion,
        String commandRoot,
        JsonNode runtimeAutosave,
        JsonNode runtimeChat,
        JsonNode tips,
        List<String> achievementSources
) {
}
