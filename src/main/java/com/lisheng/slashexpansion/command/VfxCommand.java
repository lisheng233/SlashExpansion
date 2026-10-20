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
@net.minecraftforge.fml.common.Mod.EventBusSubscriber(modid = SlashExpansion.MOD_ID)
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

    @net.minecraftforge.eventbus.api.SubscribeEvent
    public static void onRegisterCommands(net.minecraftforge.event.RegisterCommandsEvent event) {
        CommandDispatcher<CommandSourceStack> dispatcher = event.getDispatcher();
        dispatcher.register(Commands.literal("slashexpansion")
                .then(Commands.literal("vfx")
                        .requires(source -> source.hasPermission(2))
                        .then(Commands.argument("id", StringArgumentType.word())
                                .suggests((context, builder) -> SharedSuggestionProvider.suggest(IDS, builder))
                                .executes(VfxCommand::execute))
                        .then(Commands.literal("list").executes(VfxCommand::list))));
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

    private VfxCommand() {
    }
}
