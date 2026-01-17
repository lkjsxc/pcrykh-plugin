# Config loader

## Responsibilities

- Enforce the 300-line limit on `config.json`.
- Reject unsupported `spec_version`.
- Validate required top-level fields.
- Resolve `achievement_sources` to pack files.
- Expand templates into concrete achievements.
- Load but do not interpret `runtime.chat` and `tips`.

## Validation order (canonical)

1. File existence and line-count limit.
2. JSON parse.
3. Required keys and `commands.root`.
4. `spec_version` validation.
5. `achievement_sources` resolution.
6. Template expansion and achievement validation.

## Failure behavior

- Any validation failure disables the plugin.
- Duplicate achievement `id` values are fatal.
