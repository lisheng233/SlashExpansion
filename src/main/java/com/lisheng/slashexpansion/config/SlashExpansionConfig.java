package com.lisheng.slashexpansion.config;

import net.minecraftforge.common.ForgeConfigSpec;

import java.util.List;

/**
 * SlashExpansion 配置文件（config/slashexpansion-common.toml）。
 *
 * <p>所有可调数值都集中在这里：伤害公式、各个 SA 的参数、实体参数、各个 SE 的参数、
 * SA 的结构宝箱战利品以及 SE 的合成配方。</p>
 *
 * <p>注意：本类只保存配置项的定义与读取方法，真正的取值发生在运行期
 * （战斗、实体 tick、战利品表加载、数据重载），因此不会在配置加载完成前被访问。</p>
 */
public final class SlashExpansionConfig {

    private static final ForgeConfigSpec.Builder B = new ForgeConfigSpec.Builder();

    public static final ForgeConfigSpec SPEC;

    static {
        defineDamage();
        defineSa();
        defineEntities();
        defineSe();
        defineLoot();
        defineRecipes();
        SPEC = B.build();
    }

    private SlashExpansionConfig() {
    }

    // ==================================================================
    // 1. 通用伤害公式
    // ==================================================================

    public static final class Damage {
        private static ForgeConfigSpec.DoubleValue minDamage;
        private static ForgeConfigSpec.DoubleValue refineBonus;
        private static ForgeConfigSpec.DoubleValue powerEnchantBonus;

        public static float minDamage() {
            return (float) (double) minDamage.get();
        }

        public static float refineBonus() {
            return (float) (double) refineBonus.get();
        }

        public static float powerEnchantBonus() {
            return (float) (double) powerEnchantBonus.get();
        }
    }

    private static void defineDamage() {
        B.comment("所有 SA 的基础伤害公式：",
                        "  基础伤害 = max(minDamage, 刀面板 + 锻造数 × refineBonus + 力量附魔等级 × powerEnchantBonus + 玩家攻击力)",
                        "SA 的最终伤害 = 基础伤害 × 该 SA 的 damageMultiplier")
                .push("damage");

        Damage.minDamage = B.comment("伤害下限（计算结果低于该值时按该值计算）")
                .defineInRange("minDamage", 10.0D, 0.0D, 100000.0D);
        Damage.refineBonus = B.comment("每级锻造数提供的额外伤害")
                .defineInRange("refineBonus", 0.5D, 0.0D, 1000.0D);
        Damage.powerEnchantBonus = B.comment("每级“力量”附魔提供的额外伤害")
                .defineInRange("powerEnchantBonus", 2.0D, 0.0D, 1000.0D);

        B.pop();
    }

    // ==================================================================
    // 2. 各个 SA（特殊攻击）
    // ==================================================================

    public static final class Sa {

        public static final class WanJianGuiZong {
            private static ForgeConfigSpec.DoubleValue damageMultiplier;
            private static ForgeConfigSpec.IntValue searchRange;
            private static ForgeConfigSpec.IntValue minSwords;
            private static ForgeConfigSpec.IntValue maxSwords;
            private static ForgeConfigSpec.DoubleValue projectileSpeed;

            public static float damageMultiplier() {
                return (float) (double) damageMultiplier.get();
            }

            public static int searchRange() {
                return searchRange.get();
            }

            public static int minSwords() {
                return minSwords.get();
            }

            public static int maxSwords() {
                return maxSwords.get();
            }

            public static float projectileSpeed() {
                return (float) (double) projectileSpeed.get();
            }
        }

        public static final class Senbonzakura {
            private static ForgeConfigSpec.DoubleValue damageMultiplier;
            private static ForgeConfigSpec.IntValue searchRange;
            private static ForgeConfigSpec.IntValue minSwordsPerTarget;
            private static ForgeConfigSpec.IntValue maxSwordsPerTarget;
            private static ForgeConfigSpec.DoubleValue projectileSpeed;

            public static float damageMultiplier() {
                return (float) (double) damageMultiplier.get();
            }

            public static int searchRange() {
                return searchRange.get();
            }

            public static int minSwordsPerTarget() {
                return minSwordsPerTarget.get();
            }

            public static int maxSwordsPerTarget() {
                return maxSwordsPerTarget.get();
            }

            public static float projectileSpeed() {
                return (float) (double) projectileSpeed.get();
            }
        }

        public static final class RiftSlash {
            private static ForgeConfigSpec.DoubleValue damageMultiplier;
            private static ForgeConfigSpec.DoubleValue dashHorizontalSpeed;
            private static ForgeConfigSpec.DoubleValue dashVerticalSpeed;
            private static ForgeConfigSpec.IntValue bladeCount;
            private static ForgeConfigSpec.DoubleValue projectileSpeed;

            public static float damageMultiplier() {
                return (float) (double) damageMultiplier.get();
            }

            public static double dashHorizontalSpeed() {
                return dashHorizontalSpeed.get();
            }

            public static double dashVerticalSpeed() {
                return dashVerticalSpeed.get();
            }

            public static int bladeCount() {
                return bladeCount.get();
            }

            public static float projectileSpeed() {
                return (float) (double) projectileSpeed.get();
            }
        }

        public static final class FrostDomain {
            private static ForgeConfigSpec.DoubleValue damageMultiplier;
            private static ForgeConfigSpec.IntValue radius;
            private static ForgeConfigSpec.IntValue slowDuration;
            private static ForgeConfigSpec.IntValue slowAmplifier;
            private static ForgeConfigSpec.IntValue weaknessDuration;
            private static ForgeConfigSpec.IntValue weaknessAmplifier;
            private static ForgeConfigSpec.IntValue digSlowDuration;
            private static ForgeConfigSpec.IntValue digSlowAmplifier;

            public static float damageMultiplier() {
                return (float) (double) damageMultiplier.get();
            }

            public static int radius() {
                return radius.get();
            }

            public static int slowDuration() {
                return slowDuration.get();
            }

            public static int slowAmplifier() {
                return slowAmplifier.get();
            }

            public static int weaknessDuration() {
                return weaknessDuration.get();
            }

            public static int weaknessAmplifier() {
                return weaknessAmplifier.get();
            }

            public static int digSlowDuration() {
                return digSlowDuration.get();
            }

            public static int digSlowAmplifier() {
                return digSlowAmplifier.get();
            }
        }

        public static final class Inferno {
            private static ForgeConfigSpec.DoubleValue damageMultiplier;
            private static ForgeConfigSpec.IntValue radius;
            private static ForgeConfigSpec.DoubleValue netherDamageMultiplier;
            private static ForgeConfigSpec.IntValue fireSeconds;

            public static float damageMultiplier() {
                return (float) (double) damageMultiplier.get();
            }

            public static int radius() {
                return radius.get();
            }

            public static float netherDamageMultiplier() {
                return (float) (double) netherDamageMultiplier.get();
            }

            public static int fireSeconds() {
                return fireSeconds.get();
            }
        }

        public static final class ZhanTianBaJianShu {
            private static ForgeConfigSpec.DoubleValue damageMultiplier;

            public static float damageMultiplier() {
                return (float) (double) damageMultiplier.get();
            }
        }

        public static final class JianDangBaHuang {
            private static ForgeConfigSpec.DoubleValue damageMultiplier;

            public static float damageMultiplier() {
                return (float) (double) damageMultiplier.get();
            }
        }

        public static final class GengJinJianJue {
            private static ForgeConfigSpec.DoubleValue damageMultiplier;
            private static ForgeConfigSpec.IntValue minSwords;
            private static ForgeConfigSpec.IntValue maxSwords;
            private static ForgeConfigSpec.IntValue refinePerSword;
            private static ForgeConfigSpec.IntValue nearRange;
            private static ForgeConfigSpec.IntValue farRange;
            private static ForgeConfigSpec.IntValue farKillThreshold;
            private static ForgeConfigSpec.DoubleValue projectileSpeed;

            public static float damageMultiplier() {
                return (float) (double) damageMultiplier.get();
            }

            public static int minSwords() {
                return minSwords.get();
            }

            public static int maxSwords() {
                return maxSwords.get();
            }

            public static int refinePerSword() {
                return refinePerSword.get();
            }

            public static int nearRange() {
                return nearRange.get();
            }

            public static int farRange() {
                return farRange.get();
            }

            public static int farKillThreshold() {
                return farKillThreshold.get();
            }

            public static float projectileSpeed() {
                return (float) (double) projectileSpeed.get();
            }
        }

