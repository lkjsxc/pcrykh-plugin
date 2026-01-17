# Action Bar Format

This spec targets a dense, informative, consistent feel.

## Achievement Points message

### When

- Emitted whenever AP is awarded.
- In the current progression model, AP is awarded on **objective completion**.

### Required fields

An AP action bar message MUST contain:

- Achievement display name
- AP gained in this award event
- Total AP (after applying the award)
- Completion indicator (`Completed`)

### Canonical string (legacy section colors)

The implementation MUST render this canonical structure (colors MAY vary by theme, but ordering MUST remain stable):

`+{ap_award} AP | {achievement_name} | AP {ap_total} | Completed`

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
- Progress toward current objective
- Current objective title (short/truncated)

### Canonical string (legacy section colors)

`{achievement_name} | Prog {progress}/{target} | Obj {objective_title}`
