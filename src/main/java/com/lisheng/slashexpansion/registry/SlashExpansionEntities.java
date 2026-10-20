package com.lisheng.slashexpansion.registry;

import com.lisheng.slashexpansion.SlashExpansion;
import com.lisheng.slashexpansion.entity.GiantSlashEntity;
import com.lisheng.slashexpansion.entity.QiShaJianJieDomainEntity;
import com.lisheng.slashexpansion.entity.SenbonzakuraSwordEntity;
import com.lisheng.slashexpansion.entity.WanJianGuiZongSwordEntity;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

/**
 * 自定义实体注册。
 *
 * <p>1.21 的 {@code EntityType.Builder} 不再需要 {@code setCustomClientFactory}：
 * 客户端会直接用注册时给出的 {@code EntityType} + 构造函数完成实例化。</p>
 */
public class SlashExpansionEntities {

    public static final DeferredRegister<EntityType<?>> ENTITIES =
            DeferredRegister.create(Registries.ENTITY_TYPE, SlashExpansion.MOD_ID);

    // 万剑归宗 / 庚金剑诀共用的幻影剑
    public static final DeferredHolder<EntityType<?>, EntityType<WanJianGuiZongSwordEntity>> WAN_JIAN_SWORD =
            ENTITIES.register("wan_jian_sword",
                    () -> EntityType.Builder.<WanJianGuiZongSwordEntity>of(
                                    WanJianGuiZongSwordEntity::new, MobCategory.MISC)
                            .sized(0.9F, 0.9F)
                            .setTrackingRange(4)
                            .setUpdateInterval(20)
                            .build("wan_jian_sword"));

    // 千本樱
    public static final DeferredHolder<EntityType<?>, EntityType<SenbonzakuraSwordEntity>> SENBONZAKURA_SWORD =
            ENTITIES.register("senbonzakura_sword",
                    () -> EntityType.Builder.<SenbonzakuraSwordEntity>of(
                                    SenbonzakuraSwordEntity::new, MobCategory.MISC)
                            .sized(0.9F, 0.9F)
                            .setTrackingRange(4)
                            .setUpdateInterval(20)
                            .build("senbonzakura_sword"));

    // 七杀剑界领域
    public static final DeferredHolder<EntityType<?>, EntityType<QiShaJianJieDomainEntity>> QI_SHA_JIAN_JIE_DOMAIN =
            ENTITIES.register("qi_sha_jian_jie_domain",
                    () -> EntityType.Builder.<QiShaJianJieDomainEntity>of(
                                    QiShaJianJieDomainEntity::new, MobCategory.MISC)
                            .sized(0.5F, 0.5F)
                            .setTrackingRange(32)
                            .setUpdateInterval(1)
                            .build("qi_sha_jian_jie_domain"));

    // 斩天拔剑术 / 剑荡八荒的巨型剑气
    public static final DeferredHolder<EntityType<?>, EntityType<GiantSlashEntity>> GIANT_SLASH =
            ENTITIES.register("giant_slash",
                    () -> EntityType.Builder.<GiantSlashEntity>of(
                                    GiantSlashEntity::new, MobCategory.MISC)
                            .sized(0.5F, 0.5F)
                            .setTrackingRange(64)
                            .setUpdateInterval(1)
                            .build("giant_slash"));

    public static void register(IEventBus bus) {
        ENTITIES.register(bus);
    }
}
