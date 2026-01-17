# Schema: Player

Logical record keyed by player UUID.

Required fields:

- `player_uuid`: string (UUID)
- `spec_version`: string
- `achievement_tier_sum`: integer (>= 0)
- `player_level`: integer (>= 0)
- `ap_total`: integer (>= 0)
- `created_at`: timestamp
- `updated_at`: timestamp
