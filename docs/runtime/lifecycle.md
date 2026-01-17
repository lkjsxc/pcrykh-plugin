# Lifecycle

## Startup

- Load config.
- Validate spec_version.
- Initialize storage.
- Register `/pcrykh`.
- Register listeners for objective criteria.

## Shutdown

- Flush pending writes.
- Close DB cleanly.
