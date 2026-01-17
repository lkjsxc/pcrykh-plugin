# Runtime config

- node: docs/config/runtime-config.md
  - file:
    - name: `config.json`
    - max_lines: 300
    - authority: runtime input
  - required_top_level_keys:
    - `spec_version`
    - `commands.root` (MUST be `pcrykh`)
    - `runtime.autosave`
    - `runtime.chat`
    - `runtime.action_bar`
    - `achievement_sources` (non-empty list)
    - `facts_sources` (non-empty list)
  - top_level_schema:
    - json:
      ```json
      {
        "spec_version": "4.2",
        "commands": {
          "root": "pcrykh"
        },
        "runtime": {
          "autosave": {
            "enabled": true,
            "interval_seconds": 60
          },
          "chat": {
            "announce_achievements": true,
            "facts_enabled": true,
            "facts_interval_seconds": 180,
            "prefix": "[Pcrykh] "
          },
          "action_bar": {
            "progress_enabled": true
          }
        },
        "facts_sources": ["facts/packs"],
        "achievement_sources": [
          "achievements/packs/mining.json",
          "achievements/packs/harvest.json",
          "achievements/packs/crafting.json",
          "achievements/packs/hunting.json",
          "achievements/packs/fishing.json"
        ]
      }
      ```
  - rules:
    - `spec_version` MUST start with `4.`
    - `achievement_sources` MUST be a non-empty array
    - `facts_sources` MUST be a non-empty array
  - achievement_source_resolution:
    - each entry in `achievement_sources` is a path relative to the plugin data folder
    - if the entry is a directory, all `.json` files under it (recursive) are loaded in lexical order
    - if the entry is a file, it is loaded directly
  - facts_source_resolution:
    - each entry in `facts_sources` is a path relative to the plugin data folder
    - if the entry is a directory, all `.json` files under it (recursive) are loaded in lexical order
    - if the entry is a file, it is loaded directly
  - generated_achievements:
    - achievements are generated from packs; see [achievement-packs.md](achievement-packs.md)
    - generated achievements MUST conform to [domain/achievements/model.md](../domain/achievements/model.md)
