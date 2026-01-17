# Achievement packs

- node: docs/config/achievement-packs.md
  - purpose:
    - pack files define template-based achievement generation
  - pack_schema:
    - json:
      ```json
      {
        "pack_id": "string",
        "templates": ["AchievementTemplate"]
      }
      ```
  - rules:
    - `pack_id` MUST be a non-empty string
    - `templates` MUST be a non-empty array
  - template_schema:
    - see [domain/achievements/templates.md](../domain/achievements/templates.md)
  - expansion_rules:
    - each template generates `subjects.length × tiers.levels` achievements
    - tokens are applied per [domain/achievements/templates.md](../domain/achievements/templates.md)
    - the generated catalog SHOULD be ~500 achievements (target range 450–550)
