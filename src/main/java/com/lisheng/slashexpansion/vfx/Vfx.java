package com.lisheng.slashexpansion.vfx;

import com.lisheng.slashexpansion.config.SlashExpansionConfig;
import com.lisheng.slashexpansion.particle.GlowParticleOptions;
import com.lisheng.slashexpansion.particle.RuneParticleOptions;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

/**
 * 特效绘制原语库。
 *
 * <p>这里只做“画”，不做“游戏逻辑”。每个原语返回一个 {@link Effect}，
 * 由 {@link VfxDirector} 按刻推进，所以特效可以是真正的动画（扩散、旋转、扫过、汇聚），
 * 而不是施放瞬间炸一下的静态粒子。</p>
 *
 * <p>所有坐标都是绝对世界坐标，因此施法者移动不会带着已经放出去的刀光跑。</p>
 */
public final class Vfx {

    // ================= 调色板（ARGB） =================
    public static final int WHITE = 0xFFFFFFFF;
    public static final int GOLD = 0xFFFFD24A;
    public static final int GOLD_DEEP = 0xFFE89A1F;
    public static final int CRIMSON = 0xFFE0162B;
    public static final int BLOOD = 0xFF8E0B14;
    public static final int CYAN = 0xFF5FD8FF;
    public static final int ICE = 0xFFB3ECFF;
    public static final int PINK = 0xFFFF8FC7;
    public static final int SAKURA = 0xFFFFC2DE;
    public static final int ORANGE = 0xFFFF7A18;
    public static final int EMBER = 0xFFFFB347;
    public static final int VIOLET = 0xFFA855F7;
    public static final int DEEP_VIOLET = 0xFF5B21B6;
    public static final int LIGHTNING = 0xFFD9EEFF;
    public static final int JADE = 0xFF5CFFB0;
    public static final int SHADOW = 0xFF2B0A3D;

    /** 单刻推进的特效。返回 false 表示该特效结束。 */
    @FunctionalInterface
    public interface Effect {
        boolean tick(ServerLevel level, int age);
    }

    // ================= 调度入口 =================

    public static void play(Level level, Effect effect) {
        play(level, 0, effect);
    }

    public static void play(Level level, int delay, Effect effect) {
        if (level instanceof ServerLevel serverLevel) {
            VfxDirector.schedule(serverLevel, delay, effect);
        }
    }

    /** 结算类任务（例如按时生成飞剑），不受特效开关与数量上限影响。 */
    public static void playUncapped(Level level, int delay, Effect effect) {
        if (level instanceof ServerLevel serverLevel) {
            VfxDirector.scheduleUncapped(serverLevel, delay, effect);
        }
    }

    // ================= 底层发射 =================

    private static int budgetUsed;

    /** 每个服务端 tick 开始时由 {@link VfxDirector} 调用，重置本 tick 的粒子预算。 */
    static void beginTick() {
        budgetUsed = 0;
    }

    private static boolean takeBudget(int amount) {
        if (amount <= 0) return false;
        int budget = SlashExpansionConfig.Vfx.particleBudget();
        if (budget <= 0) return false;
        if (budgetUsed + amount > budget) return false;
        budgetUsed += amount;
        return true;
    }

    public static void glow(ServerLevel level, Vec3 p, int argb, float size, int life) {
        if (!takeBudget(1)) return;
        level.sendParticles(new GlowParticleOptions(argb, size, life), p.x, p.y, p.z, 1, 0.0, 0.0, 0.0, 0.0);
    }

    /** 一批高斯散开的光点，比逐点发射省很多带宽。 */
    public static void glowCloud(ServerLevel level, Vec3 p, int argb, int amount, double spread, float size, int life) {
        int n = count(amount);
        if (n <= 0 || !takeBudget(n)) return;
        level.sendParticles(new GlowParticleOptions(argb, size, life), p.x, p.y, p.z, n, spread, spread, spread, 0.0);
    }

    public static void rune(ServerLevel level, Vec3 p, double size, int argb, int life, float spin, float birthScale) {
        if (!takeBudget(1)) return;
        level.sendParticles(new RuneParticleOptions(argb, (float) size, life, spin, birthScale),
                p.x, p.y + 0.06, p.z, 1, 0.0, 0.0, 0.0, 0.0);
    }

