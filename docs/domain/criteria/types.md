# Criteria types

## Common fields

- `type` (string, required)
- `count` (integer, required, `>= 1`)
- `constraints` (object, required)

## `block_break`

```json
{
  "type": "block_break",
  "materials": ["string"],
  "count": 1,
  "constraints": {}
}
```

Rules:

- `materials` MUST be a non-empty array of strings.

## `item_craft`

```json
{
  "type": "item_craft",
  "item": "string",
  "count": 1,
  "constraints": {}
}
```

Rules:

- `item` MUST be a non-empty string.

## `entity_kill`

```json
{
  "type": "entity_kill",
  "entities": ["string"],
  "count": 1,
  "constraints": {}
}
```

Rules:

- `entities` MUST be a non-empty array of strings.

## `fish_catch`

```json
{
  "type": "fish_catch",
  "items": ["string"],
  "count": 1,
  "constraints": {}
}
```

Rules:

- `items` MUST be a non-empty array of strings.
