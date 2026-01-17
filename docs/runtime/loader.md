# Config loader

- node: docs/runtime/loader.md
	- responsibilities:
		- enforce the 300-line limit on `config.json`
		- reject unsupported `spec_version`
		- validate required top-level fields
		- resolve `achievement_sources` to pack files
		- expand templates into concrete achievements
		- load but do not interpret `runtime.chat`, `runtime.action_bar`, and `facts`
	- validation_order:
		- file existence and line-count limit
		- JSON parse
		- required keys and `commands.root`
		- `spec_version` validation
		- `achievement_sources` resolution
		- template expansion and achievement validation
	- failure_behavior:
		- any validation failure disables the plugin
		- duplicate achievement `id` values are fatal
