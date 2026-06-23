# SlashExpansion

**SlashExpansion** 是一个 [拔刀剑：重锋 (SlashBlade: Resharped)](https://www.curseforge.com/minecraft/mc-mods/slashblade-resharped) 的附属模组，为 Minecraft 1.20.1 添加了 **9 种全新的 SA（特殊攻击）**。

模组**不添加新刀剑**，专注于丰富拔刀剑的技能体系。所有 SA 的伤害基于玩家当前持有的刀动态计算，随装备成长而提升。

---

## ✨ 新增 SA 一览

| SA 名称 | 效果 | 伤害系数 | 获取方式 |
|:---|:---|:---|:---|
| **万剑归宗** | 召唤 10~256 把幻影剑从天而降，剑数随经验等级增加 | 80% | 远古城市 (10%)、下界要塞 (8%) |
| **千本樱** | 樱花环绕剑阵，每个目标 4~6 把剑，带穿透和樱花粒子 | 120% | 远古城市 (20%) |
| **裂空斩** | 冲刺突进 + 8 方向剑气 | 180% | 末地船/城 (25%) |
| **冰霜领域** | 20 格范围冻结 + 迟缓 X + 虚弱 II +挖掘疲劳 IV | 80% | 远古城市 (15%) |
| **业火焚城** | 地面火焰爆发 + 8 秒灼烧，下界生物 +50% | 100% + 灼烧 | 堡垒宝藏 (15%) |
| **斩天拔剑术** | 竖直超大型剑气，飞行 64 格，无视阻挡 | 150% | 远古城市 (10%)、末地城 (12%) |
| **剑荡八荒** | 横向超大型剑气，飞行 64 格，无视阻挡 | 150% | 远古城市 (10%)、末地城 (12%) |
| **庚金剑诀** | 1~100 把金色幻影剑（数量与锻造数相关），无视护甲 | 160% | 远古城市 (8%)、堡垒宝藏 (10%) |
| **七杀剑界** | 领域持续 7 次伤害，7 种伤害类型循环，范围与杀敌数相关 | 100% × 7 | 远古城市 (5%)、下界要塞 (6%) |

### 伤害计算规则

所有 SA 统一使用以下公式计算基础伤害：

```
基础伤害 = max(10.0, 刀面板 + 评分×0.5 + 力量等级x2.0 + 玩家基础攻击)
```

然后乘以各自的 **伤害系数**，确保技能强度与玩家装备同步成长。

---

## 🎯 生存模式获取

在以下结构宝箱中，有概率开出带有对应 SA 的耀魂宝珠：

| 结构 | 可获得的 SA | 概率 |
|:---|:---|:---|
| 远古城市 | 万剑归宗、千本樱、冰霜领域、斩天拔剑术、剑荡八荒、庚金剑诀、七杀剑界 | 10% / 20% / 15% / 10% / 10% / 8% / 5% |
| 末地船/末地城 | 裂空斩、斩天拔剑术、剑荡八荒 | 25% / 12% / 12% |
| 堡垒遗迹（宝藏区） | 业火焚城、庚金剑诀 | 15% / 10% |
| 下界要塞 | 万剑归宗、七杀剑界 | 8% / 6% |

### 使用方式

1. 获得带有 SA 的耀魂宝珠。
2. 将宝珠与任意拔刀剑放在刀挂台上。
3. 左键点击，SA 即转移至刀上。
4. 手持该刀，右键蓄力释放技能。

---

## 🛠️ 测试指令（创造模式）

```text
/give @p slashblade:slashblade{SpecialAttackType:"slashexpansion:wan_jian_gui_zong"}
将 wan_jian_gui_zong 替换为：

senbonzakura — 千本樱

rift_slash — 裂空斩

frost_domain — 冰霜领域

inferno — 业火焚城

zhan_tian_ba_jian_shu — 斩天拔剑术

jian_dang_ba_huang — 剑荡八荒

geng_jin_jian_jue — 庚金剑诀

qi_sha_jian_jie — 七杀剑界

📦 依赖
依赖	版本
Minecraft	1.20.1
拔刀剑：重锋	1.5.0+

⚙️ 开发者信息
构建
bash
gradlew build
构建产物位于 build/libs/slashexpansion-1.2.jar。

导入 IDE
IntelliJ IDEA：gradlew idea

Eclipse：gradlew eclipse

项目结构
text
src/main/
├── java/com/lisheng/slashexpansion/
│   ├── SlashExpansion.java                # 主类
│   ├── util/ DamageCalculator.java        # 动态伤害计算
│   ├── entity/                            # 自定义实体
│   ├── specialattack/                     # 9 个 SA 核心逻辑
│   ├── registry/                          # 注册类
│   ├── loot/ LootInjector.java            # 战利品注入
│   └── client/ ClientEvents.java          # 客户端渲染
└── resources/
    ├── pack.mcmeta
    ├── META-INF/mods.toml
    └── assets/slashexpansion/
        └── lang/                          # 语言文件