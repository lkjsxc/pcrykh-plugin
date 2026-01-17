# Achievement files

- node: docs/config/achievement-files.md
  - purpose:
    - define the per-achievement JSON files that populate the catalog
  - source_binding:
    - `achievement_sources` in [runtime-config.md](runtime-config.md)
  - resolution:
    - each entry is a path relative to the plugin data folder
    - directories are scanned recursively for `.json` files
    - files are loaded in lexical order
  - file_rules:
    - each file defines exactly one achievement object
    - each file MUST remain under 300 lines
    - filenames SHOULD match the achievement `id`
  - schema:
    - [domain/achievements/model.md](../domain/achievements/model.md)
