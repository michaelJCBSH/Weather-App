# Claude template for the Weather app

Agent instructions for a new Android project (`com.jcbsh.weather`), derived from this repo's `AGENTS.md`.

## Setup

1. Copy `AGENTS.md`, `CLAUDE.md` and `GLOSSARY.md` into the new project's root.
2. Copy this repo's `detekt-config.yml` into the new project's root and trim the Woolworths-specific rules.
3. Port the skills below.

## Skills worth porting

These skills in this repo's `.github/skills/` are generic enough to adapt. Copy each into the new project's `.claude/skills/` and remove the Woolworths-specific parts:

- `compose-previews-and-paparazzi`
- `compose-robolectric-ui-interaction-tests`
- `gradle-detekt-fix-code`
- `pre-pr-review`
- `figma-to-compose`
- `android-device-control`
- `remove-feature-toggles`
