# Achievement packs

- node: docs/config/achievement-packs.md
  - purpose:
    - pack files define template-based achievement generation
  - pack_schema:
    - json:
      ```json
      {
        "pack_id": "string",
        "categories": ["CategoryDefinition"],
        "templates": ["AchievementTemplate"]
      }
      ```
  - rules:
    - `pack_id` MUST be a non-empty string
    - `categories` MUST be a non-empty array
    - `templates` MUST be a non-empty array
    - category ids MUST be unique within the pack
    - every generated achievement `category_id` MUST exist in the merged category catalog
  - template_schema:
    - see [domain/achievements/templates.md](../domain/achievements/templates.md)
  - category_schema:
    - see [domain/achievements/categories.md](../domain/achievements/categories.md)
  - expansion_rules:
    - each template generates `subjects.length × tiers.levels` achievements
    - tokens are applied per [domain/achievements/templates.md](../domain/achievements/templates.md)
    - the generated catalog SHOULD be ~500 achievements (target range 450–550)
