# Spec language

This spec uses RFC-style requirement keywords:

- **MUST / MUST NOT**: strict requirement.
- **SHOULD / SHOULD NOT**: strong preference; deviation requires a reason.
- **MAY**: optional.

## Determinism

- Player progression results MUST be deterministic given event order.
- If randomness is used (e.g., loot rolls), the RNG seeding rules MUST be specified.

## Naming

- All public-facing names MUST be stable within a given “spec version”.
- Internal identifiers MUST be ASCII and snake_case.
