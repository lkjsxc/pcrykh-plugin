# Migrations

## When required

- A migration is required whenever `spec_version` changes in a way that affects schema or semantics.

## Invariants

- Migrations MUST be reversible when feasible.

## Current schema break (skills removal)

When migrating from any skill-based schema:

- Drop any skill progress tables and columns.
- Recompute `achievement_tier_sum` as the sum of all completed achievement tiers.
- Set `player_level = achievement_tier_sum`.
- Ensure `objective_history` stores only `ap_awarded`.
