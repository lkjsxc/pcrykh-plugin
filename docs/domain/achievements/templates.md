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
- `tiers` (TierSpec)

## Tokens

- `{subject}`: subject value
- `{subject_id}`: slugified subject
- `{tier}`: roman numeral
- `{tier_index}`: 1-based tier index
- `{count}`: tier count
- `{ap}`: tier reward AP

Tokens apply to all string values inside `criteria_template` and `rewards_template`.

## TierSpec

```json
{
  "levels": 10,
  "count_start": 1,
  "count_multiplier": 2.0,
  "ap_start": 5,
  "ap_multiplier": 1.6
}
```

Counts and AP are computed as:

$$count_t = round(count\_start \times count\_multiplier^{t-1})$$
$$ap_t = round(ap\_start \times ap\_multiplier^{t-1})$$

