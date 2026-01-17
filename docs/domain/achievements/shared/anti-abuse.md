# Anti-abuse rules (global)

The goal is to prevent trivial farms from auto-completing achievements without meaningful gameplay.

## Global anti-abuse requirements

- Objective progress MUST be tracked per player.
- Progress MUST NOT increase from events caused exclusively by non-player automation (implementation-defined, but MUST be consistent).
- The system MUST NOT require long-term memory of block placement to function correctly.

## Rate limiting (primary control)

- Implementations SHOULD apply `constraints.rate_limit.max_progress_per_minute` when provided.
- If an achievement does not specify a rate limit, the implementation MAY apply a conservative default cap.

## Event integrity

- For `block_break`, the breaker MUST be the player.
- For `entity_kill`, the credited killer MUST be the player.
- For `fish_catch`, the hook owner MUST be the player.
