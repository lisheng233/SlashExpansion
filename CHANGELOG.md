**Full Changelog**: https://github.com/lisheng233/SlashExpansion/compare/1.5...2.0

## 2.0

### Fixed

- Rift Slash dealt roughly ten times the intended damage: attack damage was applied twice
  (`EntityDrive` already carries it). A single slash no longer one-shots a Warden.

- Ghost players freezing the server: `setRemoved(KILLED)` is intercepted in addition to
  `remove(KILLED)`, and disconnects are allowed through when the player is already removed
  or dying.

- Server freeze caused by the removal interception.
- Clearing effects no longer wipes a creative player's abilities.
- Blood Moon Slash now deals base damage plus health-converted damage (armor-ignoring),
  scaling with the refine count.

- Admin's Might instakill fallback is delayed by one tick and removes the entity from the
  level side, bypassing mods that intercept `Entity#remove`.

- Admin's Might instakill fallback now triggers the level-side removal callback and sends
  the client remove packet, so the target disappears for everyone.

### Added

- Visual effects overhaul. Every SA now plays a multi-stage timeline animation
  (charge → slash → impact → aftermath) instead of a single particle burst.
- Two procedurally drawn particles: `glow` (soft halo) and `rune` (ground circle).
- Screen presentation: impact flash, edge vignette and camera shake, sent to nearby players.
- `[vfx]` config section: master switch, particle scale, concurrent effect limit,
  per-tick particle budget, screen flash / shake toggles and intensity. Hot-reloaded.

- 3 new SAs: Blood Moon Slash, Thor's Wrath, Immovable King.
- 6 new SEs: Flame Heart, Void Walker, Tempered Soul, Grim Reaper's Pact,
  Elven Blessing, Dragon Soul Resonance.
- SlashExpansion now ships 12 SAs and 12 SEs.

- Mixin configuration; Admin's Might now intercepts `setHealth`, entity removal and kicks,
  with a removal fallback for instakills.
- Admin's Might: stronger damage immunity and instakill.

### Changed

- Effects are driven server-side by a tick director; disabling them never changes damage.

- Tempered Soul now converts kills and proud souls into refine: every 10 kills consume
  100 proud souls for +1 refine, up to 100 times.

- Development dependency raised to SlashBlade: Resharped 1.9.65.
- Thor's Wrath lightning is visual only by default (`[sa.thors_wrath] visualOnly`).

- All hard-coded numbers moved into the config file.
- SA chest loot and SE crafting recipes are configurable (`[loot]`, `[recipe]`).

---
