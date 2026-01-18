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
  - id: "CR-20260118-009"
    date: "2026-01-18"
    summary: "Removed achievement series; standardized on individual entry files and updated source resolution docs."
    files:
      - "docs/config/runtime/runtime-config.md"
      - "docs/config/sources/achievements.md"
      - "docs/runtime/loader.md"
      - "docs/domain/achievements/README.md"
      - "docs/domain/achievements/catalog/structure.md"
      - "docs/domain/achievements/entries/README.md"
      - "docs/domain/achievements/entries/format.md"
      - "docs/domain/achievements/entries/organization.md"
      - "docs/domain/achievements/series/README.md"
      - "docs/domain/achievements/series/format.md"
      - "docs/domain/achievements/series/generation.md"
      - "docs/domain/achievements/templates.md"
      - "docs/config/achievement-files.md"
      - "docs/governance/conflicts/contradictions.md"
      - "docs/governance/changes/change-records.md"
    notes: "Achievement sources are now single entries or packs; series generation is removed."
  ```
