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
    - `category_sources` (non-empty list)
    - `achievement_sources` (non-empty list)
    - `facts_sources` (non-empty list)
  - top_level_schema:
    - json:
      ```json
      {
        "spec_version": "4.3",
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
        "category_sources": ["achievements/categories"],
        "achievement_sources": ["achievements/entries"]
      }
      ```
  - rules:
    - `spec_version` MUST start with `4.`
    - `category_sources` MUST be a non-empty array
    - `achievement_sources` MUST be a non-empty array
    - `facts_sources` MUST be a non-empty array
  - category_source_resolution:
    - each entry in `category_sources` is a path relative to the plugin data folder
    - if the entry is a directory, all `.json` files under it (recursive) are loaded in lexical order
    - if the entry is a file, it is loaded directly
  - achievement_source_resolution:
    - each entry in `achievement_sources` is a path relative to the plugin data folder
    - if the entry is a directory, all `.json` files under it (recursive) are loaded in lexical order
    - if the entry is a file, it is loaded directly
  - facts_source_resolution:
    - each entry in `facts_sources` is a path relative to the plugin data folder
    - if the entry is a directory, all `.json` files under it (recursive) are loaded in lexical order
    - if the entry is a file, it is loaded directly
  - generated_achievements:
    - achievements are loaded from single-achievement files
    - each file MUST conform to [domain/achievements/model.md](../domain/achievements/model.md)
