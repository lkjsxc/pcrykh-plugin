# pcrykh — LLM-First Specification

This documentation is the source of truth.

We assume all existing source code will be deleted and rebuilt from these docs; therefore:

- Backward compatibility is irrelevant.
- Anything not specified here does not exist.
- This spec defines a single command entrypoint: `/pcrykh`.

## How to navigate

This spec is layered. Read top-down, then dive into details:

1. Foundation sets vocabulary, scope, and versioning.
2. Domain defines what the system *is* (achievements, progression).
3. Interaction defines how players touch the system (GUI, commands, chat).
4. Data and Config describe persistence and runtime inputs.
5. Runtime covers lifecycle and operational behavior.
6. Governance records conflicts and change flow.

Each directory has a README that acts as both table of contents and local guide.

## Design intent

- The docs are written for LLMs and rebuild automation.
- This spec is intended to be sufficient to rebuild from scratch.
- Avoid relying on existing code; treat the docs as canonical.

## Top-level map

- [foundation/README.md](foundation/README.md)
- [domain/README.md](domain/README.md)
- [interaction/README.md](interaction/README.md)
- [data/README.md](data/README.md)
- [config/README.md](config/README.md)
- [runtime/README.md](runtime/README.md)
- [governance/README.md](governance/README.md)