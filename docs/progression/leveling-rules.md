# Leveling rules

## Achievement tier computation

- A player’s **Achievement Tier** for an achievement is the highest tier whose objective is completed.

## Player level computation

- Player level is computed as the sum of all completed achievement tiers.
- The computation MUST be deterministic and monotonic.

## Anti-abuse

- Objective criteria MUST be defined in terms of server-side events.
- Where necessary, objectives SHOULD include anti-cheese constraints (e.g., rate limits, biome variety, tool requirements).
