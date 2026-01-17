# Action Bar Format

This spec targets a dense, informative, consistent feel.

## Achievement Points message

### When

- Emitted whenever AP is awarded.
- In the current progression model, AP is awarded on **objective completion**.

### Required fields

An AP action bar message MUST contain:

- Achievement display name
- Current tier (after applying the award)
- AP gained in this award event
- Total AP (after applying the award)
- Progress toward the next objective (after applying the award)
- Next tier indicator:
  - Either `MAX`, or the next tier number (and MAY include a short next-objective title)

### Canonical string (legacy section colors)

The implementation MUST render this canonical structure (colors MAY vary by theme, but ordering MUST remain stable):

`+{ap_award} AP | {achievement_name} {tier} | AP {ap_total} | Prog {progress}/{target} | Next {next}`

Where:

- `next = MAX` if `next_tier > max_tier`
- else `next = {next_tier}: {short_title}` (short_title MAY be truncated)
- `progress` and `target` refer to the *next* objective after the award is applied

### Constraints

- Any included title MUST be truncated to avoid exceeding action bar width.
- Newlines MUST NOT be present.

## Delivery requirements

- The implementation MUST use a server-supported Action Bar channel.
- If multiple Action Bar delivery APIs exist, the implementation MAY emit through more than one to ensure delivery.

## Achievement Progress message (non-award)

### When

- Emitted on any action that increments objective progress without completing the objective.

### Required fields

- Achievement display name
- Current tier
- Progress toward current objective
- Current objective title (short/truncated)

### Canonical string (legacy section colors)

`{achievement_name} {tier} | Prog {progress}/{target} | Obj {objective_title}`
