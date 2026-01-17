# Lifecycle

## Startup

- Ensure default config resources exist in the data folder.
- Load `config.json` and validate required fields.
- Load achievement pack sources and expand templates.
- Register `/pcrykh`.

## Shutdown

- No persistence actions are performed.

## Command surface

- `/pcrykh` replies with the current achievement count.
