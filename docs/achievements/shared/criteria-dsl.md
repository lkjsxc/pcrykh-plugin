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

### `travel_walk`

Triggered by movement tracking while **walking** (not sprinting, not swimming, not mounted).

Required fields:

- `distance_blocks`: integer

Optional constraints:

- `biomes`: list of biome identifiers
- `dimensions`: list of `{overworld, nether, end}`

### `travel_sprint`

Triggered by movement tracking while **sprinting** (not swimming, not mounted).

Required fields:

- `distance_blocks`: integer

Optional constraints:

- `biomes`: list of biome identifiers
- `dimensions`: list of `{overworld, nether, end}`

### `travel_swim`

Triggered by movement tracking while **swimming** (not mounted).

Required fields:

- `distance_blocks`: integer

Optional constraints:

- `biomes`: list of biome identifiers
- `dimensions`: list of `{overworld, nether, end}`

### `travel_crouch`

Triggered by movement tracking while **crouch-walking** (sneaking, not swimming, not flying, not mounted).

Required fields:

- `distance_blocks`: integer

Optional constraints:

- `biomes`: list of biome identifiers
- `dimensions`: list of `{overworld, nether, end}`

### `travel_fly`

Triggered by movement tracking while **flying or gliding** (not mounted).

Required fields:

- `distance_blocks`: integer

Optional constraints:

- `biomes`: list of biome identifiers
- `dimensions`: list of `{overworld, nether, end}`

### `travel_mount`

Triggered by movement tracking while **mounted** on a non-boat vehicle.

Required fields:

- `distance_blocks`: integer
- `vehicles`: list of entity identifiers (e.g., `HORSE`, `PIG`, `STRIDER`, `CAMEL`)

Optional constraints:

- `biomes`: list of biome identifiers
- `dimensions`: list of `{overworld, nether, end}`

### `travel_boat`

Triggered by movement tracking while **rowing a boat**.

Required fields:

- `distance_blocks`: integer
- `vehicles`: list of boat identifiers (e.g., `OAK_BOAT`, `OAK_CHEST_BOAT`, `BAMBOO_RAFT`)

Optional constraints:

- `biomes`: list of biome identifiers
- `dimensions`: list of `{overworld, nether, end}`

### `travel_boat_with_animal`

Triggered by movement tracking while **rowing a boat** that carries an animal passenger.

Required fields:

- `distance_blocks`: integer
- `vehicles`: list of boat identifiers (e.g., `OAK_BOAT`, `OAK_CHEST_BOAT`, `BAMBOO_RAFT`)
- `passengers`: list of entity identifiers (e.g., `COW`, `SHEEP`, `PIG`, `CHICKEN`)

Optional constraints:

- `biomes`: list of biome identifiers
- `dimensions`: list of `{overworld, nether, end}`

### `jump`

Triggered by server-side jump increments.

Required fields:

- `count`: integer

Optional constraints:

- `rate_limit`

## Normalization

- Implementations MUST normalize identifiers to canonical Bukkit/Paper names.
- Tag support is not permitted for achievement criteria.
