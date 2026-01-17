# Runtime config

## File

- The plugin MUST load a JSON config file named `config.json`.

## Interpretation

- The runtime config is the only input the server needs to run the progression system.
- Achievement definitions in config MUST be treated as **authoritative runtime data**.
- This document specifies the schema that the implementation MUST validate (see the loader).

## Required top-level keys

- `spec_version`
- `commands.root` (MUST be `pcrykh`)
- `gui.theme`
- `runtime.autosave`
- `runtime.chat`
- `tips`
- `achievements` (list of achievement definitions)

## Top-level schema (normative)

```json
{
	"spec_version": "string",
	"commands": {
		"root": "pcrykh"
	},
	"gui": {
		"theme": "string"
	},
	"runtime": {
		"autosave": {
			"enabled": true,
			"interval_seconds": 30
		},
		"chat": {
			"announce_achievements": true,
			"tips_enabled": true,
			"tips_interval_seconds": 120,
			"tips_prefix": "[Pcrykh Tip] "
		}
	},
	"tips": ["string", "..."],
	"achievements": ["AchievementDefinition", "..."]
}
```

## Achievement definition shape

Each achievement MUST define:

- `id`
- `name`
- `category_id`
- `icon`
- `title`
- `description`
- `criteria`
- `rewards`

### AchievementDefinition schema (normative)

```json
{
	"id": "string",
	"name": "string",
	"category_id": "string",
	"icon": "string",
	"title": "string",
	"description": "string",
	"criteria": "CriteriaDefinition",
	"rewards": {
		"ap": 0
	}
}
```

#### `item_craft`

```json
{
	"type": "item_craft",
	"item": "string",
	"count": 1,
	"constraints": {"...": "..."}
}
```

**JSON note:** tokens like `POTION{HEALING}` MUST be represented as JSON strings.

**Progress note:** `count` is the fixed material-unit requirement; no level-based scaling applies.

#### `entity_kill`

```json
{
	"type": "entity_kill",
	"entities": ["string"],
	"count": 1,
	"constraints": {"...": "..."}
}
```

#### `fish_catch`

```json
{
	"type": "fish_catch",
	"items": ["string"],
	"count": 1,
	"constraints": {"...": "..."}
}
```

#### `travel`

```json
{
	"type": "travel",
	"distance_blocks": 1,
	"constraints": {"...": "..."}
}
```

#### `travel_walk`

```json
{
	"type": "travel_walk",
	"distance_blocks": 1,
	"constraints": {"...": "..."}
}
```

#### `travel_sprint`

```json
{
	"type": "travel_sprint",
	"distance_blocks": 1,
	"constraints": {"...": "..."}
}
```

#### `travel_swim`

```json
{
	"type": "travel_swim",
	"distance_blocks": 1,
	"constraints": {"...": "..."}
}
```

#### `travel_crouch`

```json
{
	"type": "travel_crouch",
	"distance_blocks": 1,
	"constraints": {"...": "..."}
}
```

#### `travel_fly`

```json
{
	"type": "travel_fly",
	"distance_blocks": 1,
	"constraints": {"...": "..."}
}
```

#### `travel_mount`

```json
{
	"type": "travel_mount",
	"distance_blocks": 1,
	"vehicles": ["HORSE", "STRIDER"],
	"constraints": {"...": "..."}
}
```

#### `travel_boat`

```json
{
	"type": "travel_boat",
	"distance_blocks": 1,
	"vehicles": ["OAK_BOAT", "OAK_CHEST_BOAT"],
	"constraints": {"...": "..."}
}
```

#### `travel_boat_with_animal`

```json
{
	"type": "travel_boat_with_animal",
	"distance_blocks": 1,
	"vehicles": ["OAK_BOAT", "OAK_CHEST_BOAT"],
	"passengers": ["COW", "SHEEP"],
	"constraints": {"...": "..."}
}
```

#### `jump`

```json
{
	"type": "jump",
	"count": 1,
	"constraints": {"...": "..."}
}
```

## Conflict handling

If a doc page conflicts with this schema, the schema in this file wins unless explicitly superseded by a higher-precedence foundation note in [foundation/README.md](../foundation/README.md). Log conflicts in [governance/conflicts/index.md](../governance/conflicts/index.md).
