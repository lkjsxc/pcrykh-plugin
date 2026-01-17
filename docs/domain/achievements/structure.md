# Achievement catalog structure

- node: docs/domain/achievements/structure.md
  - grouping:
    - achievements are grouped by `category_id`
    - categories are ordered by `categories.order`, then `categories.name`
  - sorting:
    - within each category, achievements are ordered by ascending `id`
  - invariants:
    - category list MUST be non-empty
    - every achievement MUST reference a category in the merged catalog
