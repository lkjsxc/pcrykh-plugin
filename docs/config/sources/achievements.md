# Achievement sources

- node: docs/config/sources/achievements.md
  - purpose:
    - define achievement inputs used to build the runtime catalog
  - source_binding:
    - `achievement_sources` in [../runtime/runtime-config.md](../runtime/runtime-config.md)
  - resolution:
    - each entry is a path relative to the plugin data folder
    - directories are scanned recursively for `.json` files
    - files are loaded in lexical order
  - file_rules:
    - each file MUST remain under 300 lines
    - each file is either an achievement pack or an achievement series
    - packs contain arrays of achievement definitions
    - series contain arrays of series definitions
  - pack_format:
    - each element MUST conform to [domain/achievements/catalog/model.md](../../domain/achievements/catalog/model.md)
  - series_format:
    - each element MUST conform to [domain/achievements/series/format.md](../../domain/achievements/series/format.md)
