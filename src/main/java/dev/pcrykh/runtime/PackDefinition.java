package dev.pcrykh.runtime;

import java.util.List;

public record PackDefinition(String packId, List<CategoryDefinition> categories, List<TemplateDefinition> templates) {
}
