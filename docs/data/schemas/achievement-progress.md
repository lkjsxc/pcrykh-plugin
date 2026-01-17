# Schema: AchievementProgress

One record per (player, achievement).

Required fields:

- `player_uuid`: UUID
- `achievement_id`: string
- `current_tier`: integer (>= 0)
- `next_tier`: integer (>= 1)
- `progress_amount`: integer (>= 0)
- `updated_at`: timestamp
