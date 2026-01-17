# Storage

## Requirements

- Persistence MUST be durable across restarts.
- Writes MUST be safe under crashes (transactional or journaling).

## Recommended implementation

- SQLite with WAL mode.

## Data ownership

- All persisted data MUST be derived from spec-defined schemas.
