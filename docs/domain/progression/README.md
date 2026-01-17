# Progression

This directory defines progression state, objective completion, and XP awarding.

## TOC

- [xp-currencies.md](xp-currencies.md)
- [objective-model.md](objective-model.md)
- [leveling-rules.md](leveling-rules.md)

## Model overview

Achievements are independent tracks with a single objective.

- Each achievement defines exactly one **objective**.
- Objectives accumulate a single integer progress counter (`progress_amount`).
- When the objective is satisfied, that achievement is completed and **rewards are granted**.

## State model (normative)

The runtime MUST represent per-player progression state for each achievement with the following fields:

- `completed` (bool): whether the achievement objective is complete.
- `progress_amount` (int/long): progress counter for the objective.

The runtime MUST also represent player-global progression:

- `player_level` (int): derived from total completed achievements (see below).
- `achievement_completed_sum` (int): count of completed achievements.
- `ap_total` (int/long): total Achievement Points accumulated.

## Completion semantics

Let `required(c)` be the required amount for a criteria `c`.

- For criteria types with `count`, `required(c) = c.count`.
- For `item_craft`, `required(c) = c.count`.
- For `travel`, `required(c) = c.distance_blocks`.

When an eligible game event occurs for a player and an achievement’s current objective, the implementation increments:

- `progress_amount += delta`.

If `progress_amount >= required(c)`, the implementation MUST complete the objective.

### Objective completion

On objective completion for `achievement`:

- The system MUST be idempotent: completion MUST only apply if not already completed.
- Rewards MUST be granted:
  - `ap_total += achievement.rewards.ap`
- Completion MUST be recorded:
  - `completed = true`
- `progress_amount` SHOULD be set to the required amount for display.
- `achievement_completed_sum` MUST be recomputed as the count of completed achievements.
- The completion MUST be persisted to objective history.

### Player level derivation

- `achievement_completed_sum` = count of completed achievements.
- `player_level` MUST be derived directly from `achievement_completed_sum`.

## Runtime feedback requirements

- When Achievement Points (AP) are awarded, the plugin MUST display an AP action bar message as specified in [interaction/gui/action-bar/README.md](../../interaction/gui/action-bar/README.md).

## Persistence requirements

- All progression state changes MUST be persisted.
- Persistence MUST be safe under autosave (see [runtime/autosave.md](../../runtime/autosave.md)).
