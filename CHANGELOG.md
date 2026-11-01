# Changelog

English changelog. The 1.20.1 / Forge line keeps its own history in that repository.

## 2.0.0 — Minecraft 1.21.1 · NeoForge

First release of SlashExpansion for **Minecraft 1.21.1 / NeoForge 21.1.249**, built against
**SlashBlade: Resharped 2.1.9-1.21.1**. Content matches the 1.20.1 line: 12 Special Attacks,
12 Special Effects, the full VFX timeline and all four protection mixins.

### Added

- Ported the whole mod to NeoForge 1.21.1: 12 SAs, 12 SEs, 4 custom entities, custom
  glow/rune particles, timed animation director, screen flash / vignette / camera shake,
  and the network channel for screen effects.
- `ConfigRecipeInjector` ported: all 12 SE crafting recipes are generated from the config
  and can be reshaped (pattern, ingredients, shapeless, on/off) without a data pack.
- SA chest loot reimplemented as a Global Loot Modifier (1.21 removed `LootTableLoadEvent`).
  The `"table|sa|chance"` config format is unchanged from 1.20.1.
- `/slashexpansion vfx <name>` and `/slashexpansion vfx list` are available again.
- `/slashexpansion vfx ping` — a diagnostic that emits one ring of custom glow particles,
  a rune, vanilla particles and a screen flash, then reports how many effects are queued.
  Useful to tell "effects never fired" apart from "particles do not render".

### Changed

- Blade state is read through SlashBlade 2.x **data components** (`BladeStateAccess`)
  instead of capabilities; NBT on items is replaced by `minecraft:custom_data`.
- Enchantment handling uses `ItemEnchantments` with `Holder<Enchantment>` (1.21 registries).
- Attribute modifiers use `ResourceLocation` ids and `ADD_MULTIPLIED_BASE`.
- Networking rewritten on `CustomPacketPayload` + `RegisterPayloadHandlersEvent`.
- Damage and effect events moved to `LivingIncomingDamageEvent`,
  `PlayerTickEvent.Post` and `ServerTickEvent.Post`.
- Recipes ship in the 1.21 `recipe/` format; the SE marker on the result item is written to
  the `minecraft:custom_data` component.
- Admin's Might kick protection now hooks `ServerCommonPacketListenerImpl#disconnect`.

### Fixed

- **Phantom swords did not render.** Wan Jian Gui Zong, Geng Jin Jian Jue and Senbonzakura
  now use SlashBlade's `SummonedSwordRenderer`, so the blade model is visible again.
- **Custom particles were invisible** when a caller passed a colour without an alpha byte;
  alpha `0` is now treated as fully opaque.
- **Only 6 of 12 SEs were obtainable.** The config-driven recipe generator is back, so every
  SE has a recipe, and `overrideJsonRecipes = false` still falls back to the 6 bundled JSONs.

### Notes

- Requires Minecraft 1.21.1, NeoForge 21.1.249 or newer, and SlashBlade: Resharped 2.1.9-1.21.1.
- Install on both client and server.