# Achievement packs

Pack files define template-based achievement generation.

## Pack schema (normative)

```json
{
  "pack_id": "string",
  "templates": ["AchievementTemplate", "..."]
}
```

## AchievementTemplate schema (normative)

```json
{
  "template_id": "string",
  "id_template": "string",
  "name_template": "string",
  "title_template": "string",
  "description_template": "string",
  "category_id": "string",
  "icon_template": "string",
  "criteria_template": {"...": "..."},
  "rewards_template": {"...": "..."},
  "subjects": ["string", "..."],
  "tiers": {
    "levels": 10,
    # Achievement packs

    Pack files define template-based achievement generation.

    ## Pack schema (canonical)

    ```json
    {
      "pack_id": "string",
      "templates": ["AchievementTemplate"]
    }
    ```

    Rules:

    - `pack_id` MUST be a non-empty string.
    - `templates` MUST be a non-empty array.

    ## AchievementTemplate schema (canonical)

    See [domain/achievements/templates.md](../domain/achievements/templates.md).

    ## Expansion rules

    - Each template generates `subjects.length × tiers.levels` achievements.
    - Tokens are applied per [domain/achievements/templates.md](../domain/achievements/templates.md).
    - The generated catalog SHOULD be ~500 achievements (target range 450–550).
