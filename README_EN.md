<div align="center">
  <b>English</b> | <a href="README.md">中文</a>
</div>

---

# SlashExpansion

**SlashExpansion** is an add-on mod for [SlashBlade: Resharped](https://www.curseforge.com/minecraft/mc-mods/slashblade-resharped) on Minecraft 1.20.1, introducing **9 brand-new Special Attacks (SAs)** and 6 **powerful Special Effects (SEs)** to the game.

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

### Damage Calculation

All SAs share the same base damage formula:

```
Base Damage = max(10.0, Blade Base Damage + Refine×0.5 + Strength Enchantment Level×2.0 + Player's Base Attack)
```

This base is then multiplied by each SA's **damage multiplier**, ensuring that skills stay relevant alongside your gear progression.

---

## ✨ New Special Effects

| SE Name                         | Effect                                                                                                                                                                                                      |
| :------------------------------ | :---------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------- |
| **Power of the Wind God** | Jump Boost III, Fall Immunity, Movement Speed +100%, Flight Speed +150% (main hand only)                                                                                                                    |
| **Frost Glow**            | Immune to frost damage, attacks deal bonus frost damage                                                                                                                                                     |
| **Transcendent Will**     | Enchantments +5, Damage ×1.5, Crit Chance +25%                                                                                                                                                             |
| **Abyss Whisper**         | Low level (≤99): Self Nausea I + Hunger I + Bad Luck V + Wither II; Applies Wither X, Blindness I, Weakness V, Slowness V, Mining Fatigue III to enemies. High level (≥100): Self Bad Luck III + Wither I |
| Power Of The Void               | Immune to void damage and debuffs, attacks deal bonus void damage and debuffs                                                                                                                               |
| Power Of The Administrator      | (required OP) Immune to damage and debuffs, attacks will kill the target, etc.                                                                                                                          |

---

## 🎯 Survival Mode Acquisition

### SA Acquisition: Structure Chests

Special **proudsoul_sphere** with embedded SAs can be found in chests from the following structures:

| Structure                  | Available SAs                                                                                                                | Chance                                |
| :------------------------- | :--------------------------------------------------------------------------------------------------------------------------- | :------------------------------------ |
| Ancient City               | Wan Jian Gui Zong, Senbonzakura, Frost Domain, Zhan Tian Ba Jian Shu, Jian Dang Ba Huang, Geng Jin Jian Jue, Qi Sha Jian Jie | 10% / 20% / 15% / 10% / 10% / 8% / 5% |
| End Ship / End City        | Rift Slash, Zhan Tian Ba Jian Shu, Jian Dang Ba Huang                                                                        | 25% / 12% / 12%                       |
| Bastion Remnant (Treasure) | Inferno, Geng Jin Jian Jue                                                                                                   | 15% / 10%                             |
| Nether Fortress            | Wan Jian Gui Zong, Qi Sha Jian Jie                                                                                           | 8% / 6%                               |

### SE Acquisition: Crafting

All SEs are obtained through crafting.
(Check JEI/Recipe book for Proud Soul Crystal recipes.)
PS : The recipe of "Power of the Wind God" : Bottom left is strong_swift potion, bottom right is strong_leaping potion; for some reason they are not displaying properly.

### How to Apply

1. Obtain a proudsoul_sphere with the desired SA or a Proud Soul Crystal with the desired SE.
2. Place the Core/Crystal and any SlashBlade onto a Blade Stand.
3. Left-click to transfer the SA/SE to the blade.
4. **SA**: Hold the blade, then right-click and hold to unleash the skill.
   **SE**: Passive effect — active while held.

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

---

## 📦 Dependencies

| Dependency            | Version |
| :-------------------- | :------ |
| Minecraft             | 1.20.1  |
| SlashBlade: Resharped | 1.5.0+  |

---

## ⚙️ Developer Information

### Building

```bash
gradlew build
```

The built JAR will be located at `build/libs/slashexpansion-1.4.jar`.

### Importing into IDE

- **IntelliJ IDEA**: `gradlew idea`
- **Eclipse**: `gradlew eclipse`

### Project Structure

```
src/main/
├── java/com/lisheng/slashexpansion/
│   ├── SlashExpansion.java                # Main class
│   ├── util/ DamageCalculator.java        # Dynamic damage calculation
│   ├── entity/                            # Custom entities
│   ├── specialattack/                     # 9 SA implementations
│   ├── specialeffect/                     # 6 SE implementations
│   ├── registry/                          # Registration classes
│   ├── loot/ LootInjector.java            # Loot table injection
│   └── client/ ClientEvents.java          # Client-side rendering
└── resources/
    ├── slashexpansion.png
    ├── pack.mcmeta
    ├── META-INF/mods.toml
    ├── data/slashexpansion/recipes/       # Recipes for SEs
    └── assets/slashexpansion/
        └── lang/
            ├── zh_cn.json                 # Chinese language file
            └── en_us.json                 # English language file
```
