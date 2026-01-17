# Objective model

## Definitions

- **Achievement**: A named progression track (e.g., `gather_oak_logs`).
- **Tier**: An integer starting at 1.
- **Objective**: A completion condition for a given (achievement, tier).

## Objective contract

Each objective MUST define:

- `achievement_id`
- `tier`
- `title`
- `description`
- `criteria` (machine-readable)
- `rewards`:
  - `ap` amount

## Completion

- Objectives MUST be completed in order within an achievement (no skipping).
- Completing an objective MUST be idempotent.
