# Achievement format

An **achievement** is a named progression track with a single objective.

## Required fields

Each achievement definition MUST include:

- `id`: string
- `name`: string
- `category_id`: string
- `icon`: string
- `title`: string
- `description`: string
- `criteria`: criteria object (see [achievements/shared/criteria-dsl.md](../shared/criteria-dsl.md))
- `rewards`: object (see [rewards.md](rewards.md))
