package com.lisheng.slashexpansion.registry;

import com.lisheng.slashexpansion.SlashExpansion;
import mods.flammpfeil.slashblade.slasharts.SlashArts;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;

public class SlashExpansionSlashArtsRegistry {
    public static final DeferredRegister<SlashArts> SLASH_ARTS =
            DeferredRegister.create(SlashArts.REGISTRY_KEY, SlashExpansion.MOD_ID);

    public static final RegistryObject<SlashArts> WAN_JIAN_GUI_ZONG =
            SLASH_ARTS.register("wan_jian_gui_zong",
                    () -> new SlashArts((e) -> SlashExpansionComboStateRegistry.WAN_JIAN_GUI_ZONG.getId()));

    public static final RegistryObject<SlashArts> SENBONZAKURA =
            SLASH_ARTS.register("senbonzakura",
                    () -> new SlashArts((e) -> SlashExpansionComboStateRegistry.SENBONZAKURA.getId()));

    public static final RegistryObject<SlashArts> RIFT_SLASH =
            SLASH_ARTS.register("rift_slash",
                    () -> new SlashArts((e) -> SlashExpansionComboStateRegistry.RIFT_SLASH.getId()));

    public static final RegistryObject<SlashArts> FROST_DOMAIN =
            SLASH_ARTS.register("frost_domain",
                    () -> new SlashArts((e) -> SlashExpansionComboStateRegistry.FROST_DOMAIN.getId()));

    public static final RegistryObject<SlashArts> INFERNO =
            SLASH_ARTS.register("inferno",
                    () -> new SlashArts((e) -> SlashExpansionComboStateRegistry.INFERNO.getId()));
}