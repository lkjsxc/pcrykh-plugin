# Criteria DSL

The Criteria DSL is a machine-readable, JSON-shaped object describing a completion condition.

## Common fields

Every criteria object MUST include:

- `type`: string
- `count`: integer (>= 1) if the criteria is count-based
- `constraints`: object (optional)

Every objective evaluation MUST be performed server-side from events.

## Supported criteria types (v1)

### `block_break`

Triggered by server block-break events.

Required fields:

- `materials`: list of material identifiers
- `count`: integer

Common constraints:

- `tool`: object
  - `required`: boolean
  - `type`: one of `{axe, pickaxe, shovel, hoe}`

#### Woodcutting exception (normative)

- For tree materials (logs, wood blocks, stems, hyphae), tool constraints MUST NOT block progress.
- In other words, breaking tree blocks MUST count even if the player is holding a non-axe item or empty hand.

### `item_craft`

Triggered by crafting result events.

Required fields:

- `item`: item identifier
- `count`: integer

Progress semantics:

- Progress increments by **material-units**, not output item count.
- A **material-unit** is the number of non-air ingredient slots used in the recipe for one craft.
- For shift-crafts, the material-units MUST scale by the number of crafts executed.

### `entity_kill`

Triggered when an entity dies.

Required fields:

- `entities`: list of entity identifiers
- `count`: integer

Common constraints:

- `by_player_only`: boolean (default true)
- `weapon`: object (optional)
  - `type`: one of `{melee, bow, crossbow, trident}`

### `fish_catch`

Triggered by fishing catch events.

Required fields:

- `items`: list of fish item identifiers
- `count`: integer

Common constraints:

- `open_water_only`: boolean
- `in_rain_only`: boolean
- `biomes`: list of biome identifiers

### `travel`

Triggered by movement tracking.

Required fields:

- `distance_blocks`: integer

Optional constraints:

- `biomes`: list of biome identifiers
- `dimensions`: list of `{overworld, nether, end}`

## Normalization

- Implementations MUST normalize identifiers to canonical Bukkit/Paper names.
- Tag support is not permitted for achievement criteria.