        public static final class QiShaJianJie {
            private static ForgeConfigSpec.DoubleValue damageMultiplier;
            private static ForgeConfigSpec.IntValue baseRadius;
            private static ForgeConfigSpec.IntValue maxRadius;
            private static ForgeConfigSpec.IntValue killsPerRadius;
            private static ForgeConfigSpec.IntValue spawnDistance;
            private static ForgeConfigSpec.IntValue maxHits;
            private static ForgeConfigSpec.IntValue hitInterval;
            private static ForgeConfigSpec.IntValue ownerLeashDistance;

            public static float damageMultiplier() {
                return (float) (double) damageMultiplier.get();
            }

            public static int baseRadius() {
                return baseRadius.get();
            }

            public static int maxRadius() {
                return maxRadius.get();
            }

            public static int killsPerRadius() {
                return killsPerRadius.get();
            }

            public static int spawnDistance() {
                return spawnDistance.get();
            }

            public static int maxHits() {
                return maxHits.get();
            }

            public static int hitInterval() {
                return hitInterval.get();
            }

            public static int ownerLeashDistance() {
                return ownerLeashDistance.get();
            }
        }

        public static final class BloodMoonSlash {
            private static ForgeConfigSpec.DoubleValue healthCostPercent;
            private static ForgeConfigSpec.DoubleValue damagePerHealth;
            private static ForgeConfigSpec.IntValue range;
            private static ForgeConfigSpec.DoubleValue minHealthAfterCost;

            public static float healthCostPercent() {
                return (float) (double) healthCostPercent.get();
            }

            public static float damagePerHealth() {
                return (float) (double) damagePerHealth.get();
            }

            public static int range() {
                return range.get();
            }

            public static float minHealthAfterCost() {
                return (float) (double) minHealthAfterCost.get();
            }
        }

        public static final class ThorsWrath {
            private static ForgeConfigSpec.DoubleValue damageMultiplier;
            private static ForgeConfigSpec.IntValue searchRange;
            private static ForgeConfigSpec.IntValue boltCount;
            private static ForgeConfigSpec.IntValue paralysisDuration;
            private static ForgeConfigSpec.IntValue paralysisAmplifier;
            private static ForgeConfigSpec.BooleanValue disableAttacks;
            private static ForgeConfigSpec.BooleanValue visualOnly;

            public static float damageMultiplier() {
                return (float) (double) damageMultiplier.get();
            }

            public static int searchRange() {
                return searchRange.get();
            }

            public static int boltCount() {
                return boltCount.get();
            }

            public static int paralysisDuration() {
                return paralysisDuration.get();
            }

            public static int paralysisAmplifier() {
                return paralysisAmplifier.get();
            }

            public static boolean disableAttacks() {
                return disableAttacks.get();
            }

            public static boolean visualOnly() {
                return visualOnly.get();
            }
        }

        public static final class ImmovableKing {
            private static ForgeConfigSpec.DoubleValue damageMultiplier;
            private static ForgeConfigSpec.IntValue radius;
            private static ForgeConfigSpec.IntValue durationTicks;
            private static ForgeConfigSpec.IntValue immobilizeAmplifier;

            public static float damageMultiplier() {
                return (float) (double) damageMultiplier.get();
            }

            public static int radius() {
                return radius.get();
            }

            public static int durationTicks() {
                return durationTicks.get();
            }

            public static int immobilizeAmplifier() {
                return immobilizeAmplifier.get();
            }
        }
    }

