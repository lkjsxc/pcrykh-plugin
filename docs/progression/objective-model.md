# Objective model

## Definitions

- **Achievement**: A named progression track identified by `id`.
- **Tier**: An integer starting at 1.
- **Objective**: A completion condition for a given (achievement, tier).

## Objective contract

Each objective MUST define:

- `achievement_id` (string) — references the achievement `id`.
- `tier` (int)
- `title` (string)
- `description` (string)
- `criteria` (machine-readable)
- `rewards`:
  - `ap` amount

## Completion ordering

- Objectives MUST be completed in order within an achievement (no skipping).
- Completing an objective MUST be idempotent.

## Required amounts

Let `required(c)` be the required amount for criteria `c`.

- For criteria types with `count`, `required(c) = c.count`.
- For `travel` and `travel_*`, `required(c) = c.distance_blocks`.

## Progress semantics

- Objective progress is a single integer counter per (achievement, tier).
- Progress increments MUST be based solely on server-side events.
