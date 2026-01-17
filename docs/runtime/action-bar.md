# Action bar progress

- node: docs/runtime/action-bar.md
  - purpose:
    - notify players whenever progress increases for an achievement
  - config_binding:
    - `runtime.action_bar.progress_enabled`
  - message_format:
    - `<title> <meter> <current>/<target>`
  - meter:
    - render a fixed-width bar with filled and empty segments
    - filled segments scale with `current/target`
