package dev.pcrykh.runtime;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CategoryCatalog {
    private final Map<String, CategoryDefinition> byId = new HashMap<>();
    private final List<CategoryDefinition> ordered;

    public CategoryCatalog(List<PackDefinition> packs) {
        for (PackDefinition pack : packs) {
            for (CategoryDefinition category : pack.categories()) {
                CategoryDefinition existing = byId.get(category.id());
                if (existing != null && !existing.equals(category)) {
                    throw new ConfigException("Duplicate category id with differing fields: " + category.id());
                }
                byId.put(category.id(), category);
            }
        }
        if (byId.isEmpty()) {
            throw new ConfigException("No categories defined in achievement packs");
        }
        ordered = new ArrayList<>(byId.values());
        ordered.sort(Comparator.comparingInt(CategoryDefinition::order)
                .thenComparing(CategoryDefinition::name)
                .thenComparing(CategoryDefinition::id));
    }

    public CategoryDefinition get(String id) {
        return byId.get(id);
    }

    public List<CategoryDefinition> ordered() {
        return ordered;
    }
}
