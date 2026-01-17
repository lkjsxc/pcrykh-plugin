# Change records

## Purpose

- Record current-spec changes for LLM ingestion.
- Records describe what is now true, not historical compatibility.

## Record format (canonical)

```yaml
- id: "CR-YYYYMMDD-NNN"
  date: "YYYY-MM-DD"
  summary: "string"
  files:
    - "path/to/file.md"
  notes: "optional"
```

Rules:

- Records MUST be pruned when they no longer describe current-spec behavior.
- Records MUST include all modified spec files for the change.
- Backward compatibility notes MUST be omitted.

## Records

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
    - "docs/governance/changes/deprecations.md"
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
```
