# Achievement catalog structure

- node: docs/domain/achievements/structure.md
  - grouping:
    - achievements are grouped by `category_id`
    - categories are ordered by `categories.order`, then `categories.name`
  - sorting:
    - within each category, achievements are ordered by ascending `id`
  - sources:
    - the catalog is assembled from single-achievement files
    - achievement files SHOULD be organized by category directories
    - each achievement file MUST remain under 300 lines
    - category files provide ordering and icon metadata
    - pack/template expansion is not supported
    - grouped or incremental achievement definitions are not supported
  - invariants:
    - category list MUST be non-empty
    - every achievement MUST reference a category in the merged catalog
