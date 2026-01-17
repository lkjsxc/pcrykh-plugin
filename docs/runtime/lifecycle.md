# Lifecycle

- node: docs/runtime/lifecycle.md
	- startup_sequence:
		- ensure default config resources exist in the data folder
		- load `config.json` and validate required fields
		- resolve `category_sources` to category files
		- resolve `achievement_sources` to achievement files
		- load categories and achievements into the catalog
		- register GUI menu listeners
		- register hotbar beacon entry and click handler
		- register `/pcrykh`
	- shutdown:
		- no persistence actions are performed
	- runtime_invariants:
		- the achievement catalog is immutable after load
