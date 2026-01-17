package dev.pcrykh.runtime;

import java.util.List;

public record PackDefinition(String packId, List<TemplateDefinition> templates) {
}
