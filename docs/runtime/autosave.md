# Autosave

## Requirement

- The implementation MUST periodically persist dirty player records.

## Safety

- Autosave MUST not run on the main thread if it can block.