    private static void defineSa() {
        B.comment("各个 SA 的数值，damageMultiplier 为最终伤害系数（最终伤害 = 基础伤害 × damageMultiplier）")
                .push("sa");

        // ---------- 万剑归宗 ----------
        B.comment("万剑归宗：召唤幻影剑从天而降").push("wan_jian_gui_zong");
        Sa.WanJianGuiZong.damageMultiplier = B.comment("伤害系数")
                .defineInRange("damageMultiplier", 0.8D, 0.0D, 1000.0D);
        Sa.WanJianGuiZong.searchRange = B.comment("索敌半径（格）")
                .defineInRange("searchRange", 64, 1, 512);
        Sa.WanJianGuiZong.minSwords = B.comment("最少召唤剑数")
                .defineInRange("minSwords", 10, 1, 4096);
        Sa.WanJianGuiZong.maxSwords = B.comment("最多召唤剑数（玩家经验等级越高剑越多）")
                .defineInRange("maxSwords", 256, 1, 4096);
        Sa.WanJianGuiZong.projectileSpeed = B.comment("幻影剑飞行速度")
                .defineInRange("projectileSpeed", 2.0D, 0.0D, 100.0D);
        B.pop();

        // ---------- 千本樱 ----------
        B.comment("千本樱：樱花环绕剑阵，逐目标生成多把剑").push("senbonzakura");
        Sa.Senbonzakura.damageMultiplier = B.comment("伤害系数")
                .defineInRange("damageMultiplier", 1.2D, 0.0D, 1000.0D);
        Sa.Senbonzakura.searchRange = B.comment("索敌半径（格）")
                .defineInRange("searchRange", 30, 1, 512);
        Sa.Senbonzakura.minSwordsPerTarget = B.comment("每个目标最少剑数")
                .defineInRange("minSwordsPerTarget", 4, 1, 1024);
        Sa.Senbonzakura.maxSwordsPerTarget = B.comment("每个目标最多剑数")
                .defineInRange("maxSwordsPerTarget", 6, 1, 1024);
        Sa.Senbonzakura.projectileSpeed = B.comment("幻影剑飞行速度")
                .defineInRange("projectileSpeed", 1.5D, 0.0D, 100.0D);
        B.pop();

        // ---------- 裂空斩 ----------
        B.comment("裂空斩：冲刺突进 + 环形剑气").push("rift_slash");
        Sa.RiftSlash.damageMultiplier = B.comment("伤害系数")
                .defineInRange("damageMultiplier", 1.8D, 0.0D, 1000.0D);
        Sa.RiftSlash.dashHorizontalSpeed = B.comment("冲刺水平速度")
                .defineInRange("dashHorizontalSpeed", 6.0D, 0.0D, 100.0D);
        Sa.RiftSlash.dashVerticalSpeed = B.comment("冲刺垂直速度")
                .defineInRange("dashVerticalSpeed", 0.5D, -100.0D, 100.0D);
        Sa.RiftSlash.bladeCount = B.comment("环形剑气数量")
                .defineInRange("bladeCount", 8, 1, 360);
        Sa.RiftSlash.projectileSpeed = B.comment("剑气飞行速度")
                .defineInRange("projectileSpeed", 2.0D, 0.0D, 100.0D);
        B.pop();

        // ---------- 冰霜领域 ----------
        B.comment("冰霜领域：范围冻结并施加负面效果").push("frost_domain");
        Sa.FrostDomain.damageMultiplier = B.comment("伤害系数")
                .defineInRange("damageMultiplier", 0.8D, 0.0D, 1000.0D);
        Sa.FrostDomain.radius = B.comment("作用半径（格）")
                .defineInRange("radius", 20, 1, 512);
        Sa.FrostDomain.slowDuration = B.comment("缓慢效果持续时间（tick，20 = 1 秒）")
                .defineInRange("slowDuration", 120, 0, 100000);
        Sa.FrostDomain.slowAmplifier = B.comment("缓慢效果等级（0 = I）")
                .defineInRange("slowAmplifier", 9, 0, 255);
        Sa.FrostDomain.weaknessDuration = B.comment("虚弱效果持续时间（tick）")
                .defineInRange("weaknessDuration", 100, 0, 100000);
        Sa.FrostDomain.weaknessAmplifier = B.comment("虚弱效果等级（0 = I）")
                .defineInRange("weaknessAmplifier", 1, 0, 255);
        Sa.FrostDomain.digSlowDuration = B.comment("挖掘疲劳持续时间（tick）")
                .defineInRange("digSlowDuration", 80, 0, 100000);
        Sa.FrostDomain.digSlowAmplifier = B.comment("挖掘疲劳等级（0 = I）")
                .defineInRange("digSlowAmplifier", 3, 0, 255);
        B.pop();

        // ---------- 业火焚城 ----------
        B.comment("业火焚城：地面火焰爆发并点燃目标").push("inferno");
        Sa.Inferno.damageMultiplier = B.comment("伤害系数")
                .defineInRange("damageMultiplier", 1.0D, 0.0D, 1000.0D);
        Sa.Inferno.radius = B.comment("作用半径（格）")
                .defineInRange("radius", 18, 1, 512);
        Sa.Inferno.netherDamageMultiplier = B.comment("对下界生物（猪灵/疣猪兽/僵尸疣猪兽/炽足兽/岩浆怪）的额外伤害倍率")
                .defineInRange("netherDamageMultiplier", 1.5D, 0.0D, 1000.0D);
        Sa.Inferno.fireSeconds = B.comment("点燃秒数")
                .defineInRange("fireSeconds", 12, 0, 100000);
        B.pop();

        // ---------- 斩天拔剑术 ----------
        B.comment("斩天拔剑术：竖直巨型剑气，参数见 [entities.giant_slash]").push("zhan_tian_ba_jian_shu");
        Sa.ZhanTianBaJianShu.damageMultiplier = B.comment("伤害系数")
                .defineInRange("damageMultiplier", 1.5D, 0.0D, 1000.0D);
        B.pop();

        // ---------- 剑荡八荒 ----------
        B.comment("剑荡八荒：横向巨型剑气，参数见 [entities.giant_slash]").push("jian_dang_ba_huang");
        Sa.JianDangBaHuang.damageMultiplier = B.comment("伤害系数")
                .defineInRange("damageMultiplier", 1.5D, 0.0D, 1000.0D);
        B.pop();

        // ---------- 庚金剑诀 ----------
        B.comment("庚金剑诀：金色幻影剑，剑数与锻造数相关").push("geng_jin_jian_jue");
        Sa.GengJinJianJue.damageMultiplier = B.comment("伤害系数")
                .defineInRange("damageMultiplier", 1.6D, 0.0D, 1000.0D);
        Sa.GengJinJianJue.minSwords = B.comment("最少剑数")
                .defineInRange("minSwords", 1, 1, 4096);
        Sa.GengJinJianJue.maxSwords = B.comment("最多剑数")
                .defineInRange("maxSwords", 100, 1, 4096);
        Sa.GengJinJianJue.refinePerSword = B.comment("每多少级锻造数增加 1 把剑")
                .defineInRange("refinePerSword", 10, 1, 100000);
        Sa.GengJinJianJue.nearRange = B.comment("杀敌数低于阈值时的索敌半径（格）")
                .defineInRange("nearRange", 20, 1, 512);
        Sa.GengJinJianJue.farRange = B.comment("杀敌数达到阈值后的索敌半径（格）")
                .defineInRange("farRange", 36, 1, 512);
        Sa.GengJinJianJue.farKillThreshold = B.comment("切换为远距离索敌所需的杀敌数")
                .defineInRange("farKillThreshold", 1000, 0, 1000000);
        Sa.GengJinJianJue.projectileSpeed = B.comment("幻影剑飞行速度")
                .defineInRange("projectileSpeed", 2.0D, 0.0D, 100.0D);
        B.pop();

        // ---------- 七杀剑界 ----------
        B.comment("七杀剑界：持续 7 次伤害的领域，范围随杀敌数增长").push("qi_sha_jian_jie");
        Sa.QiShaJianJie.damageMultiplier = B.comment("每次伤害的系数")
                .defineInRange("damageMultiplier", 1.0D, 0.0D, 1000.0D);
        Sa.QiShaJianJie.baseRadius = B.comment("基础半径（格）")
                .defineInRange("baseRadius", 3, 1, 512);
        Sa.QiShaJianJie.maxRadius = B.comment("最大半径（格）")
                .defineInRange("maxRadius", 64, 1, 512);
        Sa.QiShaJianJie.killsPerRadius = B.comment("每多少杀敌数增加 1 格半径")
                .defineInRange("killsPerRadius", 49, 1, 1000000);
        Sa.QiShaJianJie.spawnDistance = B.comment("领域生成在玩家前方多少格")
                .defineInRange("spawnDistance", 5, 0, 128);
        Sa.QiShaJianJie.maxHits = B.comment("领域最大伤害次数")
                .defineInRange("maxHits", 7, 1, 1000);
        Sa.QiShaJianJie.hitInterval = B.comment("两次伤害之间的间隔（tick）")
                .defineInRange("hitInterval", 7, 1, 100000);
        Sa.QiShaJianJie.ownerLeashDistance = B.comment("玩家离领域超过该距离时领域消失（格）")
                .defineInRange("ownerLeashDistance", 64, 1, 2048);
        B.pop();

        // ---------- 血月斩 ----------
        B.comment("血月斩：消耗自身当前生命值，对前方敌人造成基于消耗生命的伤害（无视护甲）")
                .push("blood_moon_slash");
        Sa.BloodMoonSlash.healthCostPercent = B.comment("消耗当前生命值的比例（0.3 = 30%）")
                .defineInRange("healthCostPercent", 0.3D, 0.0D, 1.0D);
        Sa.BloodMoonSlash.damagePerHealth = B.comment("每点消耗生命值转化的伤害")
                .defineInRange("damagePerHealth", 2.0D, 0.0D, 1000.0D);
        Sa.BloodMoonSlash.range = B.comment("生效距离（格，仅命中玩家前方扇形区域内的敌人）")
                .defineInRange("range", 8, 1, 128);
        Sa.BloodMoonSlash.minHealthAfterCost = B.comment("扣除后至少保留的生命值（防止自杀）")
                .defineInRange("minHealthAfterCost", 1.0D, 0.0D, 1000.0D);
        B.pop();

        // ---------- 雷神之怒 ----------
        B.comment("雷神之怒：召唤闪电依次劈向范围内的随机敌人").push("thors_wrath");
        Sa.ThorsWrath.damageMultiplier = B.comment("每道闪电的伤害系数")
                .defineInRange("damageMultiplier", 1.2D, 0.0D, 1000.0D);
        Sa.ThorsWrath.searchRange = B.comment("索敌半径（格）")
                .defineInRange("searchRange", 20, 1, 512);
        Sa.ThorsWrath.boltCount = B.comment("闪电数量（敌人不足时会重复命中）")
                .defineInRange("boltCount", 7, 1, 200);
        Sa.ThorsWrath.paralysisDuration = B.comment("麻痹持续时间（tick，60 = 3 秒）")
                .defineInRange("paralysisDuration", 60, 0, 100000);
        Sa.ThorsWrath.paralysisAmplifier = B.comment("迟缓等级（9 = X）")
                .defineInRange("paralysisAmplifier", 9, 0, 255);
        Sa.ThorsWrath.disableAttacks = B.comment("麻痹期间是否禁止目标攻击")
                .define("disableAttacks", true);
        Sa.ThorsWrath.visualOnly = B.comment("闪电是否仅作视觉表现（true = 不产生原版雷击伤害与火焰）")
                .define("visualOnly", true);
        B.pop();

        // ---------- 不动明王 ----------
        B.comment("不动明王：短时间内免疫伤害且无法移动，结束时对周围造成范围伤害").push("immovable_king");
        Sa.ImmovableKing.damageMultiplier = B.comment("结束时范围伤害的系数")
                .defineInRange("damageMultiplier", 2.0D, 0.0D, 1000.0D);
        Sa.ImmovableKing.radius = B.comment("结束时范围伤害的半径（格）")
                .defineInRange("radius", 10, 1, 512);
        Sa.ImmovableKing.durationTicks = B.comment("免疫与定身持续时间（tick，60 = 3 秒）")
                .defineInRange("durationTicks", 60, 1, 100000);
        Sa.ImmovableKing.immobilizeAmplifier = B.comment("定身所用迟缓等级（255 表示几乎无法移动）")
                .defineInRange("immobilizeAmplifier", 255, 0, 255);
        B.pop();

        B.pop();
    }

    // ==================================================================
    // 3. 实体参数
    // ==================================================================

    public static final class Entities {

        public static final class GiantSlash {
            private static ForgeConfigSpec.IntValue maxDistance;
            private static ForgeConfigSpec.DoubleValue speedPerTick;
            private static ForgeConfigSpec.IntValue maxTicks;
            private static ForgeConfigSpec.IntValue damageCooldown;
            private static ForgeConfigSpec.DoubleValue verticalHitRange;
            private static ForgeConfigSpec.DoubleValue horizontalHitRange;

            public static int maxDistance() {
                return maxDistance.get();
            }

            public static double speedPerTick() {
                return speedPerTick.get();
            }

            public static int maxTicks() {
                return maxTicks.get();
            }

            public static int damageCooldown() {
                return damageCooldown.get();
            }

            public static double verticalHitRange() {
                return verticalHitRange.get();
            }

            public static double horizontalHitRange() {
                return horizontalHitRange.get();
            }
        }
    }

