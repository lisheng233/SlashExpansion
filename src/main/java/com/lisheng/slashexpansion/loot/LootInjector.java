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

    private static final Map<ResourceLocation, Float> STRUCTURE_CHANCES = Map.of(
        new ResourceLocation("minecraft:chests/ancient_city"),         0.30f,
        new ResourceLocation("minecraft:chests/end_city_treasure"),    0.25f,
        new ResourceLocation("minecraft:chests/bastion_treasure"),     0.15f,
        new ResourceLocation("minecraft:chests/bastion_bridge"),       0.09f,
        new ResourceLocation("minecraft:chests/bastion_other"),        0.06f,
        new ResourceLocation("minecraft:chests/bastion_hoglin_stable"),0.06f,
        new ResourceLocation("minecraft:chests/nether_bridge"),        0.08f
    );

    @SubscribeEvent
    public static void onLootTableLoad(LootTableLoadEvent event) {
        ResourceLocation tableId = event.getName();
        if (!STRUCTURE_CHANCES.containsKey(tableId)) return;

        float chance = STRUCTURE_CHANCES.get(tableId);

        // ★ 使用 NBTExplorer 确认的键名
        CompoundTag nbt = new CompoundTag();
        nbt.putString("SpecialAttackType", "slashexpansion:wan_jian_gui_zong");

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