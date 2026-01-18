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
  - id: "CR-20260118-008"
    date: "2026-01-18"
    summary: "Replaced per-achievement JSON inputs with series/pack sources, reorganized docs tree, and specified 500 achievement types."
    files:
      - "docs/README.md"
      - "docs/config/README.md"
      - "docs/config/runtime/README.md"
      - "docs/config/runtime/runtime-config.md"
      - "docs/config/sources/README.md"
      - "docs/config/sources/achievements.md"
      - "docs/config/sources/categories.md"
      - "docs/config/sources/facts.md"
      - "docs/config/runtime-config.md"
      - "docs/config/achievement-files.md"
      - "docs/config/category-files.md"
      - "docs/domain/achievements/README.md"
      - "docs/domain/achievements/catalog/README.md"
      - "docs/domain/achievements/catalog/model.md"
      - "docs/domain/achievements/catalog/structure.md"
      - "docs/domain/achievements/catalog/categories.md"
      - "docs/domain/achievements/catalog/rewards.md"
      - "docs/domain/achievements/series/README.md"
      - "docs/domain/achievements/series/format.md"
      - "docs/domain/achievements/series/generation.md"
      - "docs/domain/achievements/ideas/README.md"
      - "docs/domain/achievements/ideas/ideas-001-100.md"
      - "docs/domain/achievements/ideas/ideas-101-200.md"
      - "docs/domain/achievements/ideas/ideas-201-300.md"
      - "docs/domain/achievements/ideas/ideas-301-400.md"
      - "docs/domain/achievements/ideas/ideas-401-500.md"
      - "docs/domain/achievements/model.md"
      - "docs/domain/achievements/structure.md"
      - "docs/domain/achievements/categories.md"
      - "docs/domain/achievements/rewards.md"
      - "docs/domain/achievements/templates.md"
      - "docs/domain/achievements/ideas-001-100.md"
      - "docs/domain/achievements/ideas-101-200.md"
      - "docs/domain/achievements/ideas-201-300.md"
      - "docs/domain/achievements/ideas-301-400.md"
      - "docs/domain/achievements/ideas-401-500.md"
      - "docs/runtime/achievements.md"
      - "docs/runtime/facts.md"
      - "docs/runtime/loader.md"
      - "docs/gui/achievements.md"
      - "docs/governance/conflicts/contradictions.md"
      - "docs/governance/changes/change-records.md"
    notes: "Achievement catalog now generated from series; ordering standardized to category order then id."
  ```
