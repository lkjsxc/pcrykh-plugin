# Config loader

## Responsibilities

- Enforce the 300-line limit on `config.json`.
- Reject unsupported `spec_version`.
- Validate required top-level fields.
- Resolve `achievement_sources` to pack files.
- Expand templates into concrete achievements.
- Load but do not interpret `runtime.chat` and `tips`.

## Failure behavior

- Any validation failure disables the plugin.
- Duplicate achievement `id` values are fatal.
