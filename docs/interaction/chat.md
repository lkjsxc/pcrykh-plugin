# Chat output

This document specifies global chat behaviors.

## Achievement announcements

- When an achievement completes, the plugin MUST broadcast a global chat message.
- The message MUST include:
  - Achievement display name
  - AP awarded in that completion
  - Total AP after the award

## Tips

- Tips are global chat messages selected from `tips` in config.
- Tips MUST be emitted at a fixed interval controlled by `runtime.chat.tips_interval_seconds`.
- Tips MUST be prefixed by `runtime.chat.tips_prefix`.
- Tips MAY be disabled via `runtime.chat.tips_enabled`.

## Settings

- `runtime.chat.announce_achievements` toggles achievement announcements.
- `runtime.chat.tips_enabled` toggles tips.
- `runtime.chat.tips_interval_seconds` controls the interval.
- `runtime.chat.tips_prefix` controls the prefix.
