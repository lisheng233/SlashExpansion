package com.lisheng.slashexpansion.command;

import com.lisheng.slashexpansion.SlashExpansion;
import com.lisheng.slashexpansion.vfx.SaVfx;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import net.minecraft.ChatFormatting;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.SharedSuggestionProvider;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;

import java.util.List;

/**
 * 特效预览命令：{@code /slashexpansion vfx <名字>}。
 *
 * <p>方便在不打架的情况下单独欣赏（或者调参）每个 SA 的动画。</p>
 */
@net.neoforged.fml.common.EventBusSubscriber(modid = SlashExpansion.MOD_ID)
public final class VfxCommand {

    private static final List<String> IDS = List.of(
            "wan_jian_gui_zong",
            "senbonzakura",
            "rift_slash",
            "frost_domain",
            "inferno",
            "zhan_tian_ba_jian_shu",
            "jian_dang_ba_huang",
            "geng_jin_jian_jue",
            "qi_sha_jian_jie",
            "blood_moon_slash",
            "thors_wrath",
            "immovable_king_cast",
            "immovable_king_release"
    );

    @net.neoforged.bus.api.SubscribeEvent
    public static void onRegisterCommands(net.neoforged.neoforge.event.RegisterCommandsEvent event) {
        CommandDispatcher<CommandSourceStack> dispatcher = event.getDispatcher();
        dispatcher.register(Commands.literal("slashexpansion")
                .then(Commands.literal("vfx")
                        .requires(source -> source.hasPermission(2))
                        .then(Commands.argument("id", StringArgumentType.word())
                                .suggests((context, builder) -> SharedSuggestionProvider.suggest(IDS, builder))
                                .executes(VfxCommand::execute))
                        .then(Commands.literal("list").executes(VfxCommand::list))
                        .then(Commands.literal("ping").executes(VfxCommand::ping))));
    }

    private static int list(CommandContext<CommandSourceStack> context) {
        context.getSource().sendSuccess(
                () -> Component.literal("可用特效：" + String.join(", ", IDS)).withStyle(ChatFormatting.GOLD),
                false);
        return 1;
    }

    private static int execute(CommandContext<CommandSourceStack> context) {
        ServerPlayer player;
        try {
            player = context.getSource().getPlayerOrException();
        } catch (Exception exception) {
            context.getSource().sendFailure(Component.literal("该命令只能由玩家执行"));
            return 0;
        }

        String id = StringArgumentType.getString(context, "id");
        if (!IDS.contains(id)) {
            context.getSource().sendFailure(Component.literal("未知特效：" + id + "（可用 /slashexpansion vfx list 查看）"));
            return 0;
        }

        Level level = player.level();
        List<LivingEntity> targets = level.getEntitiesOfClass(LivingEntity.class,
                player.getBoundingBox().inflate(20.0D),
                entity -> entity != player && entity.isAlive() && !entity.isSpectator());
        if (targets.isEmpty()) {
            targets = List.of(player);
        }

        switch (id) {
            case "wan_jian_gui_zong" -> SaVfx.wanJianGuiZong(player, targets);
            case "senbonzakura" -> SaVfx.senbonzakura(player, targets);
            case "rift_slash" -> SaVfx.riftSlash(player);
            case "frost_domain" -> SaVfx.frostDomain(player, targets, 8);
            case "inferno" -> SaVfx.inferno(player, targets, 8);
            case "zhan_tian_ba_jian_shu" -> SaVfx.zhanTianBaJianShu(player);
            case "jian_dang_ba_huang" -> SaVfx.jianDangBaHuang(player);
            case "geng_jin_jian_jue" -> SaVfx.gengJinJianJue(player, targets);
            case "qi_sha_jian_jie" -> SaVfx.qiShaJianJie(player,
                    player.position().add(player.getLookAngle().scale(8.0D)), 8);
            case "blood_moon_slash" -> SaVfx.bloodMoonSlash(player, targets, 8);
            case "thors_wrath" -> SaVfx.thorsWrath(player, targets);
            case "immovable_king_cast" -> SaVfx.immovableKingCast(player, 60);
            case "immovable_king_release" -> SaVfx.immovableKingRelease(player, 8);
            default -> {
                return 0;
            }
        }

        context.getSource().sendSuccess(
                () -> Component.literal("播放特效：" + id).withStyle(ChatFormatting.AQUA), false);
        return 1;
    }


    /**
     * 诊断用：直接在脚下打出一圈自定义粒子与一发屏幕闪光。
     *
     * <p>用来区分「特效调度没跑」与「粒子层没渲染」：
     * 看到光点说明粒子与网络都正常，问题在 VfxDirector 的时间轴；
     * 什么都看不到则说明粒子层或资源有问题。</p>
     */
    private static int ping(CommandContext<CommandSourceStack> context) {
        ServerPlayer player;
        try {
            player = context.getSource().getPlayerOrException();
        } catch (Exception exception) {
            context.getSource().sendFailure(Component.literal("该命令只能由玩家执行"));
            return 0;
        }

        Level level = player.level();
        if (!(level instanceof net.minecraft.server.level.ServerLevel serverLevel)) {
            context.getSource().sendFailure(Component.literal("该命令只能在服务端执行"));
            return 0;
        }

        double cx = player.getX();
        double cy = player.getY() + 1.0D;
        double cz = player.getZ();

        // 1) 自定义柔光粒子：一圈 64 个
        for (int i = 0; i < 64; i++) {
            double angle = Math.PI * 2.0D * i / 64.0D;
            double x = cx + Math.cos(angle) * 2.0D;
            double z = cz + Math.sin(angle) * 2.0D;
            serverLevel.sendParticles(
                    new com.lisheng.slashexpansion.particle.GlowParticleOptions(0xFFFFD24A, 0.9F, 40),
                    x, cy, z, 1, 0.0D, 0.0D, 0.0D, 0.0D);
        }

        // 2) 自定义法阵粒子：地面 1 发、较大
        serverLevel.sendParticles(
                new com.lisheng.slashexpansion.particle.RuneParticleOptions(0xFF5FD8FF, 6.0F, 60, 0.02F, 0.2F),
                cx, cy - 0.9D, cz, 1, 0.0D, 0.0D, 0.0D, 0.0D);

        // 3) 原版粒子对照：如果这一圈也没有，说明是客户端粒子渲染整体问题
        serverLevel.sendParticles(net.minecraft.core.particles.ParticleTypes.END_ROD,
                cx, cy + 0.5D, cz, 16, 0.6D, 0.6D, 0.6D, 0.02D);

        // 4) 屏幕闪光：验证网络通道
        com.lisheng.slashexpansion.net.SfxNet.screenEffect(level, player.position(), 16.0D,
                0xFFD24A, 0.35F, 10, 0.0F);

        int tasks = com.lisheng.slashexpansion.vfx.VfxDirector.activeCount();
        context.getSource().sendSuccess(() -> Component.literal(
                "诊断已触发：64 个 glow + 1 个 rune + 16 个原版 END_ROD，当前特效任务数 " + tasks)
                .withStyle(ChatFormatting.AQUA), false);
        return 1;
    }
    private VfxCommand() {
    }
}
