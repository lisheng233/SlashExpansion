package com.lisheng.slashexpansion.loot;

import com.lisheng.slashexpansion.config.SlashExpansionConfig;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.neoforged.neoforge.common.loot.IGlobalLootModifier;
import net.neoforged.neoforge.common.loot.LootModifier;

/**
 * SA 获取：按配置往指定战利品表里按概率追加「带 SA 的耀魂宝珠」。
 *
 * <p>1.21 移除了 {@code LootTableLoadEvent}，改用 Global Loot Modifier：
 * 运行时能拿到本次查询的战利品表 ID（{@link LootContext#getQueriedLootTableId()}），
 * 所以配置里「表 ID | SA ID | 概率」的语义与 1.20.1 完全一致。</p>
 */
public class SaScrollLootModifier extends LootModifier {

    public static final MapCodec<SaScrollLootModifier> CODEC = RecordCodecBuilder.mapCodec(instance ->
            codecStart(instance).apply(instance, SaScrollLootModifier::new));

    public SaScrollLootModifier(LootItemCondition[] conditions) {
        super(conditions);
    }

    @Override
    public MapCodec<? extends IGlobalLootModifier> codec() {
        return CODEC;
    }

    @Override
    protected ObjectArrayList<ItemStack> doApply(ObjectArrayList<ItemStack> generatedLoot, LootContext context) {
        if (!SlashExpansionConfig.Loot.enabled()) {
            return generatedLoot;
        }

        ResourceLocation tableId = context.getQueriedLootTableId();
        if (tableId == null) {
            return generatedLoot;
        }

        Item item = resolveLootItem();
        RandomSource random = context.getRandom();

        for (String raw : SlashExpansionConfig.Loot.entries()) {
            String[] parts = raw.split("\\|");
            if (parts.length != 3) {
                continue;
            }
            if (!parts[0].trim().equals(tableId.toString())) {
                continue;
            }

            double chance;
            try {
                chance = Double.parseDouble(parts[2].trim());
            } catch (NumberFormatException e) {
                continue;
            }
            if (chance <= 0.0D || random.nextDouble() >= chance) {
                continue;
            }

            ItemStack stack = new ItemStack(item);
            CompoundTag tag = new CompoundTag();
            tag.putString("SpecialAttackType", parts[1].trim());
            stack.set(DataComponents.CUSTOM_DATA, CustomData.of(tag));
            generatedLoot.add(stack);
        }

        return generatedLoot;
    }

    private static Item resolveLootItem() {
        try {
            ResourceLocation id = ResourceLocation.parse(SlashExpansionConfig.Loot.itemId());
            Item item = BuiltInRegistries.ITEM.get(id);
            if (item != null && item != net.minecraft.world.item.Items.AIR) {
                return item;
            }
        } catch (Exception ignored) {
            // 配置写错时回退
        }
        return net.minecraft.world.item.Items.EMERALD;
    }
}