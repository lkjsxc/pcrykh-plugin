# Action Bar

This directory specifies all **HUD/action-bar** feedback.

This is not an inventory GUI; it is a one-line, time-critical feedback channel.

## TOC

- [format.md](format.md)
- [events.md](events.md)
- [throttling.md](throttling.md)

## Global invariants

- The action bar MUST be used for high-frequency feedback.
- When a player gains **Achievement Points (AP)**, the plugin MUST emit an AP action bar message.
- Action bar messages MUST be considered ephemeral and MAY be overwritten by later messages.
- Messages MUST be concise and MUST fit into a single line.

## Non-goals

- The action bar is not a persistent log.
- The action bar is not the source of truth for state.