    /** 在 a、b 之间画一条光带。 */
    public static void line(ServerLevel level, Vec3 a, Vec3 b, int argb, float size, int life, double step) {
        double distance = a.distanceTo(b);
        if (distance < 1.0E-4) {
            glow(level, a, argb, size, life);
            return;
        }
        int n = Math.max(2, (int) Math.ceil(distance / step));
        for (int i = 0; i <= n; i++) {
            glow(level, a.lerp(b, (double) i / n), argb, size, life);
        }
    }

    static int count(int amount) {
        return Math.max(0, Math.round(amount * SlashExpansionConfig.Vfx.particleScale()));
    }

    static int perTick(int amount) {
        return Math.max(2, count(amount));
    }

    static int alpha(int argb, float factor) {
        int a = (int) (((argb >>> 24) & 0xFF) * Mth.clamp(factor, 0.0F, 1.0F));
        return (a << 24) | (argb & 0xFFFFFF);
    }

    public static Vec3 randomDirection(RandomSource random) {
        double z = random.nextDouble() * 2.0 - 1.0;
        double angle = random.nextDouble() * Mth.TWO_PI;
        double r = Math.sqrt(1.0D - z * z);
        return new Vec3(r * Math.cos(angle), z, r * Math.sin(angle));
    }

    /** 绕任意轴旋转（罗德里格斯公式）。 */
    public static Vec3 rotate(Vec3 v, Vec3 axis, double angle) {
        Vec3 k = axis.normalize();
        double cos = Math.cos(angle);
        double sin = Math.sin(angle);
        return v.scale(cos)
                .add(k.cross(v).scale(sin))
                .add(k.scale(k.dot(v) * (1.0D - cos)));
    }

    // ================= 原语 =================

    /** 地面或者任意水平面上的扩散圆环。 */
    public static Effect ring(Vec3 center, double r0, double r1, int argb, int amount, int ticks, float size, int life) {
        return new Ring(center, r0, r1, argb, amount, Math.max(1, ticks), size, life);
    }

    /** 竖直的冲击环（例如刀气命中面）。{@code normal} 为环面法线。 */
    public static Effect halo(Vec3 center, Vec3 normal, double r0, double r1, int argb, int amount, int ticks,
                              float size, int life) {
        return new Halo(center, normal.normalize(), r0, r1, argb, amount, Math.max(1, ticks), size, life);
    }

    /** 球状爆散。 */
    public static Effect burst(Vec3 center, int argb, int amount, double radius, float size, int life) {
        return new Burst(center, argb, amount, radius, size, life);
    }

    /** 空心球壳。 */
    public static Effect shell(Vec3 center, int argb, int amount, double radius, float size, int life) {
        return new Shell(center, argb, amount, radius, size, life);
    }

    /** 光柱。 */
    public static Effect pillar(Vec3 base, double height, double radius, int argb, int amount, int ticks,
                                float size, int life) {
        return new Pillar(base, height, radius, argb, amount, Math.max(1, ticks), size, life);
    }

    /** 螺旋（剑气漩涡、上升的火舌）。 */
    public static Effect spiral(Vec3 center, double r0, double r1, double h0, double h1, int argb, int amount,
                                int ticks, float size, int life, double turns) {
        return new Spiral(center, r0, r1, h0, h1, argb, amount, Math.max(1, ticks), size, life, turns);
    }

    /** 向内汇聚（万剑归宗、聚气）。 */
    public static Effect implode(Vec3 center, double r0, double r1, double h, int argb, int amount, int ticks,
                                 float size, int life, double turns) {
        return new Implode(center, r0, r1, h, argb, amount, Math.max(1, ticks), size, life, turns);
    }

    /**
     * 刀光：沿某个轴扫过一段圆弧。
     *
     * @param axis 旋转轴，水平横扫用 (0,1,0)，竖劈用视线与上方向的叉积
     */
    public static Effect arc(Vec3 center, Vec3 look, Vec3 axis, double radius, double halfAngleRad, int argb,
                             int amount, int ticks, float size, int life) {
        return new Arc(center, look, axis.normalize(), radius, halfAngleRad, argb, amount, Math.max(1, ticks), size, life);
    }

