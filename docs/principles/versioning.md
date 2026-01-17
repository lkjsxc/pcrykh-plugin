# Versioning

## Spec version

- This spec is versioned conceptually as `spec_version`.
- Implementations MUST store `spec_version` in persisted player data.

## Compatibility

- There is no promise of compatibility between spec versions.
- Migration rules MUST be specified in [config/migration.md](../config/migration.md) and [data/model/migrations.md](../data/model/migrations.md) when/if a version bump is introduced.
