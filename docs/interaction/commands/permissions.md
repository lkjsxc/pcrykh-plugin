# Permissions

## Permission nodes

This spec defines the following nodes:

- `pcrykh.use` (default: true)
  - Allows `/pcrykh`, GUI access, and viewing own profile.
- `pcrykh.use.others` (default: op)
  - Allows viewing others’ profiles via `/pcrykh profile <player>`.
- `pcrykh.admin` (default: op)
  - Allows `/pcrykh admin ...`.

## Enforcement rules

- Permissions MUST be checked server-side for every command and GUI action that mutates state.
- Unauthorized access MUST produce:
  - A single chat message, and
  - A GUI “deny” sound (if invoked from GUI).
