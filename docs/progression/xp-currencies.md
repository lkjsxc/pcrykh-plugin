# XP model

The system uses a single currency.

## Achievement Points (AP)

- Earned by completing achievement tiers.
- Used to summarize player progression and reward totals.

## Player level derivation

- Player level is derived from achievement tiers, not a separate currency.
- `achievement_tier_sum` = sum of all completed tiers across achievements.
- `player_level` MUST equal `achievement_tier_sum`.

## Currency invariants

- Objective completion MUST grant AP.
- AP awards MUST be explicitly defined per tier objective.
