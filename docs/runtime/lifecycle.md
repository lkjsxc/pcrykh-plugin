# Lifecycle

## Startup sequence

1. Ensure default config resources exist in the data folder.
2. Load `config.json` and validate required fields.
3. Resolve `achievement_sources` to pack files.
4. Expand templates into concrete achievements.
5. Register `/pcrykh`.

## Shutdown

- No persistence actions are performed.

## Runtime invariants

- The achievement catalog is immutable after load.
