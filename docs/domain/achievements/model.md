# Achievement model

- node: docs/domain/achievements/model.md
	- achievement_definition:
		- schema:
			```json
			{
				"id": "string",
				"name": "string",
				"category_id": "string",
				"icon": "string",
				"title": "string",
				"description": "string",
				"criteria": { "...": "CriteriaDefinition" },
				"rewards": { "...": "RewardDefinition" }
			}
			```
		- rules:
			- all fields are required
			- `id` MUST be globally unique across the generated catalog
			- `category_id` is a label only; no runtime behavior is inferred
			- `criteria` MUST conform to the canonical criteria types and include `constraints`
			- `rewards` MUST conform to the canonical reward definition