    private static void defineEntities() {
        B.comment("自定义实体的参数").push("entities");

        B.comment("巨型剑气（斩天拔剑术 / 剑荡八荒）").push("giant_slash");
        Entities.GiantSlash.maxDistance = B.comment("最大飞行距离（格）")
                .defineInRange("maxDistance", 64, 1, 1024);
        Entities.GiantSlash.speedPerTick = B.comment("每 tick 飞行距离（格）")
                .defineInRange("speedPerTick", 2.0D, 0.0D, 64.0D);
        Entities.GiantSlash.maxTicks = B.comment("最长存活时间（tick）")
                .defineInRange("maxTicks", 36, 1, 100000);
        Entities.GiantSlash.damageCooldown = B.comment("同一目标的再次受伤冷却（tick）")
                .defineInRange("damageCooldown", 3, 0, 10000);
        Entities.GiantSlash.verticalHitRange = B.comment("竖直剑气的命中判定半径（格）")
                .defineInRange("verticalHitRange", 6.0D, 0.0D, 128.0D);
        Entities.GiantSlash.horizontalHitRange = B.comment("横向剑气的命中判定半径（格）")
                .defineInRange("horizontalHitRange", 8.0D, 0.0D, 128.0D);
        B.pop();

        B.pop();
    }

    // ==================================================================
    // 4. 各个 SE（特殊效果）
    // ==================================================================

    public static final class Se {

        public static final class WindGodPower {
            private static ForgeConfigSpec.IntValue checkInterval;
            private static ForgeConfigSpec.IntValue jumpDuration;
            private static ForgeConfigSpec.IntValue jumpAmplifier;
            private static ForgeConfigSpec.DoubleValue moveSpeedMultiplier;
            private static ForgeConfigSpec.DoubleValue flySpeedMultiplier;

            public static int checkInterval() {
                return checkInterval.get();
            }

            public static int jumpDuration() {
                return jumpDuration.get();
            }

            public static int jumpAmplifier() {
                return jumpAmplifier.get();
            }

            public static double moveSpeedMultiplier() {
                return moveSpeedMultiplier.get();
            }

            public static float flySpeedMultiplier() {
                return (float) (double) flySpeedMultiplier.get();
            }
        }

        public static final class FrostGlow {
            private static ForgeConfigSpec.DoubleValue bonusDamageMultiplier;
            private static ForgeConfigSpec.DoubleValue minBonusDamage;
            private static ForgeConfigSpec.IntValue slowDuration;
            private static ForgeConfigSpec.IntValue slowAmplifier;
            private static ForgeConfigSpec.IntValue digSlowDuration;
            private static ForgeConfigSpec.IntValue digSlowAmplifier;

            public static float bonusDamageMultiplier() {
                return (float) (double) bonusDamageMultiplier.get();
            }

            public static float minBonusDamage() {
                return (float) (double) minBonusDamage.get();
            }

            public static int slowDuration() {
                return slowDuration.get();
            }

            public static int slowAmplifier() {
                return slowAmplifier.get();
            }

            public static int digSlowDuration() {
                return digSlowDuration.get();
            }

            public static int digSlowAmplifier() {
                return digSlowAmplifier.get();
            }
        }

        public static final class TranscendentWill {
            private static ForgeConfigSpec.IntValue enchantBoost;
            private static ForgeConfigSpec.DoubleValue damageMultiplier;
            private static ForgeConfigSpec.DoubleValue critChance;
            private static ForgeConfigSpec.DoubleValue critBonusDamageMultiplier;

            public static int enchantBoost() {
                return enchantBoost.get();
            }

            public static float damageMultiplier() {
                return (float) (double) damageMultiplier.get();
            }

            public static float critChance() {
                return (float) (double) critChance.get();
            }

            public static float critBonusDamageMultiplier() {
                return (float) (double) critBonusDamageMultiplier.get();
            }
        }

        public static final class AbyssWhisper {
            private static ForgeConfigSpec.IntValue checkInterval;
            private static ForgeConfigSpec.IntValue highLevelThreshold;
            private static ForgeConfigSpec.IntValue selfLowBlindnessDuration;
            private static ForgeConfigSpec.IntValue selfLowConfusionDuration;
            private static ForgeConfigSpec.IntValue selfLowHungerDuration;
            private static ForgeConfigSpec.IntValue selfLowUnluckDuration;
            private static ForgeConfigSpec.IntValue selfLowUnluckAmplifier;
            private static ForgeConfigSpec.IntValue selfLowWitherDuration;
            private static ForgeConfigSpec.IntValue selfLowWitherAmplifier;
            private static ForgeConfigSpec.IntValue selfHighUnluckDuration;
            private static ForgeConfigSpec.IntValue selfHighUnluckAmplifier;
            private static ForgeConfigSpec.IntValue selfHighWitherDuration;
            private static ForgeConfigSpec.IntValue targetBlindnessDuration;
            private static ForgeConfigSpec.IntValue targetSlowDuration;
            private static ForgeConfigSpec.IntValue targetSlowAmplifier;
            private static ForgeConfigSpec.IntValue targetDigSlowDuration;
            private static ForgeConfigSpec.IntValue targetDigSlowAmplifier;
            private static ForgeConfigSpec.IntValue targetWitherDuration;
            private static ForgeConfigSpec.IntValue targetWitherAmplifier;
            private static ForgeConfigSpec.IntValue targetWeaknessDuration;
            private static ForgeConfigSpec.IntValue targetWeaknessAmplifier;

            public static int checkInterval() {
                return checkInterval.get();
            }

            public static int highLevelThreshold() {
                return highLevelThreshold.get();
            }

            public static int selfLowBlindnessDuration() {
                return selfLowBlindnessDuration.get();
            }

            public static int selfLowConfusionDuration() {
                return selfLowConfusionDuration.get();
            }

            public static int selfLowHungerDuration() {
                return selfLowHungerDuration.get();
            }

            public static int selfLowUnluckDuration() {
                return selfLowUnluckDuration.get();
            }

            public static int selfLowUnluckAmplifier() {
                return selfLowUnluckAmplifier.get();
            }

            public static int selfLowWitherDuration() {
                return selfLowWitherDuration.get();
            }

            public static int selfLowWitherAmplifier() {
                return selfLowWitherAmplifier.get();
            }

            public static int selfHighUnluckDuration() {
                return selfHighUnluckDuration.get();
            }

            public static int selfHighUnluckAmplifier() {
                return selfHighUnluckAmplifier.get();
            }

            public static int selfHighWitherDuration() {
                return selfHighWitherDuration.get();
            }

            public static int targetBlindnessDuration() {
                return targetBlindnessDuration.get();
            }

            public static int targetSlowDuration() {
                return targetSlowDuration.get();
            }

            public static int targetSlowAmplifier() {
                return targetSlowAmplifier.get();
            }

            public static int targetDigSlowDuration() {
                return targetDigSlowDuration.get();
            }

            public static int targetDigSlowAmplifier() {
                return targetDigSlowAmplifier.get();
            }

            public static int targetWitherDuration() {
                return targetWitherDuration.get();
            }

            public static int targetWitherAmplifier() {
                return targetWitherAmplifier.get();
            }

            public static int targetWeaknessDuration() {
                return targetWeaknessDuration.get();
            }

            public static int targetWeaknessAmplifier() {
                return targetWeaknessAmplifier.get();
            }
        }

        public static final class VoidPower {
            private static ForgeConfigSpec.DoubleValue bonusDamageMultiplier;
            private static ForgeConfigSpec.DoubleValue minBonusDamage;
            private static ForgeConfigSpec.IntValue blindnessDuration;
            private static ForgeConfigSpec.IntValue confusionDuration;

            public static float bonusDamageMultiplier() {
                return (float) (double) bonusDamageMultiplier.get();
            }

            public static float minBonusDamage() {
                return (float) (double) minBonusDamage.get();
            }

            public static int blindnessDuration() {
                return blindnessDuration.get();
            }

            public static int confusionDuration() {
                return confusionDuration.get();
            }
        }

        public static final class Admin {
            private static ForgeConfigSpec.IntValue permissionLevel;
            private static ForgeConfigSpec.IntValue resistanceDuration;
            private static ForgeConfigSpec.IntValue resistanceAmplifier;
            private static ForgeConfigSpec.IntValue messageCooldownTicks;
            private static ForgeConfigSpec.BooleanValue autoExperience;
            private static ForgeConfigSpec.BooleanValue damageImmunity;
            private static ForgeConfigSpec.BooleanValue preventNegativeEffects;
            private static ForgeConfigSpec.BooleanValue instaKill;
            private static ForgeConfigSpec.DoubleValue instaKillRadius;
            private static ForgeConfigSpec.BooleanValue instaKillPlayers;
            private static ForgeConfigSpec.BooleanValue preventKick;

