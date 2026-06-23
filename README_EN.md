<div align="center">
  <b>English</b> | <a href="README.md">中文</a>
</div>

---

# SlashExpansion

**SlashExpansion** is an add-on mod for [SlashBlade: Resharped](https://www.curseforge.com/minecraft/mc-mods/slashblade-resharped) on Minecraft 1.20.1, introducing **9 brand-new Special Attacks (SAs)** to the game.

The mod **adds no new blades** — it focuses purely on enriching the skill system. All SA damage is dynamically calculated based on the blade you're currently holding, scaling with your gear and stats as you progress.

---

## ✨ New Special Attacks

| SA Name | Effect | Damage Multiplier | Acquisition |
|:---|:---|:---|:---|
| **Wan Jian Gui Zong** | Summons 10–256 phantom blades from the sky; count scales with your xp level | 80% | Ancient City (10%), Nether Fortress (8%) |
| **Senbonzakura** | Cherry blossom blade ring; 4–6 blades per target, piercing, with petal particles | 120% | Ancient City (20%) |
| **Rift Slash** | Dash forward + 8-direction projectile wave | 180% | End Ship/City (25%) |
| **Frost Domain** | Freezes enemies in a 20-block radius, applies Slowness X, Weakness II, and Mining Fatigue IV | 80% | Ancient City (15%) |
| **Inferno** | Ground-shattering flame eruption + 8 seconds of burning; +50% damage to Nether mobs | 100% + burn | Bastion Treasure (15%) |
| **Zhan Tian Ba Jian Shu** | Massive vertical energy slash, travels 64 blocks, ignores obstacles | 150% | Ancient City (10%), End City (12%) |
| **Jian Dang Ba Huang** | Massive horizontal energy slash, travels 64 blocks, ignores obstacles | 150% | Ancient City (10%), End City (12%) |
| **Geng Jin Jian Jue** | 1–100 golden phantom blades (count tied to your Refine level), ignores armor | 160% | Ancient City (8%), Bastion Treasure (10%) |
| **Qi Sha Jian Jie** | Domain deals 7 sequential hits, cycling through 7 damage types; range scales with your KillCount | 100% × 7 | Ancient City (5%), Nether Fortress (6%) |

### Damage Calculation

All SAs share the same base damage formula:

```
Base Damage = max(10.0, Blade Base Damage + Refine×0.5 + Strength Enchantment Level×2.0 + Player's Attack)
```

This base is then multiplied by each SA's **damage multiplier**, ensuring that skills stay relevant alongside your gear progression.

---

## 🎯 Survival Mode Acquisition

You can find special **Blade Cores** (耀魂宝珠) with embedded SAs in chests from the following structures:

| Structure | Available SAs | Chance |
|:---|:---|:---|
| Ancient City | Wan Jian Gui Zong, Senbonzakura, Frost Domain, Zhan Tian Ba Jian Shu, Jian Dang Ba Huang, Geng Jin Jian Jue, Qi Sha Jian Jie | 10% / 20% / 15% / 10% / 10% / 8% / 5% |
| End Ship / End City | Rift Slash, Zhan Tian Ba Jian Shu, Jian Dang Ba Huang | 25% / 12% / 12% |
| Bastion Remnant (Treasure) | Inferno, Geng Jin Jian Jue | 15% / 10% |
| Nether Fortress | Wan Jian Gui Zong, Qi Sha Jian Jie | 8% / 6% |

### How to Apply

1. Obtain a Blade Core with the desired SA.
2. Place the Core and any SlashBlade onto a Blade Stand (刀挂台).
3. Left-click to transfer the SA to the blade.
4. Hold the blade, then right-click and hold to unleash the skill.

---

## 🛠️ Test Commands (Creative Mode)

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

---

## 📦 Dependencies

| Dependency | Version |
| :--- | :--- |
| Minecraft | 1.20.1 |
| SlashBlade: Resharped | 1.5.0+ |

---

## ⚙️ Developer Info

### Building

Use Gradle to build:

```bash
gradlew build
```

The output JAR will be at `build/libs/slashexpansion-1.2.jar`.

### Importing into an IDE

- **IntelliJ IDEA**:
  ```bash
  gradlew idea
  ```
- **Eclipse**:
  ```bash
  gradlew eclipse
  ```

### Project Structure

```text
src/main/
├── java/com/lisheng/slashexpansion/
│   ├── SlashExpansion.java                # Main class
│   ├── util/
│   │   └── DamageCalculator.java          # Dynamic damage logic
│   ├── entity/                            # Custom entities
│   ├── specialattack/                     # 9 SA implementations
│   ├── registry/                          # Registries
│   ├── loot/
│   │   └── LootInjector.java              # Loot table injection
│   └── client/
│       └── ClientEvents.java              # Client-side rendering
└── resources/
    ├── pack.mcmeta
    ├── META-INF/mods.toml
    └── assets/slashexpansion/
        └── lang/                          # Localization files
```