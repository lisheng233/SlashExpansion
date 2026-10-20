package com.lisheng.slashexpansion.registry;

import com.lisheng.slashexpansion.SlashExpansion;
import mods.flammpfeil.slashblade.slasharts.SlashArts;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

/**
 * SA（SlashArts）注册。构造器签名与 1.20.1 相同：传入「返回 ComboState id」的函数。
 * 阶段一只注册万剑归宗与裂空斩。
 */
public class SlashExpansionSlashArtsRegistry {

    public static final DeferredRegister<SlashArts> SLASH_ARTS =
            DeferredRegister.create(SlashArts.REGISTRY_KEY, SlashExpansion.MOD_ID);

    public static final DeferredHolder<SlashArts, SlashArts> FROST_DOMAIN =
            SLASH_ARTS.register("frost_domain",
                    () -> new SlashArts(entity -> id(SlashExpansionComboStateRegistry.FROST_DOMAIN)));

    public static final DeferredHolder<SlashArts, SlashArts> RIFT_SLASH =
            SLASH_ARTS.register("rift_slash",
                    () -> new SlashArts(entity -> id(SlashExpansionComboStateRegistry.RIFT_SLASH)));

    /** DeferredHolder -> ResourceLocation（1.21 里 RegistryObject 换成了 DeferredHolder）。 */
    public static ResourceLocation id(DeferredHolder<?, ?> holder) {
        return holder.getKey().location();
    }
}