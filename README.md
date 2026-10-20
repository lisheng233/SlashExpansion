<div align="center">
  <a href="README_EN.md">English</a> | <b>中文</b>
</div>

---

# SlashExpansion

**SlashExpansion** 是一个 [拔刀剑：重锋 (SlashBlade: Resharped)](https://www.curseforge.com/minecraft/mc-mods/slashblade-resharped) 的附属模组，为 Minecraft 1.20.1 添加了 **12 种全新的 SA（特殊攻击）** 和 **12 种强大的 SE（特殊效果）**。

模组**不添加新刀剑**，专注于丰富拔刀剑的技能体系。所有 SA 的伤害基于玩家当前持有的刀动态计算，随装备成长而提升。

---

## ✨ 新增 SA 一览

| SA 名称              | 效果                                                  | 伤害系数    | 获取方式                      |
| :------------------- | :---------------------------------------------------- | :---------- | :---------------------------- |
| **万剑归宗**   | 召唤 10~256 把幻影剑从天而降，剑数随经验等级增加      | 80%         | 远古城市 (10%)、下界要塞 (8%) |
| **千本樱**     | 樱花环绕剑阵，每个目标 4~6 把剑，带穿透和樱花粒子     | 120%        | 远古城市 (20%)                |
| **裂空斩**     | 冲刺突进 + 8 方向剑气                                 | 180%        | 末地船/城 (25%)               |
| **冰霜领域**   | 20 格范围冻结 + 迟缓 X + 虚弱 II + 挖掘疲劳 IV        | 80%         | 远古城市 (15%)                |
| **业火焚城**   | 地面火焰爆发 + 12 秒灼烧，下界生物 +50%               | 100% + 灼烧 | 堡垒宝藏 (15%)                |
| **斩天拔剑术** | 竖直超大型剑气，飞行 64 格，无视阻挡                  | 150%        | 远古城市 (10%)、末地城 (12%)  |
| **剑荡八荒**   | 横向超大型剑气，飞行 64 格，无视阻挡                  | 150%        | 远古城市 (10%)、末地城 (12%)  |
| **庚金剑诀**   | 1~100 把金色幻影剑（数量与锻造数相关），无视护甲      | 160%        | 远古城市 (8%)、堡垒宝藏 (10%) |
| **七杀剑界**   | 领域持续 7 次伤害，7 种伤害类型循环，范围与杀敌数相关 | 100% × 7   | 远古城市 (5%)、下界要塞 (6%)  |
| **血月斩**     | 消耗自身 30% 当前生命，对前方 8 格造成「基础伤害 + 消耗生命兑换的伤害（随锻造数提升、无视护甲）」 | 基础 + 兑换 | 下界要塞 (10%) |
| **雷神之怒**   | 对 20 格内随机敌人降下 7 道闪电，每道 120% 伤害并附加 3 秒麻痹 | 120% × 7 | 堡垒宝藏 (12%) |
| **不动明王**   | 3 秒内免疫所有伤害但无法移动，结束后对周围 10 格造成 200% 伤害并击退 | 200% | 堡垒宝藏 (8%) |

> 注：**雷神之怒** 的闪电默认仅作视觉表现（`[sa.thors_wrath] visualOnly = true`），不会产生原版雷击伤害与火焰。

### 伤害计算规则

所有 SA 统一使用以下公式计算基础伤害：

```
基础伤害 = max(10.0, 刀面板 + 锻造数×0.5 + 力量附魔等级×2.0 + 玩家基础攻击)
```

然后乘以各自的 **伤害系数**，确保技能强度与玩家装备同步成长。

> 公式中的伤害下限、锻造加成、力量加成，以及每个 SA 的伤害系数，都可以在配置文件中调整。

---

## ✨ 新增 SE 一览

| SE 名称              | 效果                                                                                                                                         |
| :------------------- | :------------------------------------------------------------------------------------------------------------------------------------------- |
| **风神的伟力** | 跳跃提升 III、免疫摔落伤害、移速+100%、飞行速度+150%（仅主手生效）                                                                           |
| **寒霜的辉光** | 免疫霜冻伤害、攻击附加霜冻伤害                                                                                                               |
| **超越的意志** | 附魔等级 +5、伤害 ×1.5、暴击 +25%                                                                                                           |
| **深渊的低语** | 低等级（≤99）：自身反胃I+饥饿I+霉运V+凋零II；攻击敌人施加凋零 X、失明I、虚弱 V、缓慢 V、挖掘疲劳 III  ； 高等级（≥100）：自身霉运III+凋零I |
| 虚空之力             | 攻击附加虚空伤害，给予目标负面效果 ；持刀时免疫虚空类型伤害和负面效果                                                                        |
| 管理员之力           | (需要权限4级)持刀时免疫一切伤害、负面效果与击退，可飞行、自动回满状态并清除负面效果；近战攻击直接秒杀目标（默认 3 格溅射，可配置）。伤害/移除/踢出由 Mixin 在最底层拦截 |
| **烈焰之心**   | 攻击点燃敌人 5 秒；免疫火焰伤害；在下界时移速 +20%                                                                                          |
| **虚空行者**   | 站在方块边缘时自动悬浮（不会掉落）；末地移速 +30%；攻击末影人伤害 ×2                                                                        |
| **千锤百炼**   | 耐久消耗减半；击杀回复 1 点耐久；每 10 点杀敌消耗 100 耀魂 +1 锻造（最多 100 次） |
| **死神之约**   | 生命值低于 30% 时攻击力 +50%，但每秒流失 1 点生命值（不会致死）                                                                             |
| **精灵祝福**   | 白天移速 +20%，夜晚攻击力 +30%；免疫中毒；箭矢伤害 +50%                                                                                     |
| **龙魂共鸣**   | 击杀末影龙后永久 +10% 攻击力；攻击时 5% 概率触发龙息范围伤害                                                                                |

---

## 🎯 生存模式获取

> 下表中的结构、SA 与概率都可以在配置文件 `[loot]` 中修改。

### SA 获取：结构宝箱

在以下结构宝箱中，有概率开出带有对应 SA 的耀魂宝珠：

| 结构               | 可获得的 SA                                                          | 概率                                  |
| :----------------- | :------------------------------------------------------------------- | :------------------------------------ |
| 远古城市           | 万剑归宗、千本樱、冰霜领域、斩天拔剑术、剑荡八荒、庚金剑诀、七杀剑界 | 10% / 20% / 15% / 10% / 10% / 8% / 5% |
| 末地船/末地城      | 裂空斩、斩天拔剑术、剑荡八荒                                         | 25% / 12% / 12%                       |
| 堡垒遗迹（宝藏区） | 业火焚城、庚金剑诀、雷神之怒、不动明王                               | 15% / 10% / 12% / 8%                  |
| 下界要塞           | 万剑归宗、七杀剑界、血月斩                                           | 8% / 6% / 10%                         |

### SE 获取：合成

所有 SE 通过合成获得
（通过JEI/配方书查看耀魂结晶的用途；配方可在配置 `[recipe]` 中自由修改）

> 注：**龙魂共鸣** 的 3×3 合成格最多容纳 9 格，因此材料为 龙蛋 ×1 + 龙息 ×7 + 耀魂结晶 ×1。

### 使用方式

1. 获得带有 SA 的耀魂宝珠或带有 SE 的耀魂结晶。
2. 将宝珠/结晶与任意拔刀剑放在刀挂台上。
3. 左键点击，SA/SE 即转移至刀上。
4. SA 需右键蓄力释放；SE 为被动效果，持有即生效。

---

## ⚙️ 配置文件

所有可调数值、**SA 的结构宝箱获取**以及 **SE 的合成配方**都集中在
**`config/slashexpansion-common.toml`**（首次启动时自动生成）。

修改数值类配置后重启游戏生效；修改 `[loot]` / `[recipe]` 后重启服务器或执行 `/reload` 即可生效。

| 分组                     | 内容                                                                 |
| :----------------------- | :------------------------------------------------------------------- |
| `[damage]`               | 基础伤害公式：伤害下限、锻造加成、力量附魔加成                       |
| `[sa.<技能>]`            | 每个 SA 的伤害系数、索敌半径、剑数、生效半径、效果时长与等级等       |
| `[entities.giant_slash]` | 巨型剑气（斩天拔剑术 / 剑荡八荒）的飞行距离、速度、命中判定等        |
| `[se.<效果>]`            | 每个 SE 的持续时间、效果等级、倍率、检查间隔等                       |
| `[loot]`                 | SA 的结构宝箱获取方式                                                |
| `[recipe]`               | SE 的合成配方                                                        |

### SA 获取（`[loot]`）

```toml
[loot]
enabled = true
item = "slashblade:proudsoul_sphere"     # 掉落的物品
# 每条格式："战利品表ID|SA ID|概率（0~1）"
entries = [
  "minecraft:chests/ancient_city|slashexpansion:wan_jian_gui_zong|0.10",
  "minecraft:chests/end_city_treasure|slashexpansion:rift_slash|0.25"
]
```

可以增删条目、更换结构战利品表、修改技能与概率，也可以把同一个 SA 放到多个结构中。

### SE 合成（`[recipe]`）

```toml
[recipe]
overrideJsonRecipes = true               # 是否用本配置覆盖内置 JSON 配方

[recipe.frost_glow]                      # 每个 SE 一段，共 12 段
enabled = true
shapeless = false                        # true 时按 ingredients 无序合成
resultItem = "slashblade:proudsoul_crystal"
pattern = ["CIC", "BWP", "CAC"]          # 1~3 行、每行 1~3 个字符，空格表示留空
keys = ["C=slashblade:proudsoul_crystal", "I=minecraft:blue_ice"]
ingredients = []                         # shapeless = true 时读取此列表
```

- 配方 ID 固定为 `slashexpansion:<SE ID>_proudsoul_crystal`，因此配置修改后可直接替换内置配方。
- 把 `enabled` 设为 `false` 会移除该配方；把 `overrideJsonRecipes` 设为 `false` 则完全使用模组内置 JSON 配方（可被数据包覆盖）。
- 产物会自动附加 `SpecialEffectType = slashexpansion:<SE ID>` 标签。

---

## 🛠️ 测试指令（创造模式）

### SA 测试

```
/give @p slashblade:slashblade{SpecialAttackType:"slashexpansion:wan_jian_gui_zong"}
```

将 `wan_jian_gui_zong` 替换为：

- `senbonzakura` — 千本樱
- `rift_slash` — 裂空斩
- `frost_domain` — 冰霜领域
- `inferno` — 业火焚城
- `zhan_tian_ba_jian_shu` — 斩天拔剑术
- `jian_dang_ba_huang` — 剑荡八荒
- `geng_jin_jian_jue` — 庚金剑诀
- `qi_sha_jian_jie` — 七杀剑界
- `blood_moon_slash` — 血月斩
- `thors_wrath` — 雷神之怒
- `immovable_king` — 不动明王

### SE 测试

```
/give @p slashblade:proudsoul_crystal{SpecialEffectType:"slashexpansion:wind_god_power"}
```

将 `wind_god_power` 替换为：

- `frost_glow` — 寒霜的辉光
- `transcendent_will` — 超越的意志
- `abyss_whisper` — 深渊的低语
- `void_power` — 虚空之力
- `admin` — 管理员之力
- `flame_heart` — 烈焰之心
- `void_walker` — 虚空行者
- `tempered_soul` — 千锤百炼
- `grim_reapers_pact` — 死神之约
- `elven_blessing` — 精灵祝福
- `dragon_soul_resonance` — 龙魂共鸣

---

## 📦 依赖

| 依赖         | 版本   |
| :----------- | :----- |
| Minecraft    | 1.20.1 |
| 拔刀剑：重锋 | 1.5.0+ |

> 开发编译依赖为 SlashBlade: Resharped **1.9.65**（Modrinth Maven），运行时支持 1.5.0+。

---

## ⚙️ 开发者信息

### 构建

```bash
gradlew build
```

构建产物位于 `build/libs/slashexpansion-1.9.4.jar`。

### 导入 IDE

- **IntelliJ IDEA**：`gradlew idea`
- **Eclipse**：`gradlew eclipse`

### 项目结构

```
src/main/
├── java/com/lisheng/slashexpansion/
│   ├── SlashExpansion.java                # 主类
│   ├── config/
│   │   ├── SlashExpansionConfig.java      # 配置文件定义（所有可调数值）
│   │   └── ConfigRecipeInjector.java      # 按配置生成并注入 SE 配方
│   ├── util/ DamageCalculator.java        # 动态伤害计算
│   ├── entity/                            # 自定义实体
│   ├── specialattack/                     # 9 个 SA 核心逻辑
│   ├── specialeffect/                     # 6 个 SE 核心逻辑
│   ├── mixin/                             # Mixin 拦截（生命值/移除/踢出保护）
│   ├── registry/                          # 注册类
│   ├── loot/ LootInjector.java            # 按配置注入战利品
│   └── client/ ClientEvents.java          # 客户端渲染
└── resources/
    ├── slashexpansion.png
    ├── pack.mcmeta
    ├── META-INF/mods.toml
    ├── data/slashexpansion/recipes/       # 内置默认配方（可被配置覆盖）
    └── assets/slashexpansion/
        └── lang/
            ├── zh_cn.json                 # 中文语言文件
            └── en_us.json                 # 英文语言文件
```