    /** 直线光带（持续若干刻）。 */
    public static Effect beam(Vec3 from, Vec3 to, int argb, int amount, int ticks, float size, int life, double jitter) {
        return new Beam(from, to, argb, amount, Math.max(1, ticks), size, life, jitter);
    }

    /** 折线闪电。 */
    public static Effect lightning(Vec3 from, Vec3 to, int argb, int ticks, float size) {
        return new Lightning(from, to, argb, Math.max(1, ticks), size);
    }

    /** 残影：跟着实体每刻留一串光点。 */
    public static Effect trail(Entity entity, int argb, int ticks, float size, int life, double spread) {
        return new Trail(entity, argb, Math.max(1, ticks), size, life, spread);
    }

    /** 脉动光球。 */
    public static Effect orb(Vec3 center, double radius, int argb, int amount, int ticks, float size, int life,
                             double pulse) {
        return new Orb(center, radius, argb, amount, Math.max(1, ticks), size, life, pulse);
    }

    /** 生成一个法阵。 */
    public static Effect rune(Vec3 center, double size, int argb, int life, float spin, float birthScale) {
        return new RuneOnce(center, size, argb, life, spin, birthScale);
    }

    /** 连续生成法阵（旋转法阵的脉冲感）。 */
    public static Effect runePulse(Vec3 center, double size, int argb, int life, float spin, float birthScale,
                                   int ticks, int interval) {
        return new RunePulse(center, size, argb, life, spin, birthScale, Math.max(1, ticks), Math.max(1, interval));
    }

    // ================= 实现 =================

    private record Ring(Vec3 c, double r0, double r1, int argb, int amount, int ticks,
                        float size, int life) implements Effect {
        @Override
        public boolean tick(ServerLevel level, int age) {
            double t = (double) age / Math.max(1, ticks - 1);
            double ease = 1.0D - Math.pow(1.0D - t, 2.6D);
            double r = r0 + (r1 - r0) * ease;
            int n = perTick(amount);
            for (int i = 0; i < n; i++) {
                double a = (i + level.random.nextDouble() * 0.9D) / n * Mth.TWO_PI;
                glow(level, new Vec3(c.x + Math.cos(a) * r, c.y, c.z + Math.sin(a) * r),
                        alpha(argb, 1.0F - (float) t * 0.55F), size, life);
            }
            return age < ticks - 1;
        }
    }

    private record Halo(Vec3 c, Vec3 normal, double r0, double r1, int argb, int amount, int ticks,
                        float size, int life) implements Effect {
        @Override
        public boolean tick(ServerLevel level, int age) {
            double t = (double) age / Math.max(1, ticks - 1);
            double r = r0 + (r1 - r0) * (1.0D - Math.pow(1.0D - t, 2.2D));

            Vec3 helper = Math.abs(normal.y) > 0.95D ? new Vec3(1.0, 0.0, 0.0) : new Vec3(0.0, 1.0, 0.0);
            Vec3 u = normal.cross(helper).normalize();
            Vec3 v = normal.cross(u).normalize();

            int n = perTick(amount);
            for (int i = 0; i < n; i++) {
                double a = (i + level.random.nextDouble() * 0.9D) / n * Mth.TWO_PI;
                Vec3 p = c.add(u.scale(Math.cos(a) * r)).add(v.scale(Math.sin(a) * r));
                glow(level, p, alpha(argb, 1.0F - (float) t * 0.5F), size, life);
            }
            return age < ticks - 1;
        }
    }

    private record Burst(Vec3 c, int argb, int amount, double radius, float size, int life) implements Effect {
        @Override
        public boolean tick(ServerLevel level, int age) {
            int n = count(amount);
            for (int i = 0; i < n; i++) {
                double r = radius * Math.cbrt(level.random.nextDouble());
                glow(level, c.add(randomDirection(level.random).scale(r)), argb, size, life);
            }
            return false;
        }
    }

