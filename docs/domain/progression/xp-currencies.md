# XP model

The system uses a single currency.

## Achievement Points (AP)

- Earned by completing achievements.
- Used to summarize player progression and reward totals.

## Player level derivation

- Player level is derived from completed achievements, not a separate currency.
- `achievement_completed_sum` = count of all completed achievements.
- `player_level` MUST equal `achievement_completed_sum`.

## Currency invariants

- Objective completion MUST grant AP.
- AP awards MUST be explicitly defined per achievement objective.
