# Principles

This directory defines the meta-rules for the specification.

## TOC

- [spec-language.md](spec-language.md)
- [scope-and-non-goals.md](scope-and-non-goals.md)
- [versioning.md](versioning.md)

## Normative definitions

- **Implementation**: Any code/artifacts generated to satisfy this specification.
- **Spec**: The documentation under `docs/`.
- **Authoritative**: If there is a conflict between spec and code, the spec wins.

## Hard requirements

- The plugin MUST expose exactly one command root: `/pcrykh`.
- The GUI MUST be the primary user interface for non-admin interactions.
- The entire system MUST be rebuildable solely from this spec.