            public static int permissionLevel() {
                return permissionLevel.get();
            }

            public static int resistanceDuration() {
                return resistanceDuration.get();
            }

            public static int resistanceAmplifier() {
                return resistanceAmplifier.get();
            }

            public static int messageCooldownTicks() {
                return messageCooldownTicks.get();
            }

            public static boolean autoExperience() {
                return autoExperience.get();
            }

            public static boolean damageImmunity() {
                return damageImmunity.get();
            }

            public static boolean preventNegativeEffects() {
                return preventNegativeEffects.get();
            }

            public static boolean instaKill() {
                return instaKill.get();
            }

            public static double instaKillRadius() {
                return instaKillRadius.get();
            }

            public static boolean instaKillPlayers() {
                return instaKillPlayers.get();
            }

            public static boolean preventKick() {
                return preventKick.get();
            }
        }

        public static final class FlameHeart {
            private static ForgeConfigSpec.IntValue igniteSeconds;
            private static ForgeConfigSpec.DoubleValue netherSpeedBonus;
            private static ForgeConfigSpec.BooleanValue immuneToFire;

            public static int igniteSeconds() {
                return igniteSeconds.get();
            }

            public static double netherSpeedBonus() {
                return netherSpeedBonus.get();
            }

            public static boolean immuneToFire() {
                return immuneToFire.get();
            }
        }

        public static final class VoidWalker {
            private static ForgeConfigSpec.BooleanValue hoverEnabled;
            private static ForgeConfigSpec.IntValue hoverCheckInterval;
            private static ForgeConfigSpec.DoubleValue hoverSearchDepth;
            private static ForgeConfigSpec.DoubleValue endSpeedBonus;
            private static ForgeConfigSpec.DoubleValue endermanDamageMultiplier;

            public static boolean hoverEnabled() {
                return hoverEnabled.get();
            }

            public static int hoverCheckInterval() {
                return hoverCheckInterval.get();
            }

            public static double hoverSearchDepth() {
                return hoverSearchDepth.get();
            }

            public static double endSpeedBonus() {
                return endSpeedBonus.get();
            }

            public static float endermanDamageMultiplier() {
                return (float) (double) endermanDamageMultiplier.get();
            }
        }

        public static final class TemperedSoul {
            private static ForgeConfigSpec.DoubleValue durabilityCostMultiplier;
            private static ForgeConfigSpec.IntValue durabilityPerKill;
            private static ForgeConfigSpec.IntValue refineBonus;

            public static double durabilityCostMultiplier() {
                return durabilityCostMultiplier.get();
            }

            public static int durabilityPerKill() {
                return durabilityPerKill.get();
            }

            public static int refineBonus() {
                return refineBonus.get();
            }
        }

        public static final class GrimReapersPact {
            private static ForgeConfigSpec.DoubleValue healthThreshold;
            private static ForgeConfigSpec.DoubleValue attackBonus;
            private static ForgeConfigSpec.DoubleValue healthDrainPerSecond;
            private static ForgeConfigSpec.IntValue drainInterval;

            public static double healthThreshold() {
                return healthThreshold.get();
            }

            public static double attackBonus() {
                return attackBonus.get();
            }

            public static float healthDrainPerSecond() {
                return (float) (double) healthDrainPerSecond.get();
            }

            public static int drainInterval() {
                return drainInterval.get();
            }
        }

        public static final class ElvenBlessing {
            private static ForgeConfigSpec.DoubleValue daySpeedBonus;
            private static ForgeConfigSpec.DoubleValue nightAttackBonus;
            private static ForgeConfigSpec.DoubleValue arrowDamageBonus;
            private static ForgeConfigSpec.BooleanValue immuneToPoison;

            public static double daySpeedBonus() {
                return daySpeedBonus.get();
            }

            public static double nightAttackBonus() {
                return nightAttackBonus.get();
            }

            public static double arrowDamageBonus() {
                return arrowDamageBonus.get();
            }

            public static boolean immuneToPoison() {
                return immuneToPoison.get();
            }
        }

        public static final class DragonSoulResonance {
            private static ForgeConfigSpec.DoubleValue permanentAttackBonus;
            private static ForgeConfigSpec.DoubleValue breathChance;
            private static ForgeConfigSpec.DoubleValue breathRadius;
            private static ForgeConfigSpec.DoubleValue breathDamageMultiplier;

            public static double permanentAttackBonus() {
                return permanentAttackBonus.get();
            }

            public static float breathChance() {
                return (float) (double) breathChance.get();
            }

            public static double breathRadius() {
                return breathRadius.get();
            }

            public static float breathDamageMultiplier() {
                return (float) (double) breathDamageMultiplier.get();
            }
        }
    }

