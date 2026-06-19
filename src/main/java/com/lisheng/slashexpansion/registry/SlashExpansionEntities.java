package com.lisheng.slashexpansion.registry;

import com.lisheng.slashexpansion.SlashExpansion;
import com.lisheng.slashexpansion.entity.*;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class SlashExpansionEntities {
    public static final DeferredRegister<EntityType<?>> ENTITIES =
            DeferredRegister.create(ForgeRegistries.ENTITY_TYPES, SlashExpansion.MOD_ID);

    // 万剑归宗
    public static final RegistryObject<EntityType<WanJianGuiZongSwordEntity>> WAN_JIAN_SWORD =
            ENTITIES.register("wan_jian_sword",
                    () -> EntityType.Builder.<WanJianGuiZongSwordEntity>of(
                                    WanJianGuiZongSwordEntity::new, MobCategory.MISC)
                            .sized(0.9F, 0.9F)
                            .setTrackingRange(4)
                            .setUpdateInterval(20)
                            .setCustomClientFactory(WanJianGuiZongSwordEntity::createInstance)
                            .build("wan_jian_sword")
            );

    // 千本樱
    public static final RegistryObject<EntityType<SenbonzakuraSwordEntity>> SENBONZAKURA_SWORD =
            ENTITIES.register("senbonzakura_sword",
                    () -> EntityType.Builder.<SenbonzakuraSwordEntity>of(
                                    SenbonzakuraSwordEntity::new, MobCategory.MISC)
                            .sized(0.9F, 0.9F)
                            .setTrackingRange(4)
                            .setUpdateInterval(20)
                            .setCustomClientFactory(SenbonzakuraSwordEntity::createInstance)
                            .build("senbonzakura_sword")
            );

    // 冰霜领域（使用默认召唤剑实体，无需自定义）
    // 业火焚城（使用默认召唤剑实体，无需自定义）
    // 裂空斩（使用 EntityDrive，无需注册）

    public static void register(IEventBus bus) {
        ENTITIES.register(bus);
    }
}