    private record Shell(Vec3 c, int argb, int amount, double radius, float size, int life) implements Effect {
        @Override
        public boolean tick(ServerLevel level, int age) {
            int n = count(amount);
            for (int i = 0; i < n; i++) {
                double r = radius * (0.92D + level.random.nextDouble() * 0.16D);
                glow(level, c.add(randomDirection(level.random).scale(r)), argb, size, life);
            }
            return false;
        }
    }

    private record Pillar(Vec3 base, double height, double radius, int argb, int amount, int ticks,
                          float size, int life) implements Effect {
        @Override
        public boolean tick(ServerLevel level, int age) {
            double t = (double) age / Math.max(1, ticks - 1);
            double grow = Math.min(1.0D, t * 3.0D);
            double h = height * grow;
            double r = radius * (1.0D - 0.35D * t);
            int n = perTick(amount);
            for (int i = 0; i < n; i++) {
                double a = level.random.nextDouble() * Mth.TWO_PI;
                double d = r * Math.sqrt(level.random.nextDouble());
                double y = base.y + level.random.nextDouble() * h;
                glow(level, new Vec3(base.x + Math.cos(a) * d, y, base.z + Math.sin(a) * d),
                        alpha(argb, 1.0F - (float) t * 0.6F), size, life);
            }
            return age < ticks - 1;
        }
    }

    private record Spiral(Vec3 c, double r0, double r1, double h0, double h1, int argb, int amount, int ticks,
                          float size, int life, double turns) implements Effect {
        @Override
        public boolean tick(ServerLevel level, int age) {
            double t = (double) age / Math.max(1, ticks - 1);
            double r = r0 + (r1 - r0) * t;
            double h = h0 + (h1 - h0) * t;
            int n = perTick(amount);
            for (int i = 0; i < n; i++) {
                double phase = (double) i / n;
                double a = turns * Mth.TWO_PI * (t + phase * 0.35D);
                double rr = r * (0.85D + level.random.nextDouble() * 0.3D);
                double y = c.y + h * (0.9D + level.random.nextDouble() * 0.2D);
                glow(level, new Vec3(c.x + Math.cos(a) * rr, y, c.z + Math.sin(a) * rr),
                        alpha(argb, 1.0F - (float) t * 0.5F), size, life);
            }
            return age < ticks - 1;
        }
    }

    private record Implode(Vec3 c, double r0, double r1, double h, int argb, int amount, int ticks,
                           float size, int life, double turns) implements Effect {
        @Override
        public boolean tick(ServerLevel level, int age) {
            double t = (double) age / Math.max(1, ticks - 1);
            double r = r0 + (r1 - r0) * (t * t);
            int n = perTick(amount);
            for (int i = 0; i < n; i++) {
                double phase = (double) i / n;
                double a = turns * Mth.TWO_PI * (t * 1.6D + phase * 0.25D);
                double y = c.y + h * (1.0D - t) * (0.7D + level.random.nextDouble() * 0.3D);
                // 越接近中心越亮
                float fade = 0.45F + 0.55F * (float) t;
                glow(level, new Vec3(c.x + Math.cos(a) * r, y, c.z + Math.sin(a) * r),
                        alpha(argb, fade), size, life);
            }
            return age < ticks - 1;
        }
    }

    private record Arc(Vec3 center, Vec3 look, Vec3 axis, double radius, double halfAngle, int argb, int amount,
                       int ticks, float size, int life) implements Effect {
        @Override
        public boolean tick(ServerLevel level, int age) {
            double t = (double) age / Math.max(1, ticks - 1);
            // 刀光从一侧扫到另一侧，前段快、后段慢
            double sweep = 1.0D - Math.pow(1.0D - t, 1.8D);
            double head = -halfAngle + 2.0D * halfAngle * sweep;
            int n = perTick(amount);
            double tailStep = halfAngle * 0.28D;

            for (int k = 0; k < 5; k++) {
                double a = head - tailStep * k;
                if (a < -halfAngle) continue;
                float fade = 1.0F - k * 0.18F;
                float thickness = size * (1.0F - k * 0.12F);
                Vec3 dir = rotate(look, axis, a);
                int nk = Math.max(1, n / (k + 1));
                for (int i = 0; i < nk; i++) {
                    double rr = radius * (0.94D + level.random.nextDouble() * 0.12D);
                    Vec3 p = center.add(dir.scale(rr));
                    glow(level, p, alpha(argb, fade * (1.0F - (float) t * 0.25F)), thickness, life);
                }
            }
            return age < ticks - 1;
        }
    }

