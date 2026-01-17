# Action bar progress

- node: docs/runtime/action-bar.md
  - purpose:
    - notify players as they approach achievement milestones
  - config_binding:
    - `runtime.action_bar.progress_enabled`
    - `runtime.action_bar.milestone_thresholds`
    - `runtime.action_bar.cooldown_seconds`
  - thresholds:
    - thresholds are decimal ratios in ascending order (example: `0.80`)
    - a threshold is triggered when `current/target` crosses it
  - message_format:
    - `<prefix><title> <percent>%`
    - `<prefix>` is `runtime.chat.prefix`
  - throttling:
    - progress notifications per achievement are rate-limited by `cooldown_seconds`
