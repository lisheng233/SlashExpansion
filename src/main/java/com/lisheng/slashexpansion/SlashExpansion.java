package com.lisheng.slashexpansion;

import com.mojang.logging.LogUtils;
import com.lisheng.slashexpansion.config.SlashExpansionConfig;
import com.lisheng.slashexpansion.registry.SlashExpansionComboStateRegistry;
import com.lisheng.slashexpansion.registry.SlashExpansionEntities;
import com.lisheng.slashexpansion.registry.SlashExpansionParticles;
import com.lisheng.slashexpansion.registry.SlashExpansionSlashArtsRegistry;
import com.lisheng.slashexpansion.registry.SlashExpansionSpecialEffectsRegistry;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import org.slf4j.Logger;

/**
 * SlashExpansion — NeoForge 1.21.1 版本。
 *
 * <p>本分支从 1.20.1 / Forge 47 移植而来，迁移分两阶段：
 * 阶段一接入构建骨架与 2 个 SA（裂空斩、冰霜领域）+ 2 个 SE（烈焰之心、千锤百炼），
 * 用于验证 SlashBlade 2.x 的 API（刀状态数据组件、ComboState、SpecialEffect、EntityDrive）；
 * 阶段二再把剩余 10 个 SA、10 个 SE、特效系统与 Mixin 补齐。</p>
 */
@Mod(SlashExpansion.MOD_ID)
public class SlashExpansion {

    public static final String MOD_ID = "slashexpansion";

    private static final Logger LOGGER = LogUtils.getLogger();

    public SlashExpansion(IEventBus modBus, ModContainer container) {
        // config/slashexpansion-common.toml
        container.registerConfig(ModConfig.Type.COMMON, SlashExpansionConfig.SPEC);

        SlashExpansionEntities.register(modBus);
        SlashExpansionParticles.register(modBus);
        SlashExpansionComboStateRegistry.COMBO_STATES.register(modBus);
        SlashExpansionSlashArtsRegistry.SLASH_ARTS.register(modBus);
        SlashExpansionSpecialEffectsRegistry.SPECIAL_EFFECTS.register(modBus);

        LOGGER.info("[SlashExpansion] NeoForge 1.21.1 已加载：12 SA / 2 SE / 4 实体 / 2 粒子");
    }

    /** 1.21 起 ResourceLocation 构造器已私有化，统一走工厂方法。 */
    public static ResourceLocation prefix(String path) {
        return ResourceLocation.fromNamespaceAndPath(MOD_ID, path);
    }
}