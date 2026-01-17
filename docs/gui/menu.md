# GUI menu

## Purpose

- Provide a read-only view of the loaded achievement catalog.

## Inventory

- Size: 54 slots.
- Title: `Pcrykh Achievements`.
- All clicks are cancelled; no item movement is permitted.

## Content layout

- Slots `0..44` render achievements in ascending lexicographic order by `id`.
- If the catalog is empty, slot `22` contains a `BARRIER` named `No achievements loaded.` and all other slots are empty.

## Item rendering

- Material is derived from `icon` using the platform match function.
- If the icon is invalid or unrecognized, use `PAPER`.
- Display name is `title`.
- Lore lines (in order):
  - `description`
  - `id: <id>`
  - `category: <category_id>`
