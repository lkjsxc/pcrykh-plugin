# Versioning

## Spec version

- This spec is versioned conceptually as `spec_version`.
- Implementations MUST store `spec_version` in persisted player data.

## Compatibility

- There is no promise of compatibility between spec versions.
- A version bump MAY require a full reset; migrations are OPTIONAL and only required if explicitly mandated by a newer spec.
