# Conflict resolutions

## C-006 — Achievement id field name

**Conflict**

- Some data specs referenced `achievement_id`.
- Runtime config and model require `id`.

**Resolution**

- The canonical field name is `id` everywhere.

## C-007 — Criteria type list vs config usage

**Conflict**

- Config examples used `block_break`.
- Criteria type list omitted `block_break`.

**Resolution**

- `block_break` is canonical and included in criteria types.

## C-008 — TOC canonicality vs README authority

**Conflict**

- README files were required to be the sole TOC.
- A rule declared that only items listed in the TOC are canonical, which excluded the README itself.

**Resolution**

- README files are canonical index nodes.
- Canonical spec content is limited to paths listed in each README's TOC.