    private static void defineSe() {
        B.comment("各个 SE 的数值").push("se");

        // ---------- 风神的伟力 ----------
        B.comment("风神的伟力：跳跃提升、免疫摔落、移速与飞行速度加成").push("wind_god_power");
        Se.WindGodPower.checkInterval = B.comment("每隔多少 tick 检查一次（越小响应越快，性能开销越大）")
                .defineInRange("checkInterval", 5, 1, 1000);
        Se.WindGodPower.jumpDuration = B.comment("跳跃提升持续时间（tick）")
                .defineInRange("jumpDuration", 60, 1, 100000);
        Se.WindGodPower.jumpAmplifier = B.comment("跳跃提升等级（2 = III）")
                .defineInRange("jumpAmplifier", 2, 0, 255);
        Se.WindGodPower.moveSpeedMultiplier = B.comment("移动速度加成（1.0 = +100%）")
                .defineInRange("moveSpeedMultiplier", 1.0D, 0.0D, 100.0D);
        Se.WindGodPower.flySpeedMultiplier = B.comment("飞行速度倍率（2.5 = +150%）")
                .defineInRange("flySpeedMultiplier", 2.5D, 0.1D, 100.0D);
        B.pop();

        // ---------- 寒霜的辉光 ----------
        B.comment("寒霜的辉光：免疫霜冻伤害，攻击附加霜冻伤害").push("frost_glow");
        Se.FrostGlow.bonusDamageMultiplier = B.comment("附加伤害倍率（基于 刀面板 + 玩家攻击力）")
                .defineInRange("bonusDamageMultiplier", 0.75D, 0.0D, 1000.0D);
        Se.FrostGlow.minBonusDamage = B.comment("附加伤害下限")
                .defineInRange("minBonusDamage", 4.0D, 0.0D, 100000.0D);
        Se.FrostGlow.slowDuration = B.comment("缓慢持续时间（tick）")
                .defineInRange("slowDuration", 60, 0, 100000);
        Se.FrostGlow.slowAmplifier = B.comment("缓慢等级（4 = V）")
                .defineInRange("slowAmplifier", 4, 0, 255);
        Se.FrostGlow.digSlowDuration = B.comment("挖掘疲劳持续时间（tick）")
                .defineInRange("digSlowDuration", 80, 0, 100000);
        Se.FrostGlow.digSlowAmplifier = B.comment("挖掘疲劳等级（2 = III）")
                .defineInRange("digSlowAmplifier", 2, 0, 255);
        B.pop();

        // ---------- 超越的意志 ----------
        B.comment("超越的意志：附魔等级提升、伤害倍率、暴击").push("transcendent_will");
        Se.TranscendentWill.enchantBoost = B.comment("附魔等级提升值（首次生效时写入刀 NBT）")
                .defineInRange("enchantBoost", 5, 0, 255);
        Se.TranscendentWill.damageMultiplier = B.comment("伤害倍率")
                .defineInRange("damageMultiplier", 1.5D, 0.0D, 1000.0D);
        Se.TranscendentWill.critChance = B.comment("暴击触发概率（0.25 = 25%）")
                .defineInRange("critChance", 0.25D, 0.0D, 1.0D);
        Se.TranscendentWill.critBonusDamageMultiplier = B.comment("暴击附加伤害倍率（基于刀面板）")
                .defineInRange("critBonusDamageMultiplier", 0.5D, 0.0D, 1000.0D);
        B.pop();

        // ---------- 深渊的低语 ----------
        B.comment("深渊的低语：自身负面效果与攻击附加的负面效果").push("abyss_whisper");
        Se.AbyssWhisper.checkInterval = B.comment("每隔多少 tick 检查一次自身效果")
                .defineInRange("checkInterval", 40, 1, 10000);
        Se.AbyssWhisper.highLevelThreshold = B.comment("高等级判断阈值：经验等级大于该值进入高等级模式")
                .defineInRange("highLevelThreshold", 99, 0, 1000000);
        Se.AbyssWhisper.selfLowBlindnessDuration = B.comment("低等级自身失明持续时间（tick）")
                .defineInRange("selfLowBlindnessDuration", 600, 0, 100000);
        Se.AbyssWhisper.selfLowConfusionDuration = B.comment("低等级自身反胃持续时间（tick）")
                .defineInRange("selfLowConfusionDuration", 100, 0, 100000);
        Se.AbyssWhisper.selfLowHungerDuration = B.comment("低等级自身饥饿持续时间（tick）")
                .defineInRange("selfLowHungerDuration", 100, 0, 100000);
        Se.AbyssWhisper.selfLowUnluckDuration = B.comment("低等级自身霉运持续时间（tick）")
                .defineInRange("selfLowUnluckDuration", 60, 0, 100000);
        Se.AbyssWhisper.selfLowUnluckAmplifier = B.comment("低等级自身霉运等级（4 = V）")
                .defineInRange("selfLowUnluckAmplifier", 4, 0, 255);
        Se.AbyssWhisper.selfLowWitherDuration = B.comment("低等级自身凋零持续时间（tick）")
                .defineInRange("selfLowWitherDuration", 60, 0, 100000);
        Se.AbyssWhisper.selfLowWitherAmplifier = B.comment("低等级自身凋零等级（1 = II）")
                .defineInRange("selfLowWitherAmplifier", 1, 0, 255);
        Se.AbyssWhisper.selfHighUnluckDuration = B.comment("高等级自身霉运持续时间（tick）")
                .defineInRange("selfHighUnluckDuration", 100, 0, 100000);
        Se.AbyssWhisper.selfHighUnluckAmplifier = B.comment("高等级自身霉运等级（2 = III）")
                .defineInRange("selfHighUnluckAmplifier", 2, 0, 255);
        Se.AbyssWhisper.selfHighWitherDuration = B.comment("高等级自身凋零持续时间（tick）")
                .defineInRange("selfHighWitherDuration", 40, 0, 100000);
        Se.AbyssWhisper.targetBlindnessDuration = B.comment("目标失明持续时间（tick）")
                .defineInRange("targetBlindnessDuration", 600, 0, 100000);
        Se.AbyssWhisper.targetSlowDuration = B.comment("目标缓慢持续时间（tick）")
                .defineInRange("targetSlowDuration", 240, 0, 100000);
        Se.AbyssWhisper.targetSlowAmplifier = B.comment("目标缓慢等级（4 = V）")
                .defineInRange("targetSlowAmplifier", 4, 0, 255);
        Se.AbyssWhisper.targetDigSlowDuration = B.comment("目标挖掘疲劳持续时间（tick）")
                .defineInRange("targetDigSlowDuration", 200, 0, 100000);
        Se.AbyssWhisper.targetDigSlowAmplifier = B.comment("目标挖掘疲劳等级（2 = III）")
                .defineInRange("targetDigSlowAmplifier", 2, 0, 255);
        Se.AbyssWhisper.targetWitherDuration = B.comment("目标凋零持续时间（tick）")
                .defineInRange("targetWitherDuration", 200, 0, 100000);
        Se.AbyssWhisper.targetWitherAmplifier = B.comment("目标凋零等级（9 = X）")
                .defineInRange("targetWitherAmplifier", 9, 0, 255);
        Se.AbyssWhisper.targetWeaknessDuration = B.comment("目标虚弱持续时间（tick）")
                .defineInRange("targetWeaknessDuration", 360, 0, 100000);
        Se.AbyssWhisper.targetWeaknessAmplifier = B.comment("目标虚弱等级（4 = V）")
                .defineInRange("targetWeaknessAmplifier", 4, 0, 255);
        B.pop();

        // ---------- 虚空之力 ----------
        B.comment("虚空之力：攻击附加虚空伤害与负面效果").push("void_power");
        Se.VoidPower.bonusDamageMultiplier = B.comment("附加伤害倍率（基于 刀面板 + 玩家攻击力）")
                .defineInRange("bonusDamageMultiplier", 1.0D, 0.0D, 1000.0D);
        Se.VoidPower.minBonusDamage = B.comment("附加伤害下限")
                .defineInRange("minBonusDamage", 9.0D, 0.0D, 100000.0D);
        Se.VoidPower.blindnessDuration = B.comment("目标失明持续时间（tick）")
                .defineInRange("blindnessDuration", 600, 0, 100000);
        Se.VoidPower.confusionDuration = B.comment("目标反胃持续时间（tick）")
                .defineInRange("confusionDuration", 800, 0, 100000);
        B.pop();

        // ---------- 管理员之力 ----------
        B.comment("管理员之力：无敌、飞行、秒杀等（需要对应权限等级）").push("admin");
        Se.Admin.permissionLevel = B.comment("使用所需的 OP 权限等级")
                .defineInRange("permissionLevel", 4, 0, 4);
        Se.Admin.resistanceDuration = B.comment("抗性提升持续时间（tick）")
                .defineInRange("resistanceDuration", 100, 0, 100000);
        Se.Admin.resistanceAmplifier = B.comment("抗性提升等级（255 = 256 级）")
                .defineInRange("resistanceAmplifier", 255, 0, 255);
        Se.Admin.messageCooldownTicks = B.comment("“权限不足”提示的冷却（tick）")
                .defineInRange("messageCooldownTicks", 200, 0, 100000);
        Se.Admin.autoExperience = B.comment("是否在管理员模式下自动获得经验")
                .define("autoExperience", true);
        Se.Admin.damageImmunity = B.comment("是否免疫所有伤害（包括可穿透无敌的伤害类型，如 /kill）")
                .define("damageImmunity", true);
        Se.Admin.preventNegativeEffects = B.comment("是否禁止获得任何负面效果")
                .define("preventNegativeEffects", true);
        Se.Admin.instaKill = B.comment("是否启用秒杀")
                .define("instaKill", true);
        Se.Admin.instaKillRadius = B.comment("秒杀溅射半径（格，0 = 只秒杀直接攻击的目标）")
                .defineInRange("instaKillRadius", 3.0D, 0.0D, 64.0D);
        Se.Admin.instaKillPlayers = B.comment("是否可秒杀其他玩家（创造/旁观模式仍然免疫）")
                .define("instaKillPlayers", true);
        Se.Admin.preventKick = B.comment("是否阻止服务器把管理员踢出（移动过快、违规检测、/kick 等）")
                .define("preventKick", true);
        B.pop();

        // ---------- 烈焰之心 ----------
        B.comment("烈焰之心：攻击点燃敌人、免疫火焰伤害、下界移速加成").push("flame_heart");
        Se.FlameHeart.igniteSeconds = B.comment("攻击点燃目标的秒数")
                .defineInRange("igniteSeconds", 5, 0, 100000);
        Se.FlameHeart.netherSpeedBonus = B.comment("在下界时的移动速度加成（0.2 = +20%）")
                .defineInRange("netherSpeedBonus", 0.2D, 0.0D, 100.0D);
        Se.FlameHeart.immuneToFire = B.comment("是否免疫火焰类伤害")
                .define("immuneToFire", true);
        B.pop();

        // ---------- 虚空行者 ----------
        B.comment("虚空行者：方块边缘悬停、末地移速加成、对末影人伤害加成").push("void_walker");
        Se.VoidWalker.hoverEnabled = B.comment("是否启用边缘悬停")
                .define("hoverEnabled", true);
        Se.VoidWalker.hoverCheckInterval = B.comment("悬停检测间隔（tick）")
                .defineInRange("hoverCheckInterval", 2, 1, 1000);
        Se.VoidWalker.hoverSearchDepth = B.comment("检测下方支撑方块的最大深度（格）")
                .defineInRange("hoverSearchDepth", 2.0D, 0.0D, 64.0D);
        Se.VoidWalker.endSpeedBonus = B.comment("在末地时的移动速度加成（0.3 = +30%）")
                .defineInRange("endSpeedBonus", 0.3D, 0.0D, 100.0D);
        Se.VoidWalker.endermanDamageMultiplier = B.comment("攻击末影人时的伤害倍率")
                .defineInRange("endermanDamageMultiplier", 2.0D, 0.0D, 1000.0D);
        B.pop();

        // ---------- 千锤百炼 ----------
        B.comment("千锤百炼：降低耐久消耗、击杀回复耐久、锻造收益提升").push("tempered_soul");
        Se.TemperedSoul.durabilityCostMultiplier = B.comment("耐久消耗倍率（0.5 = 消耗减半）")
                .defineInRange("durabilityCostMultiplier", 0.5D, 0.0D, 1.0D);
        Se.TemperedSoul.durabilityPerKill = B.comment("每次击杀恢复的耐久点数")
                .defineInRange("durabilityPerKill", 1, 0, 10000);
        Se.TemperedSoul.refineBonus = B.comment("等效额外锻造数（本模组 SA 伤害计算中使用，SlashBlade 未提供精炼上限接口）")
                .defineInRange("refineBonus", 10, 0, 10000);
        B.pop();

        // ---------- 死神之约 ----------
        B.comment("死神之约：低血量时攻击提升，但持续流失生命").push("grim_reapers_pact");
        Se.GrimReapersPact.healthThreshold = B.comment("触发所需生命值比例（0.3 = 30%）")
                .defineInRange("healthThreshold", 0.3D, 0.0D, 1.0D);
        Se.GrimReapersPact.attackBonus = B.comment("攻击力加成（0.5 = +50%）")
                .defineInRange("attackBonus", 0.5D, 0.0D, 100.0D);
        Se.GrimReapersPact.healthDrainPerSecond = B.comment("每秒流失的生命值（不会致死）")
                .defineInRange("healthDrainPerSecond", 1.0D, 0.0D, 1000.0D);
        Se.GrimReapersPact.drainInterval = B.comment("生命流失间隔（tick，20 = 1 秒）")
                .defineInRange("drainInterval", 20, 1, 10000);
        B.pop();

        // ---------- 精灵祝福 ----------
        B.comment("精灵祝福：白天移速、夜晚攻击、免疫中毒、箭矢伤害加成").push("elven_blessing");
        Se.ElvenBlessing.daySpeedBonus = B.comment("白天移动速度加成（0.2 = +20%）")
                .defineInRange("daySpeedBonus", 0.2D, 0.0D, 100.0D);
        Se.ElvenBlessing.nightAttackBonus = B.comment("夜晚攻击力加成（0.3 = +30%）")
                .defineInRange("nightAttackBonus", 0.3D, 0.0D, 100.0D);
        Se.ElvenBlessing.arrowDamageBonus = B.comment("箭矢伤害加成（0.5 = +50%）")
                .defineInRange("arrowDamageBonus", 0.5D, 0.0D, 100.0D);
        Se.ElvenBlessing.immuneToPoison = B.comment("是否免疫中毒")
                .define("immuneToPoison", true);
        B.pop();

        // ---------- 龙魂共鸣 ----------
        B.comment("龙魂共鸣：击杀末影龙后永久增伤，攻击有概率触发龙息").push("dragon_soul_resonance");
        Se.DragonSoulResonance.permanentAttackBonus = B.comment("击杀末影龙后的永久攻击力加成（0.1 = +10%）")
                .defineInRange("permanentAttackBonus", 0.1D, 0.0D, 100.0D);
        Se.DragonSoulResonance.breathChance = B.comment("攻击触发龙息的概率（0.05 = 5%）")
                .defineInRange("breathChance", 0.05D, 0.0D, 1.0D);
        Se.DragonSoulResonance.breathRadius = B.comment("龙息范围半径（格）")
                .defineInRange("breathRadius", 4.0D, 0.5D, 64.0D);
        Se.DragonSoulResonance.breathDamageMultiplier = B.comment("龙息伤害倍率（基于基础伤害）")
                .defineInRange("breathDamageMultiplier", 1.0D, 0.0D, 1000.0D);
        B.pop();

        B.pop();
    }

