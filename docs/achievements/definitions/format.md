# Achievement format

An **achievement** is a named progression track with ordered tiers.

## Required fields

Each achievement definition MUST include:

- `id`: string
- `name`: string
- `category_id`: string
- `icon`: string
- `max_tier`: integer (>= 1)
- `tiers`: list of tier definitions covering 1..max_tier

## Tier definition

Each tier MUST include:

- `tier`: integer (starting at 1)
- `title`: string
- `description`: string
- `criteria`: criteria object (see [achievements/shared/criteria-dsl.md](../shared/criteria-dsl.md))
- `rewards`: object (see [rewards.md](rewards.md))

## Ordering

- Tiers MUST be completed in ascending order.
- Skipping tiers is invalid.
