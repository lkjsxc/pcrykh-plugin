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
- id: "CR-20260118-006"
  date: "2026-01-18"
  summary: "Replaced achievement pack architecture with per-achievement files, added category sources, updated GUI/action-bar specs, and defined build export behavior."
  files:
    - "docs/config/README.md"
    - "docs/config/runtime-config.md"
    - "docs/config/achievement-files.md"
    - "docs/config/category-files.md"
    - "docs/domain/achievements/README.md"
    - "docs/domain/achievements/model.md"
    - "docs/domain/achievements/structure.md"
    - "docs/domain/achievements/categories.md"
    - "docs/domain/criteria/types.md"
    - "docs/gui/achievements.md"
    - "docs/gui/menu.md"
    - "docs/gui/profile.md"
    - "docs/gui/settings.md"
    - "docs/gui/navigation.md"
    - "docs/runtime/action-bar.md"
    - "docs/runtime/loader.md"
    - "docs/runtime/lifecycle.md"
    - "docs/runtime/README.md"
    - "docs/runtime/build.md"
    - "docs/governance/conflicts/index.md"
    - "docs/governance/conflicts/contradictions.md"
    - "docs/governance/conflicts/resolutions.md"
    - "docs/governance/changes/change-records.md"
  notes: "Pack/template docs removed; new catalog uses single-achievement JSON files; Docker export defined." 
  ```
