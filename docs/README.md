# pcrykh — LLM-First Specification (Canonical)

All behavior is defined here. Any behavior not specified does not exist.

### Directory map

- [foundation/README.md](foundation/README.md)
- [domain/README.md](domain/README.md)
- [config/README.md](config/README.md)
- [runtime/README.md](runtime/README.md)
- [governance/README.md](governance/README.md)

### Global rules

- Backward compatibility is irrelevant.
- JSON config is authoritative runtime input.
- The single command entrypoint is `/pcrykh`.
- The runtime MUST load achievements via pack expansion as specified in the config schema.
- Only directories and files listed in the TOC are canonical. Anything else is obsolete and MUST be ignored.