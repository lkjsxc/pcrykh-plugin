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
    - `tips`
    - `achievement_sources` (non-empty list)
  - top_level_schema:
    - json:
      ```json
      {
        "spec_version": "4.1",
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
            "tips_enabled": true,
            "tips_interval_seconds": 180,
            "tips_prefix": "[Pcrykh] "
          }
        },
        "tips": ["string"],
        "achievement_sources": ["achievements/packs/core.json"]
      }
      ```
  - rules:
    - `spec_version` MUST start with `4.`
    - `achievement_sources` MUST be a non-empty array
  - achievement_source_resolution:
    - each entry in `achievement_sources` is a path relative to the plugin data folder
    - if the entry is a directory, all `.json` files under it (recursive) are loaded in lexical order
    - if the entry is a file, it is loaded directly
  - generated_achievements:
    - achievements are generated from packs; see [achievement-packs.md](achievement-packs.md)
    - generated achievements MUST conform to [domain/achievements/model.md](../domain/achievements/model.md)
