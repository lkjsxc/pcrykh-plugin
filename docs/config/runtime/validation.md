# Runtime config validation

- node: docs/config/runtime/validation.md
  - order:
    - file existence and line-count limit
    - JSON parse
    - required keys and `commands.root`
    - `spec_version` validation
    - source resolution for categories, achievements, and facts
  - failure_behavior:
    - any validation failure disables plugin startup