    // ==================================================================
    // 5. 战利品：SA 的获取方式
    // ==================================================================

    public static final class Loot {
        private static ForgeConfigSpec.BooleanValue enabled;
        private static ForgeConfigSpec.ConfigValue<String> item;
        private static ForgeConfigSpec.ConfigValue<List<? extends String>> entries;

        public static boolean enabled() {
            return enabled.get();
        }

        public static String itemId() {
            return item.get();
        }

        public static List<String> entries() {
            List<? extends String> raw = entries.get();
            return raw == null ? List.of() : List.copyOf(raw);
        }
    }

    private static void defineLoot() {
        B.comment("SA 的获取方式：在指定结构宝箱中按概率生成带有 SA 的耀魂宝珠",
                        "条目格式：\"战利品表ID|SA ID|概率\"，例如：",
                        "  \"minecraft:chests/ancient_city|slashexpansion:wan_jian_gui_zong|0.10\"",
                        "可以自由增删条目、修改结构、SA 与概率；概率范围 0.0 ~ 1.0")
                .push("loot");

        Loot.enabled = B.comment("是否启用结构宝箱注入")
                .define("enabled", true);
        Loot.item = B.comment("掉落的物品 ID（默认耀魂宝珠）")
                .define("item", "slashblade:proudsoul_sphere");
        Loot.entries = B.comment("战利品条目列表",
                        "默认对应：远古城市 / 末地城 / 堡垒遗迹 / 下界要塞")
                .defineList("entries", List.of(
                        "minecraft:chests/ancient_city|slashexpansion:wan_jian_gui_zong|0.10",
                        "minecraft:chests/ancient_city|slashexpansion:senbonzakura|0.20",
                        "minecraft:chests/ancient_city|slashexpansion:frost_domain|0.15",
                        "minecraft:chests/ancient_city|slashexpansion:zhan_tian_ba_jian_shu|0.10",
                        "minecraft:chests/ancient_city|slashexpansion:jian_dang_ba_huang|0.10",
                        "minecraft:chests/ancient_city|slashexpansion:geng_jin_jian_jue|0.08",
                        "minecraft:chests/ancient_city|slashexpansion:qi_sha_jian_jie|0.05",
                        "minecraft:chests/end_city_treasure|slashexpansion:rift_slash|0.25",
                        "minecraft:chests/end_city_treasure|slashexpansion:zhan_tian_ba_jian_shu|0.12",
                        "minecraft:chests/end_city_treasure|slashexpansion:jian_dang_ba_huang|0.12",
                        "minecraft:chests/bastion_treasure|slashexpansion:inferno|0.15",
                        "minecraft:chests/bastion_treasure|slashexpansion:geng_jin_jian_jue|0.10",
                        "minecraft:chests/nether_bridge|slashexpansion:wan_jian_gui_zong|0.08",
                        "minecraft:chests/nether_bridge|slashexpansion:qi_sha_jian_jie|0.06",
                        "minecraft:chests/nether_bridge|slashexpansion:blood_moon_slash|0.10",
                        "minecraft:chests/bastion_treasure|slashexpansion:thors_wrath|0.12",
                        "minecraft:chests/bastion_treasure|slashexpansion:immovable_king|0.08"
                ), o -> o instanceof String);

        B.pop();
    }

    // ==================================================================
    // 6. 配方：SE 的合成方式
    // ==================================================================

    public static final class Recipes {
        private static ForgeConfigSpec.BooleanValue overrideJsonRecipes;

        public static final String[] EFFECT_IDS = {
                "wind_god_power", "frost_glow", "transcendent_will",
                "abyss_whisper", "void_power", "admin",
                "flame_heart", "void_walker", "tempered_soul",
                "grim_reapers_pact", "elven_blessing", "dragon_soul_resonance"
        };

        public static boolean overrideJsonRecipes() {
            return overrideJsonRecipes.get();
        }

        /** 该 SE 对应的配方 ID（glow -> slashexpansion:glow_proudsoul_crystal）。 */
        public static String recipeIdOf(String effectId) {
            return "slashexpansion:" + effectId + "_proudsoul_crystal";
        }
    }

    public static final class RecipeEntry {
        private final String effectId;
        private final ForgeConfigSpec.BooleanValue enabled;
        private final ForgeConfigSpec.BooleanValue shapeless;
        private final ForgeConfigSpec.ConfigValue<String> resultItem;
        private final ForgeConfigSpec.ConfigValue<List<? extends String>> pattern;
        private final ForgeConfigSpec.ConfigValue<List<? extends String>> keys;
        private final ForgeConfigSpec.ConfigValue<List<? extends String>> ingredients;

        private RecipeEntry(String effectId, String defaultResult,
                            List<String> defaultPattern, List<String> defaultKeys) {
            this.effectId = effectId;
            this.enabled = B.comment("是否启用该配方（false 时该配方将被移除）")
                    .define("enabled", true);
            this.shapeless = B.comment("是否使用无序合成；为 true 时只读取 ingredients，忽略 pattern / keys")
                    .define("shapeless", false);
            this.resultItem = B.comment("产物物品 ID（会自动附加 SpecialEffectType 标签）")
                    .define("resultItem", defaultResult);
            this.pattern = B.comment("有序合成的摆放图案（最多 3 行，每行最多 3 个字符；空格表示该格留空）")
                    .defineList("pattern", defaultPattern, o -> o instanceof String);
            this.keys = B.comment("有序合成的字符映射，格式 \"字符=物品ID\"")
                    .defineList("keys", defaultKeys, o -> o instanceof String);
            this.ingredients = B.comment("无序合成的材料列表（仅当 shapeless = true 时生效）")
                    .defineListAllowEmpty("ingredients", List.<String>of(), o -> o instanceof String);
        }

