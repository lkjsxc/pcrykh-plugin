# Achievement runtime

- node: docs/runtime/achievements.md
  - unlock_events:
    - when an achievement transitions to completed, it becomes unlocked
    - unlocks are tracked in-memory for the current session
  - global_chat_notification:
    - if `runtime.chat.announce_achievements` is true, broadcast to global chat
    - message format: `<prefix><player_name> unlocked <title>`
    - `<prefix>` is `runtime.chat.prefix`
  - progress_tracking:
    - progress is computed per criteria definition
    - the runtime MUST expose `<current>` and `<target>` for GUI rendering
    - progress values are integers
  - ordering:
    - achievements are ordered by category order, then ascending `id`
    - category order is defined in [domain/achievements/catalog/categories.md](../domain/achievements/catalog/categories.md)
