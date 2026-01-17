package dev.pcrykh.runtime;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class PackLoader {
    private final ObjectMapper mapper;

    public PackLoader(ObjectMapper mapper) {
        this.mapper = mapper;
    }

    public List<PackDefinition> loadAll(List<Path> packFiles) {
        List<PackDefinition> packs = new ArrayList<>();
        for (Path path : packFiles) {
            packs.add(loadPack(path));
        }
        return packs;
    }

    public PackDefinition loadPack(Path path) {
        try {
            JsonNode root = mapper.readTree(path.toFile());
            require(root, "pack_id");
            require(root, "categories");
            require(root, "templates");

            String packId = root.get("pack_id").asText();
            if (packId.isBlank()) {
                throw new ConfigException("pack_id must be non-empty: " + path);
            }

            JsonNode templatesNode = root.get("templates");
            if (!templatesNode.isArray() || templatesNode.size() == 0) {
                throw new ConfigException("templates must be a non-empty array: " + path);
            }

            JsonNode categoriesNode = root.get("categories");
            if (!categoriesNode.isArray() || categoriesNode.size() == 0) {
                throw new ConfigException("categories must be a non-empty array: " + path);
            }

            List<CategoryDefinition> categories = new ArrayList<>();
            List<String> categoryIds = new ArrayList<>();
            for (JsonNode categoryNode : categoriesNode) {
                CategoryDefinition category = parseCategory(categoryNode, path.toString());
                if (categoryIds.contains(category.id())) {
                    throw new ConfigException("Duplicate category id: " + category.id());
                }
                categoryIds.add(category.id());
                categories.add(category);
            }

            List<TemplateDefinition> templates = new ArrayList<>();
            List<String> templateIds = new ArrayList<>();
            for (JsonNode templateNode : templatesNode) {
                TemplateDefinition template = parseTemplate(templateNode, path.toString());
                if (templateIds.contains(template.templateId())) {
                    throw new ConfigException("Duplicate template_id: " + template.templateId());
                }
                templateIds.add(template.templateId());
                templates.add(template);
            }

            return new PackDefinition(packId, categories, templates);
        } catch (IOException ex) {
            throw new ConfigException("Failed to read pack: " + path, ex);
        }
    }

    private CategoryDefinition parseCategory(JsonNode node, String source) {
        require(node, "id");
        require(node, "name");
        require(node, "order");
        require(node, "icon");

        String id = node.get("id").asText();
        String name = node.get("name").asText();
        String icon = node.get("icon").asText();
        if (id.isBlank()) {
            throw new ConfigException("category id must be non-empty: " + source);
        }
        if (name.isBlank()) {
            throw new ConfigException("category name must be non-empty: " + source);
        }
        if (icon.isBlank()) {
            throw new ConfigException("category icon must be non-empty: " + source);
        }
        return new CategoryDefinition(id, name, node.get("order").asInt(), icon);
    }

    private TemplateDefinition parseTemplate(JsonNode node, String source) {
        require(node, "template_id");
        require(node, "id_template");
        require(node, "name_template");
        require(node, "title_template");
        require(node, "description_template");
        require(node, "category_id");
        require(node, "icon_template");
        require(node, "criteria_template");
        require(node, "rewards_template");
        require(node, "subjects");
        require(node, "tiers");

        String templateId = node.get("template_id").asText();
        if (templateId.isBlank()) {
            throw new ConfigException("template_id must be non-empty: " + source);
        }

        JsonNode subjectsNode = node.get("subjects");
        if (!subjectsNode.isArray() || subjectsNode.size() == 0) {
            throw new ConfigException("subjects must be a non-empty array: " + source);
        }
        List<String> subjects = new ArrayList<>();
        for (JsonNode subject : subjectsNode) {
            subjects.add(subject.asText());
        }

        JsonNode tiersNode = node.get("tiers");
        require(tiersNode, "levels");
        require(tiersNode, "count_start");
        require(tiersNode, "count_multiplier");
        require(tiersNode, "ap_start");
        require(tiersNode, "ap_multiplier");

        TierSpec tiers = new TierSpec(
                tiersNode.get("levels").asInt(),
                tiersNode.get("count_start").asInt(),
                tiersNode.get("count_multiplier").asDouble(),
                tiersNode.get("ap_start").asInt(),
                tiersNode.get("ap_multiplier").asDouble()
        );

        return new TemplateDefinition(
                templateId,
                node.get("id_template").asText(),
                node.get("name_template").asText(),
                node.get("title_template").asText(),
                node.get("description_template").asText(),
                node.get("category_id").asText(),
                node.get("icon_template").asText(),
                node.get("criteria_template"),
                node.get("rewards_template"),
                subjects,
                tiers
        );
    }

    private void require(JsonNode node, String field) {
        if (node == null || node.get(field) == null || node.get(field).isNull()) {
            throw new ConfigException("Missing required field: " + field);
        }
    }
}
