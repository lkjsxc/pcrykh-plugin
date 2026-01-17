# pcrykh — LLM-First Specification

This documentation is the source of truth.

We assume all existing source code will be deleted and rebuilt from these docs; therefore:

- Backward compatibility is irrelevant.
- Anything not specified here does not exist.
- This spec defines a single command entrypoint: `/pcrykh`.

## Directory invariants (strict)

These rules apply to everything under `docs/`:

1. Every directory contains exactly one `README.md`.
2. That `README.md` is both:
	- A table of contents (TOC) for that directory.
	- The authoritative documentation for that scope.
3. Every directory contains either:
	- Multiple subdirectories, or
	- Multiple Markdown files.

## Document style (LLM-targeted)

- Prefer normative language: **MUST**, **MUST NOT**, **SHOULD**, **MAY**.
- Prefer complete specs over prose.
- Prefer explicit schemas, state machines, and tables over “examples only”.

## Top-level map

- [principles/README.md](principles/README.md)
- [conflicts/README.md](conflicts/README.md)
- [commands/README.md](commands/README.md)
- [gui/README.md](gui/README.md)
- [progression/README.md](progression/README.md)
- [achievements/README.md](achievements/README.md)
- [data/README.md](data/README.md)
- [config/README.md](config/README.md)
- [runtime/README.md](runtime/README.md)