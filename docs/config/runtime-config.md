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
		}
	},
	"achievements": ["AchievementDefinition", "..."]
}
```

## Achievement definition shape

Each achievement MUST define:

- `id`
- `name`
- `category_id`
- `icon`
- `max_tier` (MUST be >= 1)
- `tiers` (objectives table; MUST include every integer tier from 1..max_tier)

### AchievementDefinition schema (normative)

```json
{
	"id": "string",
	"name": "string",
	"category_id": "string",
	"icon": "string",
	"max_tier": 5,
	"tiers": ["TierDefinition", "..."]
}
```

## Tier definition shape

Each tier MUST define:

- `tier` (int, starting at 1)
- `title` (string)
- `description` (string)
- `criteria` (object)
- `rewards` (object)

### TierDefinition schema (normative)

```json
{
	"tier": 1,
	"title": "string",
	"description": "string",
	"criteria": "CriteriaDefinition",
	"rewards": {
		"ap": 0
	}
}
```

## CriteriaDefinition

Criteria objects MUST include `type` and additional fields depending on type.

### Common fields

```json
{
	"type": "string",
	"constraints": {"...": "..."}
}
```

### Supported criteria types (required shapes)

#### `block_break`

```json
{
	"type": "block_break",
	"materials": ["string"],
	"count": 1,
	"constraints": {"...": "..."}
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

## Conflict handling

If a doc page conflicts with this schema, the schema in this file wins unless explicitly superseded by a higher-precedence principle in [principles/README.md](../principles/README.md). All conflicts MUST be recorded in [conflicts/README.md](../conflicts/README.md).
