# Progression

This directory defines progression state, objective completion, and XP awarding.

## TOC

- [xp-currencies.md](xp-currencies.md)
- [objective-model.md](objective-model.md)
- [leveling-rules.md](leveling-rules.md)

## Model overview

Achievements are independent tracks with ordered tiers.

- Each tier defines exactly one **objective**.
- Objectives accumulate a single integer progress counter (`progress_amount`).
- When the objective is satisfied, that tier is completed and **rewards are granted**.

## State model (normative)

The runtime MUST represent per-player progression state for each achievement with the following fields:

- `current_tier` (int): last completed tier for this achievement.
- `next_tier` (int): the next tier to attempt; MUST equal `current_tier + 1` unless at max.
- `progress_amount` (int/long): progress counter for the objective of `next_tier`.

The runtime MUST also represent player-global progression:

- `player_level` (int): derived from total completed achievement tiers (see below).
- `achievement_tier_sum` (int): sum of all completed tiers across achievements.
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

On objective completion for `(achievement, tier)`:

- The system MUST be idempotent: completion MUST only apply if `next_tier == tier`.
- Rewards MUST be granted:
  - `ap_total += tier.rewards.ap`
- Tiers MUST advance:
  - `current_tier = tier`
  - `next_tier = tier + 1` (capped so that `next_tier > max_tier` indicates MAX)
- `progress_amount` MUST reset to 0.
- `achievement_tier_sum` MUST be recomputed as the sum of all completed tiers.
- The completion MUST be persisted to objective history.

### Player level derivation

- `achievement_tier_sum` = sum of all completed tiers across achievements.
- `player_level` MUST be derived directly from `achievement_tier_sum`.

## Runtime feedback requirements

- When Achievement Points (AP) are awarded, the plugin MUST display an AP action bar message as specified in [gui/action-bar/README.md](../gui/action-bar/README.md).

## Persistence requirements

- All progression state changes MUST be persisted.
- Persistence MUST be safe under autosave (see [runtime/autosave.md](../runtime/autosave.md)).
