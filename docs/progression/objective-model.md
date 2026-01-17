# Objective model

## Definitions

- **Achievement**: A named progression track identified by `id`.
- **Objective**: A completion condition for a given achievement.

## Objective contract

Each objective MUST define:

- `achievement_id` (string) — references the achievement `id`.
- `title` (string)
- `description` (string)
- `criteria` (machine-readable)
- `rewards`:
  - `ap` amount

## Completion ordering

- Each achievement has a single objective; completion is binary.
- Completing an objective MUST be idempotent.

## Required amounts

Let `required(c)` be the required amount for criteria `c`.

- For criteria types with `count`, `required(c) = c.count`.
- For `travel` and `travel_*`, `required(c) = c.distance_blocks`.

## Progress semantics

- Objective progress is a single integer counter per achievement.
- Progress increments MUST be based solely on server-side events.
