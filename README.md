# SlashExpansion

**SlashExpansion** 是一个 [拔刀剑：重锋 (SlashBlade: Resharped)](https://www.curseforge.com/minecraft/mc-mods/slashblade-resharped) 的附属模组，为 Minecraft 1.20.1 添加了**5 种全新 SA（特殊攻击）**。

模组**不添加新刀剑**，专注于丰富拔刀剑的技能体系。所有 SA 的伤害**基于玩家当前持有的刀动态计算**，随装备成长而提升。

---

## ✨ 新增 SA

| SA 名称 | 效果 | 伤害系数 | 获取方式 |
|:---|:---|:---|:---|
| **万剑归宗** | 从天空召唤 10~256 把幻影剑攻击周围敌人，剑数随经验等级增加 | 80% | 远古城市 (30%)、下界要塞 (8%) |
| **千本樱** | 樱花环绕剑阵，每个目标生成 4~6 把剑，带穿透效果和樱花粒子 | 120% | 远古城市 (20%) |
| **裂空斩** | 瞬移至前方 20 格，向 8 个方向释放剑气，附带击飞 | 180% | 末地船/末地城 (25%) |
| **冰霜领域** | 冻结 15 格内敌人，造成迟缓 X、虚弱 II 和挖掘疲劳IV效果 | 80% (强控补偿) | 远古城市 (15%) |
| **业火焚城** | 地面火焰爆发，点燃敌人 12 秒，对下界生物额外 50% 伤害 | 100% + 灼烧 | 堡垒遗迹宝藏区 (15%) |

### 伤害计算规则

所有 SA 统一使用以下公式计算基础伤害：

```
基础伤害 = max(10.0, 刀面板 + 评分×0.5 + 力量等级x2.0 + 玩家基础攻击)
```

然后乘以各自的 **伤害系数**，确保技能强度与玩家装备同步成长。

---

## 🎯 生存模式获取

在以下**结构宝箱**中，有概率开出带有对应 SA 的**耀魂宝珠**（拔刀剑原版 `proudsoul_sphere`）：

| 结构 | 可获得的 SA | 概率 |
|:---|:---|:---|
| 远古城市 | 万剑归宗、千本樱、冰霜领域 | 30% / 20% / 15% |
| 末地船 / 末地城 | 裂空斩 | 25% |
| 堡垒遗迹（宝藏区） | 业火焚城 | 15% |
| 下界要塞 | 万剑归宗 | 8% |

### 使用方式

1. 获得带有 SA 的耀魂宝珠。
2. 将宝珠与任意拔刀剑一起放在**刀挂台**上。
3. 左键点击，SA 即转移至刀上。
4. 手持该刀，**右键蓄力**释放技能。

---

## 🛠️ 测试指令（创造模式）

```text
/give @p slashblade:slashblade{SpecialAttackType:"slashexpansion:wan_jian_gui_zong"}
```

将 `wan_jian_gui_zong` 替换为：
- `senbonzakura` — 千本樱
- `rift_slash` — 裂空斩
- `frost_domain` — 冰霜领域
- `inferno` — 业火焚城

---

## ⚙️ 开发者信息

### 构建

```bash
.\gradlew build
```

构建产物位于 `build/libs/slashexpansion-1.0.jar`。

### 导入 IDE

- IntelliJ IDEA：`gradlew idea`
- Eclipse：`gradlew eclipse`

### 项目结构

```
src/main/
├── java/com/lisheng/slashexpansion/
│   ├── SlashExpansion.java                # 主类
│   ├── util/
│   │   └── DamageCalculator.java          # 动态伤害计算
│   ├── entity/                            # 自定义召唤剑实体
│   ├── specialattack/                     # 5 个 SA 的核心逻辑
│   ├── registry/                          # ComboState / SlashArts 注册
│   ├── loot/                              # 战利品注入
│   └── client/                            # 客户端渲染
└── resources/
    ├── pack.mcmeta
    ├── META-INF/mods.toml
    └── assets/slashexpansion/
        ├── slashexpansion.png
        └── lang/zh_cn.json                # 多键名语言文件
```

---

## 📜 许可证

本项目采用 **All Rights Reserved** 许可证，详见 [LICENSE](LICENSE)。

未经授权，禁止转载、修改或用于商业用途。

---

**欢迎反馈 Issue 和 Star！** 🎉