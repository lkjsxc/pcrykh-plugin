# Conflict resolutions

This document records resolved contradictions and the winning rules.

## Resolved conflicts

### C-005 — Skills vs achievements

**Conflict**

- Skill-based specs define progression, config, data, GUI, and commands around skills.
- The achievement format replaces skills entirely.

**Resolution**

- The achievement format wins. All skill-based specs are removed and replaced with achievement definitions.

### C-006 — Achievement id field name

**Conflict**

- [achievements/definitions/format.md](../achievements/definitions/format.md) used `achievement_id`.
- [config/runtime-config.md](../config/runtime-config.md) and runtime usage require `id`.

**Resolution**

- The canonical field name is `id`.
- All references to `achievement_id` in achievement definitions are superseded and removed.
