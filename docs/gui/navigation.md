# GUI navigation

## Pagination

- Page size: 45 achievements.
- Page index is zero-based.
- Total pages is `ceil(count / 45)`.

## Navigation slots

- Slot `45`: `ARROW` named `Previous` when `page > 0`; otherwise empty.
- Slot `53`: `ARROW` named `Next` when `page < total_pages - 1`; otherwise empty.
- Slot `49`: `BARRIER` named `Close`.

## Click behavior

- Clicking `Previous` opens `page - 1`.
- Clicking `Next` opens `page + 1`.
- Clicking `Close` closes the inventory.
