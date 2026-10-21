<div align="center">
  <a href="README_EN.md">English</a> | <b>中文</b>
</div>

---

# SlashExpansion

> ## ⚠️ 你正在看 `1.21.1-neoforge` 分支
>
> 这是 **Minecraft 1.21.1 / NeoForge 21.1.249** 的移植分支，移植仍在进行中：
>
> | 阶段 | 内容 | 状态 |
> | :--- | :--- | :--- |
> | 一 | 构建链跑通（Gradle 9.2.1 + JDK 21 + NeoForge 21.1.249 + SlashBlade 2.1.9） | ✅ 已完成 |
> | 一 | 2 个 SA（裂空斩、冰霜领域）+ 2 个 SE（烈焰之心、千锤百炼）的 API 验证 | ✅ 已完成 |
> | 一 | 开发服务器启动验证 | ✅ 已完成 |
> | 二 | **12 个 SA 全量注册**（ComboState ×24 + SlashArts ×12） | ✅ 已完成 |
> | 二 | **12 个 SE 全量注册**（含管理员之力） | ✅ 已完成 |
> | 二 | 4 个自定义实体（幻影剑、千本樱剑、巨型剑气、七杀领域） | ✅ 已完成 |
> | 二 | 客户端渲染器 / 粒子 provider / 屏幕特效 / 网络通道 | ✅ 已完成 |
> | 二 | Mixin：移除与生命值保护 | ✅ 已完成 |
> | 二 | Mixin：防踢保护（注入 `ServerCommonPacketListenerImpl#disconnect`） | ✅ 已完成 |
> | 二 | 战利品注入（Global Loot Modifier，配置语义与 1.20.1 一致） | ✅ 已完成 |
> | 二 | 数据包内容：6 条 SE 配方 + 伤害类型（1.21 格式） | ✅ 已完成 |
> | 二 | SE 配方可配置：1.21 直接用数据包覆盖 data/slashexpansion/recipe/*.json（比代码注入更灵活，无需移植 ConfigRecipeInjector） | ✅ 已改为数据包方案 |
>
> 当前服务端验证输出：`12 SA / 12 SE / 4 实体 / 2 粒子 + 保护 Mixin` 后接 `Done`。
>
> **1.20.1 / Forge 47 版是另一个独立项目**：`D:\programs\coding\java\blade-1.20.1`
> （独立 git 仓库、`main` 分支、独立 Gradle 构建），两个项目互不影响。
>
> ### 构建
>
> ```bash
> # 需要 JDK 21（本仓库用 Gradle toolchain，配好 org.gradle.java.installations.paths 即可）
> gradlew build
> ```
>
> - 依赖解析默认走 BMCLAPI 镜像（官方 `maven.neoforged.net` 在部分网络下被 DNS 污染）：
>   `-PneoforgeMirror=` 关闭镜像，`-PneoforgeMirror=URL` 换自定义镜像。
> - **必须**保留对 `net.neoforged:minecraft-dependencies` 的排除：NeoForm 1.21.1 的 pom 声明了
>   这个构件，但它在官方仓库和所有镜像里都不存在（404），不排除会直接构建失败。
>   反编译与编译本身并不需要它。
> - 若官方源需要走代理，在 `~/.gradle/gradle.properties` 里加
>   `systemProp.https.proxyHost/Port`，或给 Gradle 传 `-Dorg.gradle.jvmargs="-Dhttps.proxyHost=..."`
>   （注意 `systemProp.*` 对 ModDevGradle 拉取 NeoForm 运行时不一定生效，实测用 JVM 参数更可靠）。

---

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

> 注意：**裂空斩** 的每把剑气都按 180% 独立结算，默认 8 把剑气全部命中时总伤害为 `8 × 180% × 基础伤害`；并且剑气沿用拔刀剑的魔法伤害类型，不受护甲减免。需要削强度就调 `[sa.rift_slash]` 的 `damageMultiplier` 或 `bladeCount`。

### 动画特效

每个 SA 都有一套**分阶段的时间轴动画**（蓄势 → 斩击 → 冲击 → 余韵），而不是施放瞬间一次性炸开粒子：

| SA             | 动画表现                                                                                          |
| :------------- | :------------------------------------------------------------------------------------------------ |
| **万剑归宗**   | 脚下金色召唤法阵展开 → 头顶剑气汇聚 → 飞剑逐把落下，每把带落点光柱与落地冲击环                      |
| **千本樱**     | 粉色樱花法阵 → 花瓣龙卷上升 → 每个目标上方花瓣汇聚后爆散，漫天樱瓣飘落                              |
| **裂空斩**     | 冲刺残影 → 面前撕开暗紫色空间裂缝（白色光芯）→ 冲击环扩散 + 青色闪光与镜头震动                      |
| **冰霜领域**   | 冰蓝色法阵铺开 → 一圈 12 根冰棱拔地而起 → 极寒涡流与飘雪，命中目标身上爆开冰晶                      |
| **业火焚城**   | 火焰圆环自脚下扩散 → 火舌沿螺旋上升 → 业火光柱冲天 + 熔岩火星，目标处火焰爆燃                       |
| **斩天拔剑术** | 高空垂下蓄势光柱 → 天空被撕开一道横贯的金色裂口 → 竖劈刀光扫过 + 地面冲击环（最强闪屏与震屏）        |
| **剑荡八荒**   | 双层宽幅金色刀光横扫 → 面向冲击环 → 地面震荡环                                                      |
| **庚金剑诀**   | 双层反向旋转的庚金法阵 → 金光向中心汇聚 → 数十道金色剑光自天而降                                    |
| **七杀剑界**   | 紫色七杀法阵 + 边界旋转光幕 → 七个方位的杀剑光柱 → 每次结算冲击环与魂光爆发                          |
| **血月斩**     | 头顶血月升起并碎裂 → 血月刀光横扫前方扇形 → 血雾爆散 + 深红闪光与暗角                               |
| **雷神之怒**   | 雷云压顶 → 预兆电弧 → 每道落雷都是锯齿闪电 + 地面冲击环，末尾在目标之间跳跃连锁电弧                  |
| **不动明王**   | 金色曼陀罗展开 → 八方法柱 → 持续护体金光与周期法阵脉冲 → 结束时金身炸裂 + 冲击波                     |

实现上分三层：

- **自定义粒子**：本模组自带两张程序化绘制的贴图（`glow.png` 柔软光晕、`rune.png` 曼陀罗法阵），配合**加法混合、不写深度**的粒子渲染类型，重叠的光点会自然叠亮成光晕。
- **服务端时间轴**：`vfx/VfxDirector` 每刻推进一次特效任务，`vfx/Vfx` 提供圆环、球壳、光柱、螺旋、汇聚、刀光扫过、折线闪电、残影等绘制原语，`vfx/SaVfx` 负责把原语编排成每个 SA 的演出。
- **客户端屏幕表现**：命中闪光、四周暗角、镜头震动，通过 `slashexpansion:main` 通道下发，半径内所有玩家都能看到。

> 特效强度可以在 `[vfx]` 分组里整体调节，性能吃紧时把 `particleScale` 调小即可；关闭特效不影响任何伤害结算。

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
| `[vfx]`                  | 动画特效：总开关、粒子数量倍率、特效数量上限、屏幕闪光/暗角、镜头震动 |

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

### 特效（`[vfx]`）

```toml
[vfx]
enabled = true               # 特效总开关（关掉后仍正常结算伤害）
particleScale = 1.0          # 粒子数量倍率，0.0 ~ 3.0，性能吃紧就调小
maxConcurrentEffects = 96    # 同时播放的特效数量上限
particleBudgetPerTick = 320 # 每 tick 粒子发射上限（兜底防卡顿）
screenEffects = true         # 命中闪光 / 四周暗角
screenShake = true           # 镜头震动
screenEffectScale = 1.0      # 屏幕效果强度倍率，0.0 ~ 2.0
```

这一组是**热读取**的：改完回到游戏里立刻生效，不需要重启。

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

### 特效预览

不想打架只想看动画时，可以直接播放任意 SA 的演出（需要权限等级 2）：

```
/slashexpansion vfx blood_moon_slash
/slashexpansion vfx list
```

可用名字：`wan_jian_gui_zong`、`senbonzakura`、`rift_slash`、`frost_domain`、`inferno`、
`zhan_tian_ba_jian_shu`、`jian_dang_ba_huang`、`geng_jin_jian_jue`、`qi_sha_jian_jie`、
`blood_moon_slash`、`thors_wrath`、`immovable_king_cast`、`immovable_king_release`。

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

构建产物位于 `build/libs/slashexpansion-1.10.1.jar`。

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
│   ├── specialattack/                     # 12 个 SA 的伤害/判定逻辑
│   ├── specialeffect/                     # 12 个 SE 核心逻辑
│   ├── vfx/
│   │   ├── Vfx.java                       # 特效绘制原语（环/柱/螺旋/刀光/闪电…）
│   │   ├── VfxDirector.java               # 服务端特效时间轴调度
│   │   └── SaVfx.java                     # 每个 SA 的动画编排
│   ├── particle/                          # 自定义粒子（光晕 / 地面法阵）
│   ├── net/                               # 网络通道（下发屏幕特效）
│   ├── command/ VfxCommand.java           # /slashexpansion vfx 预览命令
│   ├── mixin/                             # Mixin 拦截（生命值/移除/踢出保护）
│   ├── registry/                          # 注册类
│   ├── loot/ LootInjector.java            # 按配置注入战利品
│   └── client/                            # 客户端渲染与屏幕特效
└── resources/
    ├── slashexpansion.png
    ├── pack.mcmeta
    ├── META-INF/mods.toml
    ├── data/slashexpansion/recipes/       # 内置默认配方（可被配置覆盖）
    └── assets/slashexpansion/
        ├── particles/                     # 粒子描述（glow / rune）
        ├── textures/particle/             # 粒子贴图（程序化绘制）
        └── lang/
            ├── zh_cn.json                 # 中文语言文件
            └── en_us.json                 # 英文语言文件
```
