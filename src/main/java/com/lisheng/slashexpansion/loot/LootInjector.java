package com.lisheng.slashexpansion.loot;

import com.lisheng.slashexpansion.SlashExpansion;
import mods.flammpfeil.slashblade.init.SBItems;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.entries.LootItem;
import net.minecraft.world.level.storage.loot.functions.SetNbtFunction;
import net.minecraft.world.level.storage.loot.predicates.LootItemRandomChanceCondition;
import net.minecraft.world.level.storage.loot.providers.number.ConstantValue;
import net.minecraftforge.event.LootTableLoadEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.Map;

@Mod.EventBusSubscriber(modid = SlashExpansion.MOD_ID)
public class LootInjector {

    // ★ 使用 ResourceLocation.parse 替代 new ResourceLocation
    private static final Map<ResourceLocation, Map<String, Float>> LOOT_MAP = Map.of(
        ResourceLocation.parse("minecraft:chests/ancient_city"),
            Map.of(
                "slashexpansion:wan_jian_gui_zong", 0.10f,
                "slashexpansion:senbonzakura", 0.20f,
                "slashexpansion:frost_domain", 0.15f,
                "slashexpansion:zhan_tian_ba_jian_shu", 0.10f,
                "slashexpansion:jian_dang_ba_huang", 0.10f,
                "slashexpansion:geng_jin_jian_jue", 0.08f,
                "slashexpansion:qi_sha_jian_jie", 0.05f
            ),
        ResourceLocation.parse("minecraft:chests/end_city_treasure"),
            Map.of(
                "slashexpansion:rift_slash", 0.25f,
                "slashexpansion:zhan_tian_ba_jian_shu", 0.12f,
                "slashexpansion:jian_dang_ba_huang", 0.12f
            ),
        ResourceLocation.parse("minecraft:chests/bastion_treasure"),
            Map.of(
                "slashexpansion:inferno", 0.15f,
                "slashexpansion:geng_jin_jian_jue", 0.10f
            ),
        ResourceLocation.parse("minecraft:chests/nether_bridge"),
            Map.of(
                "slashexpansion:wan_jian_gui_zong", 0.08f,
                "slashexpansion:qi_sha_jian_jie", 0.06f
            )
    );

    @SubscribeEvent
    public static void onLootTableLoad(LootTableLoadEvent event) {
        ResourceLocation tableId = event.getName();
        if (!LOOT_MAP.containsKey(tableId)) return;

        Map<String, Float> saMap = LOOT_MAP.get(tableId);
        for (Map.Entry<String, Float> entry : saMap.entrySet()) {
            String saId = entry.getKey();
            float chance = entry.getValue();

            CompoundTag nbt = new CompoundTag();
            nbt.putString("SpecialAttackType", saId);

            LootPool pool = LootPool.lootPool()
                    .add(LootItem.lootTableItem(SBItems.proudsoul_sphere)
                            .apply(SetNbtFunction.setTag(nbt))
                            .when(LootItemRandomChanceCondition.randomChance(chance))
                            .setWeight(1)
                    )
                    .setRolls(ConstantValue.exactly(1))
                    .build();

            event.getTable().addPool(pool);
        }
    }
}