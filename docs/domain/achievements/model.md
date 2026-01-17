# Achievement model

## AchievementDefinition (canonical)

Required fields:

- `id` (string, unique)
- `name` (string)
- `category_id` (string)
- `icon` (string)
- `title` (string)
- `description` (string)
- `criteria` (CriteriaDefinition)
- `rewards` (RewardDefinition)

Constraints:

- `id` MUST be globally unique.
- `category_id` is a label only; no runtime behavior is inferred.
