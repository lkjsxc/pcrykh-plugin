# Runtime config

## File

- The plugin MUST load a JSON file named `config.json`.
- `config.json` MUST NOT exceed 300 lines.
- JSON config is the authoritative runtime input.

## Required top-level keys

- `spec_version`
- `commands.root` (MUST be `pcrykh`)
- `runtime.autosave`
- `runtime.chat`
- `tips`
- `achievement_sources` (non-empty list)

## Top-level schema (normative)

```json
{
  "spec_version": "4.0",
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
      # Runtime config

      ## File

      - The plugin MUST load a JSON file named `config.json`.
      - `config.json` MUST NOT exceed 300 lines.
      - JSON config is the authoritative runtime input.

      ## Required top-level keys

      - `spec_version`
      - `commands.root` (MUST be `pcrykh`)
      - `runtime.autosave`
      - `runtime.chat`
      - `tips`
      - `achievement_sources` (non-empty list)

      ## Top-level schema (canonical)

      ```json
      {
        "spec_version": "4.0",
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

      Rules:

      - `spec_version` MUST start with `4.`.
      - `achievement_sources` MUST be a non-empty array.

      ## Achievement source resolution

      - Each entry in `achievement_sources` is a path relative to the plugin data folder.
      - If the entry is a directory, all `.json` files under it (recursive) are loaded in lexical order.
      - If the entry is a file, it is loaded directly.

      ## Generated achievements

      - Achievements are generated from packs; see [achievement-packs.md](achievement-packs.md).
      - Generated achievements MUST conform to [domain/achievements/model.md](../domain/achievements/model.md).
