# Change records

- node: docs/governance/changes/change-records.md
  - purpose:
    - record current-spec changes for LLM ingestion
    - records describe what is now true, not historical compatibility
  - record_format:
    - yaml:
      ```yaml
      - id: "CR-YYYYMMDD-NNN"
        date: "YYYY-MM-DD"
        summary: "string"
        files:
          - "path/to/file.md"
        notes: "optional"
      ```
  - rules:
    - records MUST be pruned when they no longer describe current-spec behavior
    - records MUST include all modified spec files for the change
    - backward compatibility notes MUST be omitted
  - records:
    - yaml:
      ```yaml
- id: "CR-20260117-001"
  date: "2026-01-17"
  summary: "Restructured spec docs into canonical tree indices and removed deprecated content."
  files:
    - "docs/README.md"
    - "docs/foundation/README.md"
    - "docs/foundation/scope.md"
    - "docs/foundation/versioning.md"
    - "docs/domain/README.md"
    - "docs/domain/achievements/README.md"
    - "docs/domain/achievements/model.md"
    - "docs/domain/achievements/templates.md"
    - "docs/domain/achievements/rewards.md"
    - "docs/domain/criteria/README.md"
    - "docs/domain/criteria/types.md"
    - "docs/domain/criteria/constraints.md"
    - "docs/config/README.md"
    - "docs/config/runtime-config.md"
    - "docs/config/achievement-packs.md"
    - "docs/runtime/README.md"
    - "docs/runtime/commands.md"
    - "docs/runtime/lifecycle.md"
    - "docs/runtime/loader.md"
    - "docs/governance/README.md"
    - "docs/governance/changes/README.md"
    - "docs/governance/changes/change-control.md"
    - "docs/governance/conflicts/README.md"
    - "docs/governance/conflicts/index.md"
    - "docs/governance/conflicts/contradictions.md"
    - "docs/governance/conflicts/resolutions.md"
  notes: "Deprecated permissions and deprecations content removed."
- id: "CR-20260118-001"
  date: "2026-01-18"
  summary: "Added GUI menu specification and updated runtime command behavior."
  files:
    - "docs/README.md"
    - "docs/foundation/scope.md"
    - "docs/config/runtime-config.md"
    - "docs/runtime/commands.md"
    - "docs/runtime/lifecycle.md"
    - "docs/gui/README.md"
    - "docs/gui/menu.md"
    - "docs/gui/navigation.md"
    - "docs/governance/conflicts/index.md"
    - "docs/governance/conflicts/contradictions.md"
    - "docs/governance/conflicts/resolutions.md"
    - "docs/governance/changes/change-records.md"
  notes: "GUI menus are now in-scope; /pcrykh opens the GUI."
- id: "CR-20260118-002"
  date: "2026-01-18"
  summary: "Reorganized docs into a directory-tree structure, fixed config schemas, and specified the hotbar beacon entry and menu atmosphere."
  files:
    - "docs/README.md"
    - "docs/foundation/README.md"
    - "docs/foundation/scope.md"
    - "docs/foundation/versioning.md"
    - "docs/domain/README.md"
    - "docs/domain/achievements/README.md"
    - "docs/domain/achievements/model.md"
    - "docs/domain/achievements/templates.md"
    - "docs/domain/achievements/rewards.md"
    - "docs/domain/criteria/README.md"
    - "docs/domain/criteria/types.md"
    - "docs/domain/criteria/constraints.md"
    - "docs/config/README.md"
    - "docs/config/runtime-config.md"
    - "docs/config/achievement-packs.md"
    - "docs/gui/README.md"
    - "docs/gui/menu.md"
    - "docs/gui/navigation.md"
    - "docs/gui/hotbar.md"
    - "docs/runtime/README.md"
    - "docs/runtime/commands.md"
    - "docs/runtime/lifecycle.md"
    - "docs/runtime/loader.md"
    - "docs/governance/README.md"
    - "docs/governance/changes/README.md"
    - "docs/governance/changes/change-control.md"
    - "docs/governance/changes/deprecations.md"
    - "docs/governance/changes/change-records.md"
    - "docs/governance/conflicts/README.md"
    - "docs/governance/conflicts/index.md"
    - "docs/governance/conflicts/contradictions.md"
    - "docs/governance/conflicts/resolutions.md"
  notes: "Cleaned malformed spec blocks and added the hotbar beacon menu entry."
  ```
