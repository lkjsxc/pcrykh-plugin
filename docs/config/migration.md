# Config migration

## Policy

- Old config formats are not supported.
- If a config does not match this spec, the implementation MUST refuse to start and MUST explain the mismatch.
- A spec version bump MAY require deleting the config and regenerating defaults.

## JSON parsing failures

- If the server fails to parse `config.json`, the config MUST be corrected or deleted so the default config can be regenerated.
