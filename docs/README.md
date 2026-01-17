# pcrykh — LLM-First Specification

This documentation is the source of truth.

We assume all existing source code will be deleted and rebuilt from these docs; therefore:

- Backward compatibility is irrelevant.
- Anything not specified here does not exist.
- This spec defines a single command entrypoint: `/pcrykh`.

## Structure overview

This spec is organized into layered sections:

- Foundation: scope, glossary, versioning, and guiding context.
- Domain: gameplay systems and progression model.
- Interaction: player-facing commands, GUI, and chat behaviors.
- Data: persistence model and schemas.
- Config: runtime configuration surfaces.
- Runtime: lifecycle, autosave, performance, testing.
- Governance: conflict tracking and change control.

## Top-level map

- [foundation/README.md](foundation/README.md)
- [domain/README.md](domain/README.md)
- [interaction/README.md](interaction/README.md)
- [data/README.md](data/README.md)
- [config/README.md](config/README.md)
- [runtime/README.md](runtime/README.md)
- [governance/README.md](governance/README.md)