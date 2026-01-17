# Criteria types

- node: docs/domain/criteria/types.md
  - common_fields:
    - `type` (string, required)
    - `count` (integer, required, `>= 1`)
    - `constraints` (object, required)
  - block_break:
    - schema:
      ```json
      {
        "type": "block_break",
        "materials": ["string"],
        "count": 1,
        "constraints": {}
      }
      ```
    - rules:
      - `materials` MUST be a non-empty array of strings
  - item_craft:
    - schema:
      ```json
      {
        "type": "item_craft",
        "item": "string",
        "count": 1,
        "constraints": {}
      }
      ```
    - rules:
      - `item` MUST be a non-empty string
  - entity_kill:
    - schema:
      ```json
      {
        "type": "entity_kill",
        "entities": ["string"],
        "count": 1,
        "constraints": {}
      }
      ```
    - rules:
      - `entities` MUST be a non-empty array of strings
  - fish_catch:
    - schema:
      ```json
      {
        "type": "fish_catch",
        "items": ["string"],
        "count": 1,
        "constraints": {}
      }
      ```
    - rules:
      - `items` MUST be a non-empty array of strings
