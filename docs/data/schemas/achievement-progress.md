# Schema: AchievementProgress

One record per (player, achievement).

Required fields:

- `player_uuid`: UUID
- `achievement_id`: string
- `completed`: boolean
- `progress_amount`: integer (>= 0)
- `updated_at`: timestamp
