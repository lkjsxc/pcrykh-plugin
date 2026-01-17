# Achievement templates

- node: docs/domain/achievements/templates.md
  - purpose:
    - templates generate concrete achievements via subject × tier expansion
  - template_schema:
    - json:
      ```json
      {
        "template_id": "string",
        "id_template": "string",
        "name_template": "string",
        "title_template": "string",
        "description_template": "string",
        "category_id": "string",
        "icon_template": "string",
        "criteria_template": {"...": "tokenized"},
        "rewards_template": {"...": "tokenized"},
        "subjects": ["string"],
        "tiers": {
          "levels": 10,
          "count_start": 1,
          "count_multiplier": 2.0,
          "ap_start": 5,
          "ap_multiplier": 1.6
        }
      }
      ```
  - rules:
    - `template_id` MUST be unique within a pack
    - all string fields are tokenized
    - `criteria_template` and `rewards_template` are tokenized at every string leaf
  - tokens:
    - `{subject}`: subject value
    - `{subject_id}`: slugified subject
    - `{tier}`: roman numeral for `tier_index`
    - `{tier_index}`: 1-based tier index
    - `{count}`: computed tier count
    - `{ap}`: computed tier reward AP
  - slugify_algorithm:
    - lowercase
    - replace non-alphanumeric characters with `_`
    - collapse repeated `_`
    - trim leading and trailing `_`
  - tier_spec:
    - formulas:
      - $$count_t = round(count\_start \times count\_multiplier^{t-1})$$
      - $$ap_t = round(ap\_start \times ap\_multiplier^{t-1})$$
    - tier_loop:
      - for `t` in `1..levels`:
        - `tier_index = t`
        - `tier` is the roman numeral for `t`
## Tokens