    private record Beam(Vec3 from, Vec3 to, int argb, int amount, int ticks, float size, int life,
                        double jitter) implements Effect {
        @Override
        public boolean tick(ServerLevel level, int age) {
            double t = (double) age / Math.max(1, ticks - 1);
            int n = perTick(amount);
            for (int i = 0; i <= n; i++) {
                double k = (double) i / n;
                Vec3 p = from.lerp(to, k);
                if (jitter > 0.0) {
                    p = p.add((level.random.nextDouble() - 0.5) * jitter,
                            (level.random.nextDouble() - 0.5) * jitter,
                            (level.random.nextDouble() - 0.5) * jitter);
                }
                glow(level, p, alpha(argb, (1.0F - (float) t * 0.4F) * (0.6F + 0.4F * (float) k)), size, life);
            }
            return age < ticks - 1;
        }
    }

    private record Lightning(Vec3 from, Vec3 to, int argb, int ticks, float size) implements Effect {
        @Override
        public boolean tick(ServerLevel level, int age) {
            if (age % 2 != 0) return age < ticks - 1;
            int segments = 9;
            Vec3 prev = from;
            for (int i = 1; i <= segments; i++) {
                double k = (double) i / segments;
                Vec3 point = from.lerp(to, k);
                double spread = 0.9D * Math.sin(k * Math.PI);
                point = point.add((level.random.nextDouble() - 0.5) * spread,
                        (level.random.nextDouble() - 0.5) * spread,
                        (level.random.nextDouble() - 0.5) * spread);
                line(level, prev, point, argb, size, 6, 0.85D);
                prev = point;
            }
            if (age % 4 == 0) {
                glow(level, from, alpha(argb, 0.9F), size * 3.0F, 6);
            }
            return age < ticks - 1;
        }
    }

    private record Trail(Entity entity, int argb, int ticks, float size, int life, double spread) implements Effect {
        @Override
        public boolean tick(ServerLevel level, int age) {
            if (entity == null || entity.isRemoved() || entity.level() != level) return false;
            Vec3 p = entity.position().add(0.0, entity.getBbHeight() * 0.5, 0.0);
            glowCloud(level, p, argb, 14, spread, size, life);
            return age < ticks - 1;
        }
    }

    private record Orb(Vec3 c, double radius, int argb, int amount, int ticks, float size, int life,
                       double pulse) implements Effect {
        @Override
        public boolean tick(ServerLevel level, int age) {
            double t = (double) age / Math.max(1, ticks - 1);
            double r = radius * (0.85D + 0.15D * Math.sin(age * pulse));
            int n = perTick(amount);
            for (int i = 0; i < n; i++) {
                Vec3 dir = randomDirection(level.random);
                glow(level, c.add(dir.scale(r * (0.7D + level.random.nextDouble() * 0.3D))),
                        alpha(argb, (float) (0.9D - t * 0.2D)), size, life);
            }
            return age < ticks - 1;
        }
    }

    private record RuneOnce(Vec3 c, double size, int argb, int life, float spin, float birthScale) implements Effect {
        @Override
        public boolean tick(ServerLevel level, int age) {
            rune(level, c, size, argb, life, spin, birthScale);
            return false;
        }
    }

    private record RunePulse(Vec3 c, double size, int argb, int life, float spin, float birthScale,
                             int ticks, int interval) implements Effect {
        @Override
        public boolean tick(ServerLevel level, int age) {
            if (age % interval == 0) {
                rune(level, c, size, argb, life, spin, birthScale);
            }
            return age < ticks - 1;
        }
    }

    private Vfx() {
    }
}
