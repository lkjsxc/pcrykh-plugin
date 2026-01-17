# Screen: Achievements Menu

## Purpose

Browse achievements and open a specific achievement.

## Required elements

- A grid of achievement items.
- Each item MUST display:
  - Current tier
  - Current objective (next incomplete tier)
  - Progress summary (numeric progress + percentage)

## Pagination

- The achievements grid MUST paginate when the list exceeds one page.
- Page size MUST be 28 items (4 rows × 7 columns).
- The menu MUST expose:
  - Previous Page control
  - Next Page control
  - Page indicator (e.g., `Page 2/5`)

## Sorting

- Achievements MUST be displayed in a stable order defined by config.
