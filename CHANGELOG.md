# Changelog

English changelog for the 1.20.1 / Forge line. The 1.21.1 line has its own changelog in that repository.

## 1.10.1

### Fixed

- Rift Slash dealt roughly ten times the intended damage: attack damage was applied twice
  (`EntityDrive` already carries it). A single slash no longer one-shots a Warden.

## 1.10.0

### Added

- Visual effects overhaul. Every SA now plays a multi-stage timeline animation
  (charge → slash → impact → aftermath) instead of a single particle burst.
- Two procedurally drawn particles: `glow` (soft halo) and `rune` (ground circle).
- Screen presentation: impact flash, edge vignette and camera shake, sent to nearby players.
- `[vfx]` config section: master switch, particle scale, concurrent effect limit,
  per-tick particle budget, screen flash / shake toggles and intensity. Hot-reloaded.

### Changed

- Effects are driven server-side by a tick director; disabling them never changes damage.

## 1.9.4

### Fixed

- Ghost players freezing the server: `setRemoved(KILLED)` is intercepted in addition to
  `remove(KILLED)`, and disconnects are allowed through when the player is already removed
  or dying.

## 1.9.3

### Fixed

- Server freeze caused by the removal interception.
- Clearing effects no longer wipes a creative player's abilities.
- Blood Moon Slash now deals base damage plus health-converted damage (armor-ignoring),
  scaling with the refine count.

### Changed

- Tempered Soul now converts kills and proud souls into refine: every 10 kills consume
  100 proud souls for +1 refine, up to 100 times.

## 1.9.2

### Fixed

- Admin's Might instakill fallback now triggers the level-side removal callback and sends
  the client remove packet, so the target disappears for everyone.

## 1.9.1

### Fixed

- Admin's Might instakill fallback is delayed by one tick and removes the entity from the
  level side, bypassing mods that intercept `Entity#remove`.

## 1.9

### Added

- Mixin configuration; Admin's Might now intercepts `setHealth`, entity removal and kicks,
  with a removal fallback for instakills.

## 1.8

### Added

- Admin's Might: stronger damage immunity and instakill.

### Changed

- Development dependency raised to SlashBlade: Resharped 1.9.65.
- Thor's Wrath lightning is visual only by default (`[sa.thors_wrath] visualOnly`).

## 1.7

### Added

- 3 new SAs: Blood Moon Slash, Thor's Wrath, Immovable King.
- 6 new SEs: Flame Heart, Void Walker, Tempered Soul, Grim Reaper's Pact,
  Elven Blessing, Dragon Soul Resonance.
- SlashExpansion now ships 12 SAs and 12 SEs.

## 1.6

### Changed

- All hard-coded numbers moved into the config file.
- SA chest loot and SE crafting recipes are configurable (`[loot]`, `[recipe]`).

---

Releases before 1.6 predate this changelog.