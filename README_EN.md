<div align="center">
  <b>English</b> | <a href="README.md">中文</a>
</div>

---

# SlashExpansion

**SlashExpansion** is an add-on mod for [SlashBlade: Resharped](https://www.curseforge.com/minecraft/mc-mods/slashblade-resharped) on Minecraft 1.20.1, introducing **12 brand-new Special Attacks (SAs)** and **12 powerful Special Effects (SEs)** to the game.

The mod **adds no new blades** — it focuses purely on enriching the skill system. All SA damage is dynamically calculated based on the blade you're currently holding, scaling with your gear and stats as you progress.

---

## ✨ New Special Attacks

| SA Name                         | Effect                                                                                       | Damage Multiplier | Acquisition                               |
| :------------------------------ | :------------------------------------------------------------------------------------------- | :---------------- | :---------------------------------------- |
| **Wan Jian Gui Zong**     | Summons 10–256 phantom blades from the sky; count scales with your XP level                 | 80%               | Ancient City (10%), Nether Fortress (8%)  |
| **Senbonzakura**          | Cherry blossom blade ring; 4–6 blades per target, piercing, with petal particles            | 120%              | Ancient City (20%)                        |
| **Rift Slash**            | Dash forward + 8-direction projectile wave                                                   | 180%              | End Ship/City (25%)                       |
| **Frost Domain**          | Freezes enemies in a 20-block radius, applies Slowness X, Weakness II, and Mining Fatigue IV | 80%               | Ancient City (15%)                        |
| **Inferno**               | Ground-shattering flame eruption + 12 seconds of burning; +50% damage to Nether mobs         | 100% + burn       | Bastion Treasure (15%)                    |
| **Zhan Tian Ba Jian Shu** | Massive vertical energy slash, travels 64 blocks, ignores obstacles                          | 150%              | Ancient City (10%), End City (12%)        |
| **Jian Dang Ba Huang**    | Massive horizontal energy slash, travels 64 blocks, ignores obstacles                        | 150%              | Ancient City (10%), End City (12%)        |
| **Geng Jin Jian Jue**     | 1–100 golden phantom blades (count tied to refine count), ignores armor                     | 160%              | Ancient City (8%), Bastion Treasure (10%) |
| **Qi Sha Jian Jie**       | Domain deals 7 sequential hits, cycling through 7 damage types; range scales with kill count | 100% × 7         | Ancient City (5%), Nether Fortress (6%)   |
| **Blood Moon Slash**      | Sacrifices 30% of current health to deal base damage + converted damage from the consumed health (scales with refine, ignores armor) to enemies within 8 blocks in front | Base + converted | Nether Fortress (10%) |
| **Thor's Wrath**          | Calls down 7 lightning bolts on random enemies within 20 blocks, each dealing 120% damage and 3s paralysis | 120% × 7 | Bastion Treasure (12%) |
| **Immovable King**        | Immune to all damage but unable to move for 3s, then deals 200% damage in a 10-block radius with knockback | 200% | Bastion Treasure (8%) |

> Note: **Thor's Wrath** lightning bolts are visual-only by default (`[sa.thors_wrath] visualOnly = true`), so they do not deal vanilla lightning damage or set fires.

### Damage Calculation

All SAs share the same base damage formula:

```
Base Damage = max(10.0, Blade Base Damage + Refine×0.5 + Strength Enchantment Level×2.0 + Player's Base Attack)
```

This base is then multiplied by each SA's **damage multiplier**, ensuring that skills stay relevant alongside your gear progression.

> The damage floor, refine bonus, power bonus and every SA's multiplier can all be tuned in the config file.

---

## ✨ New Special Effects

| SE Name                         | Effect                                                                                                                                                                                                      |
| :------------------------------ | :---------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------- |
| **Power of the Wind God** | Jump Boost III, Fall Immunity, Movement Speed +100%, Flight Speed +150% (main hand only)                                                                                                                    |
| **Frost Glow**            | Immune to frost damage, attacks deal bonus frost damage                                                                                                                                                     |
| **Transcendent Will**     | Enchantments +5, Damage ×1.5, Crit Chance +25%                                                                                                                                                             |
| **Abyss Whisper**         | Low level (≤99): Self Nausea I + Hunger I + Bad Luck V + Wither II; Applies Wither X, Blindness I, Weakness V, Slowness V, Mining Fatigue III to enemies. High level (≥100): Self Bad Luck III + Wither I |
| Power Of The Void               | Immune to void damage and debuffs, attacks deal bonus void damage and debuffs                                                                                                                               |
| Power Of The Administrator      | (required OP) Immune to all damage, debuffs and knockback; can fly, auto-heals and clears debuffs; melee attacks instantly kill the target (3-block splash by default, configurable). Damage/removal/kicks are blocked at Mixin level |
| **Flame Heart**                 | Attacks ignite enemies for 5s; immune to fire damage; +20% movement speed in the Nether                                                                                                                     |
| **Void Walker**                 | Hovers when stepping off blocks; +30% speed in the End; ×2 damage to Endermen                                                                                                                               |
| **Tempered Soul**               | Durability cost halved; +1 durability per kill; every 10 kills consume 100 proud souls for +1 refine (max 100 times) |
| **Grim Reaper's Pact**          | Below 30% health: +50% attack, but lose 1 HP per second (never lethal)                                                                                                                                      |
| **Elven Blessing**              | Day: +20% speed; Night: +30% attack; immune to poison; +50% arrow damage                                                                                                                                     |
| **Dragon Soul Resonance**       | Permanent +10% attack after slaying the Ender Dragon; 5% chance on hit to unleash dragon breath                                                                                                             |

---

## 🎯 Survival Mode Acquisition

> Every structure, SA and chance in the table below can be changed in the `[loot]` config section.

### SA Acquisition: Structure Chests

Special **proudsoul_sphere** with embedded SAs can be found in chests from the following structures:

| Structure                  | Available SAs                                                                                                                | Chance                                |
| :------------------------- | :--------------------------------------------------------------------------------------------------------------------------- | :------------------------------------ |
| Ancient City               | Wan Jian Gui Zong, Senbonzakura, Frost Domain, Zhan Tian Ba Jian Shu, Jian Dang Ba Huang, Geng Jin Jian Jue, Qi Sha Jian Jie | 10% / 20% / 15% / 10% / 10% / 8% / 5% |
| End Ship / End City        | Rift Slash, Zhan Tian Ba Jian Shu, Jian Dang Ba Huang                                                                        | 25% / 12% / 12%                       |
| Bastion Remnant (Treasure) | Inferno, Geng Jin Jian Jue, Thor's Wrath, Immovable King                                                                     | 15% / 10% / 12% / 8%                  |
| Nether Fortress            | Wan Jian Gui Zong, Qi Sha Jian Jie, Blood Moon Slash                                                                         | 8% / 6% / 10%                         |

### SE Acquisition: Crafting

All SEs are obtained through crafting.
(Check JEI/Recipe book for Proud Soul Crystal recipes. Recipes can be freely edited in the `[recipe]` config section.)
PS : The recipe of "Power of the Wind God" : Bottom left is strong_swift potion, bottom right is strong_leaping potion; for some reason they are not displaying properly.

> Note: the 3×3 crafting grid holds at most 9 slots, so **Dragon Soul Resonance** costs 1 Dragon Egg + 7 Dragon's Breath + 1 Proud Soul Crystal.

### How to Apply

1. Obtain a proudsoul_sphere with the desired SA or a Proud Soul Crystal with the desired SE.
2. Place the Core/Crystal and any SlashBlade onto a Blade Stand.
3. Left-click to transfer the SA/SE to the blade.
4. **SA**: Hold the blade, then right-click and hold to unleash the skill.
   **SE**: Passive effect — active while held.

---

## ⚙️ Configuration

All tunable values, **SA loot acquisition** and **SE crafting recipes** are defined in
**`config/slashexpansion-common.toml`** (generated on first launch).

Numeric changes take effect after restarting the game; `[loot]` / `[recipe]` changes apply after a server restart or `/reload`.

| Section                  | Contents                                                                        |
| :----------------------- | :------------------------------------------------------------------------------ |
| `[damage]`               | Base damage formula: damage floor, refine bonus, Power enchantment bonus        |
| `[sa.<skill>]`           | Per-SA damage multiplier, search range, sword count, radius, effect duration/level |
| `[entities.giant_slash]` | Giant slash (Zhan Tian Ba Jian Shu / Jian Dang Ba Huang) flight and hit settings |
| `[se.<effect>]`          | Per-SE duration, amplifier, multiplier, check interval                          |
| `[loot]`                 | How SAs are obtained from structure chests                                      |
| `[recipe]`               | SE crafting recipes                                                             |

### SA Acquisition (`[loot]`)

```toml
[loot]
enabled = true
item = "slashblade:proudsoul_sphere"     # dropped item
# Each entry: "loot table id|SA id|chance (0~1)"
entries = [
  "minecraft:chests/ancient_city|slashexpansion:wan_jian_gui_zong|0.10",
  "minecraft:chests/end_city_treasure|slashexpansion:rift_slash|0.25"
]
```

### SE Crafting (`[recipe]`)

```toml
[recipe]
overrideJsonRecipes = true               # whether this config overrides the built-in JSON recipes

[recipe.frost_glow]                      # one block per SE (12 in total)
enabled = true
shapeless = false                        # true = shapeless recipe from `ingredients`
resultItem = "slashblade:proudsoul_crystal"
pattern = ["CIC", "BWP", "CAC"]          # 1~3 rows, 1~3 chars per row, space = empty
keys = ["C=slashblade:proudsoul_crystal", "I=minecraft:blue_ice"]
ingredients = []                         # used only when shapeless = true
```

- Recipe ids are fixed as `slashexpansion:<se id>_proudsoul_crystal`, so configs replace the built-in recipes directly.
- `enabled = false` removes that recipe; `overrideJsonRecipes = false` keeps the built-in JSON recipes (datapack-overridable) instead.
- The result always carries `SpecialEffectType = slashexpansion:<se id>`.

---

## 🛠️ Test Commands (Creative Mode)

### SA Testing

```
/give @p slashblade:slashblade{SpecialAttackType:"slashexpansion:wan_jian_gui_zong"}
```

Replace `wan_jian_gui_zong` with any of the following:

- `senbonzakura` — Senbonzakura
- `rift_slash` — Rift Slash
- `frost_domain` — Frost Domain
- `inferno` — Inferno
- `zhan_tian_ba_jian_shu` — Zhan Tian Ba Jian Shu
- `jian_dang_ba_huang` — Jian Dang Ba Huang
- `geng_jin_jian_jue` — Geng Jin Jian Jue
- `qi_sha_jian_jie` — Qi Sha Jian Jie
- `blood_moon_slash` — Blood Moon Slash
- `thors_wrath` — Thor's Wrath
- `immovable_king` — Immovable King

### SE Testing

```
/give @p slashblade:proudsoul_crystal{SpecialEffectType:"slashexpansion:wind_god_power"}
```

Replace `wind_god_power` with any of the following:

- `frost_glow` — Frost Glow
- `transcendent_will` — Transcendent Will
- `abyss_whisper` — Abyss Whisper
- `void_power` — Power Of The Void
- `admin` — Power Of The Administrator
- `flame_heart` — Flame Heart
- `void_walker` — Void Walker
- `tempered_soul` — Tempered Soul
- `grim_reapers_pact` — Grim Reaper's Pact
- `elven_blessing` — Elven Blessing
- `dragon_soul_resonance` — Dragon Soul Resonance

---

## 📦 Dependencies

| Dependency            | Version |
| :-------------------- | :------ |
| Minecraft             | 1.20.1  |
| SlashBlade: Resharped | 1.5.0+  |

> The dev compile dependency is SlashBlade: Resharped **1.9.65** (Modrinth Maven); 1.5.0+ is supported at runtime.

---

## ⚙️ Developer Information

### Building

```bash
gradlew build
```

The built JAR will be located at `build/libs/slashexpansion-1.9.4.jar`.

### Importing into IDE

- **IntelliJ IDEA**: `gradlew idea`
- **Eclipse**: `gradlew eclipse`

### Project Structure

```
src/main/
├── java/com/lisheng/slashexpansion/
│   ├── SlashExpansion.java                # Main class
│   ├── config/
│   │   ├── SlashExpansionConfig.java      # Config definition (all tunable values)
│   │   └── ConfigRecipeInjector.java      # Builds & injects SE recipes from config
│   ├── util/ DamageCalculator.java        # Dynamic damage calculation
│   ├── entity/                            # Custom entities
│   ├── specialattack/                     # 9 SA implementations
│   ├── specialeffect/                     # 6 SE implementations
│   ├── mixin/                             # Mixin guards (health / removal / kick)
│   ├── registry/                          # Registration classes
│   ├── loot/ LootInjector.java            # Config-driven loot injection
│   └── client/ ClientEvents.java          # Client-side rendering
└── resources/
    ├── slashexpansion.png
    ├── pack.mcmeta
    ├── META-INF/mods.toml
    ├── data/slashexpansion/recipes/       # Built-in default recipes (overridable by config)
    └── assets/slashexpansion/
        └── lang/
            ├── zh_cn.json                 # Chinese language file
            └── en_us.json                 # English language file
```
