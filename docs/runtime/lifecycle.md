# Lifecycle

- node: docs/runtime/lifecycle.md
	- startup_sequence:
		- ensure default config resources exist in the data folder
		- load `config.json` and validate required fields
		- resolve `achievement_sources` to pack files
		- expand templates into concrete achievements
		- register GUI menu listeners
		- register hotbar beacon entry and click handler
		- register `/pcrykh`
	- shutdown:
		- no persistence actions are performed
	- runtime_invariants:
		- the achievement catalog is immutable after load
