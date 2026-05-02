## Additions

- Added `priority` for UI Definitions, so with higher values, you're able to apply it first, and avoid conflicts (with this, `replace` will work properly)
- Added `blit_custom_sprite` for rendering sprites with a specific `textureWidth`, `textureHeight`, `uvX` and `uvY`
- Fixed advanced text widget not working the url click event when there isn't a world in versions >=1.21.6

## Changes

- Support for 1.21.11
- Support for 26.1.2

## Fixes

- Fixed external components (using text files for translations) and components with variables not allowing fonts and styles