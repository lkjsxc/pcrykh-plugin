# Action Bar Events

This document specifies which game events MUST produce action bar output.

## Achievement Points (AP) awards

- On any objective completion that grants AP, the plugin MUST emit an **AP** action bar message.
- The progress displayed MUST be the current progress toward the **next** objective after the award is applied.

## Achievement progress updates

- On any qualifying action that increments objective progress, the plugin MUST emit an **Achievement Progress** action bar message.
- These progress messages MUST be throttled (see [throttling.md](throttling.md)).

## Optional extensions (allowed)

These are explicitly allowed but not required:

- Progress updates (objective progress increments) MAY be shown on the action bar, but MUST be throttled.
- Level-up celebration MAY also emit a chat message, title, or sound.

## Conflict note

- The statement "GUIs MUST be inventory-based" applies only to inventory UIs.
- Action bar output is HUD feedback and does not violate the GUI constraint.
