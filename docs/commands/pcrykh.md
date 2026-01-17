# `/pcrykh` command

## Root invariants

- The only registered Bukkit command MUST be `/pcrykh`.
- All functionality MUST be accessible via subcommands of `/pcrykh`.
- `/pcrykh` with no arguments MUST open the main GUI for players.

## Syntax (normative)

Notation:

- `<x>` required
- `[x]` optional

### Player-facing

- `/pcrykh` → open main menu
- `/pcrykh menu` → open main menu
- `/pcrykh help` → show help summary
- `/pcrykh profile [player]` → open/view profile (self by default)
- `/pcrykh achievements` → open achievements menu
- `/pcrykh achievement <achievement_id>` → open a specific achievement menu

### Admin

- `/pcrykh admin reload` → reload config + runtime definitions
- `/pcrykh admin export <player>` → export player data (debug)
- `/pcrykh admin setap <player> <amount>`
- `/pcrykh admin complete <player> <achievement_id> <tier>` → mark objective tier complete and grant rewards
- `/pcrykh admin reset <player> [scope]` → reset progression

## Tab completion

- Subcommands MUST tab-complete.
- `achievement_id` MUST tab-complete from the configured achievements list.

## Output model

- Non-admin commands SHOULD avoid chat spam (GUI-first).
- Errors MUST be short, structured, and actionable.
