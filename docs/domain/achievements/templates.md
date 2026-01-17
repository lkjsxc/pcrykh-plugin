# Achievement templates

Templates generate concrete achievements via subject × tier expansion.

## Template structure

Fields:

- `template_id` (string, unique in pack)
- `id_template` (string with tokens)
- `name_template` (string with tokens)
- `title_template` (string with tokens)
- `description_template` (string with tokens)
- `category_id` (string)
- `icon_template` (string with tokens)
- `criteria_template` (object, tokenized)
- `rewards_template` (object, tokenized)
- `subjects` (array of strings)
# Achievement templates

Templates generate concrete achievements via subject × tier expansion.

## Template schema (canonical)

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

Rules:

- `template_id` MUST be unique within a pack.
- All string fields are tokenized.
- `criteria_template` and `rewards_template` are tokenized at every string leaf.

## Tokens

- `{subject}`: subject value.
- `{subject_id}`: slugified subject.
- `{tier}`: roman numeral for `tier_index`.
- `{tier_index}`: 1-based tier index.
- `{count}`: computed tier count.
- `{ap}`: computed tier reward AP.

Slugify algorithm:

- Lowercase.
- Replace non-alphanumeric characters with `_`.
- Collapse repeated `_`.
- Trim leading and trailing `_`.

## TierSpec

Counts and AP are computed as:

$$count_t = round(count\_start \times count\_multiplier^{t-1})$$
$$ap_t = round(ap\_start \times ap\_multiplier^{t-1})$$

Tier loop:

- For `t` in `1..levels`:
  - `tier_index = t`.
  - `tier` is the roman numeral for `t`.

