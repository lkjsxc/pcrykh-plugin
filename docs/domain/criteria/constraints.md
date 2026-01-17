# Constraints

Constraints are opaque objects passed through without validation.

Rules:

- `constraints` MUST exist in every criteria object, even if empty.
- `constraints` MUST be a JSON object.
- The runtime MUST NOT interpret or validate constraint keys.
