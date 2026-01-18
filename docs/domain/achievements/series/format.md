# Achievement series format

- node: docs/domain/achievements/series/format.md
  - series_file:
    - schema:
      ```json
      {
        "series": ["SeriesDefinition"]
      }
      ```
  - series_definition:
    - schema:
      ```json
      {
        "id_prefix": "string",
        "count": 1,
        "category_id": "string",
        "icon": "string",
        "title_template": "string",
        "description_template": "string",
        "name_template": "string",
        "amount_start": 1,
        "amount_step": 0,
        "reward_ap_start": 0,
        "reward_ap_step": 0,
        "criteria": { "...": "CriteriaDefinition" }
      }
      ```
    - rules:
      - required fields: `id_prefix`, `count`, `category_id`, `icon`, `title_template`, `description_template`, `amount_start`, `amount_step`, `reward_ap_start`, `reward_ap_step`, `criteria`
      - `name_template` is optional; when omitted it defaults to `title_template`
      - `count` MUST be an integer `>= 1`
      - `amount_start` MUST be an integer `>= 1`
      - `amount_step` MUST be an integer `>= 0`
      - `reward_ap_start` MUST be an integer `>= 0`
      - `reward_ap_step` MUST be an integer `>= 0`
      - `criteria` MUST conform to the canonical criteria types and include `constraints`
  - generation:
    - ids are generated as `<id_prefix>_<NNN>` with 1-based indexing and zero-padding to width 3
    - `amount = amount_start + amount_step * (index - 1)`
    - `reward.ap = reward_ap_start + reward_ap_step * (index - 1)`
    - template tokens:
      - `{i}` is the 1-based index
      - `{amount}` is the generated amount value
