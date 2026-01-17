# Leveling rules

## Achievement completion computation

- A player’s **Achievement Completion** for an achievement is a boolean: completed or not.

## Player level computation

- Player level is computed as the count of completed achievements.
- The computation MUST be deterministic and monotonic.

## Anti-abuse

- Objective criteria MUST be defined in terms of server-side events.
- Where necessary, objectives SHOULD include anti-cheese constraints (e.g., rate limits, biome variety, tool requirements).
