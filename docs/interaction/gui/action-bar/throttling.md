# Action Bar Throttling

Action bar output can be spammy if emitted on high-frequency events.

## Requirements

- AP award messages MUST always be emitted.
- Progress-update action bar messages MUST be throttled.

## Suggested policy (not required)

- Per-player, per-achievement: at most 1 progress action bar update per 250ms.
- AP award message bypasses throttling.
