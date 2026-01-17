# Config loader

## Responsibilities

- Enforce the 300-line limit on `config.json`.
- Reject unsupported `spec_version`.
- Validate required top-level fields.
- Resolve `achievement_sources` to pack files.
- Expand templates into concrete achievements.

## Failure behavior

- Any validation failure disables the plugin.
- Duplicate achievement `id` values are fatal.
