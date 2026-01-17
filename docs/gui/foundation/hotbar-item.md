# Hotbar item

This document specifies the **pcrykh hotbar item** behavior.

## Identity

- The hotbar item is the primary, always-available GUI entrypoint.
- It MUST be uniquely identifiable via plugin-owned metadata.

## Placement

- The hotbar item MUST be fixed at the far-right hotbar slot (slot index `8`).
- If slot 8 is occupied by a non-pcrykh item, the system MUST relocate that item to the first empty slot.
- If no empty slot exists, the system MUST drop the displaced item at the player’s feet.

## Persistence

- The hotbar item MUST be enforced on:
  - Player join
  - Player respawn
  - Inventory interaction events

## Interaction

- Right-clicking the hotbar item MUST open the main menu (same as `/pcrykh`).
- The item MUST NOT be droppable or moveable by the player.

## Non-goals

- The hotbar item is not a cosmetic reward.
- The hotbar item is not a consumable.