        public String effectId() {
            return effectId;
        }

        public boolean enabled() {
            return enabled.get();
        }

        public boolean shapeless() {
            return shapeless.get();
        }

        public String resultItem() {
            return resultItem.get();
        }

        public List<String> pattern() {
            List<? extends String> raw = pattern.get();
            return raw == null ? List.of() : List.copyOf(raw);
        }

        public List<String> keys() {
            List<? extends String> raw = keys.get();
            return raw == null ? List.of() : List.copyOf(raw);
        }

        public List<String> ingredients() {
            List<? extends String> raw = ingredients.get();
            return raw == null ? List.of() : List.copyOf(raw);
        }
    }

    public static final class RecipeEntries {
        public static RecipeEntry WIND_GOD_POWER;
        public static RecipeEntry FROST_GLOW;
        public static RecipeEntry TRANSCENDENT_WILL;
        public static RecipeEntry ABYSS_WHISPER;
        public static RecipeEntry VOID_POWER;
        public static RecipeEntry ADMIN;
        public static RecipeEntry FLAME_HEART;
        public static RecipeEntry VOID_WALKER;
        public static RecipeEntry TEMPERED_SOUL;
        public static RecipeEntry GRIM_REAPERS_PACT;
        public static RecipeEntry ELVEN_BLESSING;
        public static RecipeEntry DRAGON_SOUL_RESONANCE;

        public static RecipeEntry byEffectId(String effectId) {
            return switch (effectId) {
                case "wind_god_power" -> WIND_GOD_POWER;
                case "frost_glow" -> FROST_GLOW;
                case "transcendent_will" -> TRANSCENDENT_WILL;
                case "abyss_whisper" -> ABYSS_WHISPER;
                case "void_power" -> VOID_POWER;
                case "admin" -> ADMIN;
                case "flame_heart" -> FLAME_HEART;
                case "void_walker" -> VOID_WALKER;
                case "tempered_soul" -> TEMPERED_SOUL;
                case "grim_reapers_pact" -> GRIM_REAPERS_PACT;
                case "elven_blessing" -> ELVEN_BLESSING;
                case "dragon_soul_resonance" -> DRAGON_SOUL_RESONANCE;
                default -> null;
            };
        }
    }

    private static void defineRecipes() {
        B.comment("SE 的合成配方。修改后重启游戏（或执行 /reload）生效。",
                        "shapeless = false：按 pattern + keys 进行有序合成；shapeless = true：按 ingredients 进行无序合成。",
                        "若把 overrideJsonRecipes 设为 false，则完全使用模组内置的 JSON 配方，以下设置不会生效。")
                .push("recipe");

        Recipes.overrideJsonRecipes = B.comment("是否用本配置覆盖内置 JSON 配方")
                .define("overrideJsonRecipes", true);

        B.comment("风神的伟力").push("wind_god_power");
        RecipeEntries.WIND_GOD_POWER = new RecipeEntry("wind_god_power", "slashblade:proudsoul_crystal",
                List.of("ECM", "CSC", "PCP"),
                List.of("E=minecraft:elytra", "C=slashblade:proudsoul_crystal",
                        "M=minecraft:phantom_membrane", "S=minecraft:nether_star",
                        "P=minecraft:beacon"));
        B.pop();

        B.comment("寒霜的辉光").push("frost_glow");
        RecipeEntries.FROST_GLOW = new RecipeEntry("frost_glow", "slashblade:proudsoul_crystal",
                List.of("CIC", "BWP", "CAC"),
                List.of("C=slashblade:proudsoul_crystal", "I=minecraft:blue_ice",
                        "B=minecraft:packed_ice", "A=minecraft:ice",
                        "W=minecraft:conduit", "P=minecraft:powder_snow_bucket"));
        B.pop();

        B.comment("超越的意志").push("transcendent_will");
        RecipeEntries.TRANSCENDENT_WILL = new RecipeEntry("transcendent_will", "slashblade:proudsoul_crystal",
                List.of("XFY", "DSB", "CPZ"),
                List.of("X=minecraft:silence_armor_trim_smithing_template",
                        "F=minecraft:totem_of_undying",
                        "Y=minecraft:rib_armor_trim_smithing_template",
                        "D=minecraft:dragon_egg",
                        "S=slashblade:proudsoul_crystal",
                        "B=minecraft:beacon",
                        "C=minecraft:spire_armor_trim_smithing_template",
                        "P=minecraft:enchanted_golden_apple",
                        "Z=minecraft:ward_armor_trim_smithing_template"));
        B.pop();

        B.comment("深渊的低语").push("abyss_whisper");
        RecipeEntries.ABYSS_WHISPER = new RecipeEntry("abyss_whisper", "slashblade:proudsoul_crystal",
                List.of("CWC", "SCD", "CEC"),
                List.of("C=slashblade:proudsoul_crystal", "W=minecraft:wither_skeleton_skull",
                        "S=minecraft:sculk_shrieker", "D=minecraft:end_crystal",
                        "E=minecraft:echo_shard"));
        B.pop();

        B.comment("虚空之力").push("void_power");
        RecipeEntries.VOID_POWER = new RecipeEntry("void_power", "slashblade:proudsoul_crystal",
                List.of("CEC", "DAD", "CEC"),
                List.of("C=minecraft:bedrock", "E=minecraft:echo_shard",
                        "D=minecraft:end_crystal", "A=slashblade:proudsoul_crystal"));
        B.pop();

        B.comment("管理员之力").push("admin");
        RecipeEntries.ADMIN = new RecipeEntry("admin", "slashblade:proudsoul_crystal",
                List.of("CWC", "SAD", "CEC"),
                List.of("C=slashblade:proudsoul_crystal", "W=minecraft:structure_block",
                        "S=minecraft:barrier", "A=minecraft:command_block",
                        "D=minecraft:bedrock", "E=minecraft:light"));
        B.pop();

        B.comment("烈焰之心").push("flame_heart");
        RecipeEntries.FLAME_HEART = new RecipeEntry("flame_heart", "slashblade:proudsoul_crystal",
                List.of("BBB", "BCB", "BBB"),
                List.of("B=minecraft:blaze_rod", "C=slashblade:proudsoul_crystal"));
        B.pop();

        B.comment("虚空行者").push("void_walker");
        RecipeEntries.VOID_WALKER = new RecipeEntry("void_walker", "slashblade:proudsoul_crystal",
                List.of("BBB", "BCB", "BBB"),
                List.of("B=minecraft:ender_pearl", "C=slashblade:proudsoul_crystal"));
        B.pop();

        B.comment("千锤百炼").push("tempered_soul");
        RecipeEntries.TEMPERED_SOUL = new RecipeEntry("tempered_soul", "slashblade:proudsoul_crystal",
                List.of(" B ", "BCB", " B "),
                List.of("B=minecraft:netherite_ingot", "C=slashblade:proudsoul_crystal"));
        B.pop();

        B.comment("死神之约").push("grim_reapers_pact");
        RecipeEntries.GRIM_REAPERS_PACT = new RecipeEntry("grim_reapers_pact", "slashblade:proudsoul_crystal",
                List.of("BBB", "BCB", "BBB"),
                List.of("B=minecraft:wither_skeleton_skull", "C=slashblade:proudsoul_crystal"));
        B.pop();

        B.comment("精灵祝福").push("elven_blessing");
        RecipeEntries.ELVEN_BLESSING = new RecipeEntry("elven_blessing", "slashblade:proudsoul_crystal",
                List.of("BBB", "BCB", "BBB"),
                List.of("B=minecraft:golden_apple", "C=slashblade:proudsoul_crystal"));
        B.pop();

        B.comment("龙魂共鸣（3x3 内最多容纳 9 格，因此龙息数量使用 7 个）").push("dragon_soul_resonance");
        RecipeEntries.DRAGON_SOUL_RESONANCE = new RecipeEntry("dragon_soul_resonance", "slashblade:proudsoul_crystal",
                List.of("BEB", "BCB", "BBB"),
                List.of("B=minecraft:dragon_breath", "E=minecraft:dragon_egg",
                        "C=slashblade:proudsoul_crystal"));
        B.pop();

        B.pop();
    }
}
