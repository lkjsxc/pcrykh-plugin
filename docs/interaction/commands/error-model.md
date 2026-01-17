# Error model

## Principles

- Errors MUST be deterministic and consistent.
- Errors MUST NOT leak internal exceptions to players.

## Categories

- `INVALID_ARGUMENT`
- `MISSING_PERMISSION`
- `PLAYER_NOT_FOUND`
- `ACHIEVEMENT_NOT_FOUND`
- `STATE_VIOLATION` (e.g., completing level 7 before level 6)
- `INTERNAL_ERROR`

## Presentation

- For commands, errors MUST be displayed in chat in a single line.
- For GUI actions, errors MUST be shown as a short actionbar message.
