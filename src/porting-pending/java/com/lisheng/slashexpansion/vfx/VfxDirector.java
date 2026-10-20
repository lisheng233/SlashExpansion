package com.lisheng.slashexpansion.vfx;

import com.lisheng.slashexpansion.SlashExpansion;
import com.lisheng.slashexpansion.config.SlashExpansionConfig;
import net.minecraft.server.level.ServerLevel;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.level.LevelEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.slf4j.Logger;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * 特效时间轴调度器。
 *
 * <p>所有 SA 动画都以 {@link Vfx.Effect} 的形式挂在这里，每个服务端 tick 推进一次，
 * 让特效能够分阶段播放（蓄势 → 斩击 → 冲击波 → 余韵）。</p>
 */
@Mod.EventBusSubscriber(modid = SlashExpansion.MOD_ID)
public final class VfxDirector {

    private static final Logger LOGGER = com.mojang.logging.LogUtils.getLogger();

    private static final List<Task> TASKS = new ArrayList<>();

    public static void schedule(ServerLevel level, int delay, Vfx.Effect effect) {
        if (level == null || effect == null) return;
        if (!SlashExpansionConfig.Vfx.enabled()) return;
        if (TASKS.size() >= SlashExpansionConfig.Vfx.maxConcurrentEffects()) return;
        TASKS.add(new Task(level, effect, Math.max(0, delay)));
    }

    /**
     * 不受「特效上限 / 特效总开关」影响的任务。
     *
     * <p>飞剑生成之类的工作属于结算逻辑而不是纯视觉，不能被特效开关或数量上限吞掉，
     * 否则会在人多或者特效关掉时直接丢伤害。</p>
     */
    public static void scheduleUncapped(ServerLevel level, int delay, Vfx.Effect effect) {
        if (level == null || effect == null) return;
        TASKS.add(new Task(level, effect, Math.max(0, delay)));
    }

    public static void clear(ServerLevel level) {
        TASKS.removeIf(task -> task.level == level);
    }

    public static int activeCount() {
        return TASKS.size();
    }

    @SubscribeEvent
    public static void onLevelTick(TickEvent.LevelTickEvent event) {
        if (event.phase != TickEvent.Phase.END) return;
        if (!(event.level instanceof ServerLevel level)) return;
        if (TASKS.isEmpty()) return;

        Vfx.beginTick();

        Iterator<Task> iterator = TASKS.iterator();
        while (iterator.hasNext()) {
            Task task = iterator.next();
            if (task.level != level) continue;
            if (task.delay > 0) {
                task.delay--;
                continue;
            }
            boolean keep;
            try {
                keep = task.effect.tick(level, task.age++);
            } catch (Throwable throwable) {
                LOGGER.error("[SlashExpansion] 特效推进失败，已丢弃该特效", throwable);
                keep = false;
            }
            if (!keep) iterator.remove();
        }
    }

    @SubscribeEvent
    public static void onLevelUnload(LevelEvent.Unload event) {
        if (event.getLevel() instanceof ServerLevel level) {
            clear(level);
        }
    }

    private static final class Task {
        private final ServerLevel level;
        private final Vfx.Effect effect;
        private int delay;
        private int age;

        private Task(ServerLevel level, Vfx.Effect effect, int delay) {
            this.level = level;
            this.effect = effect;
            this.delay = delay;
        }
    }

    private VfxDirector() {
    }
}
