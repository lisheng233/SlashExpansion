package com.lisheng.slashexpansion.loot;

import com.lisheng.slashexpansion.SlashExpansion;
import com.lisheng.slashexpansion.config.SlashExpansionConfig;
import com.mojang.logging.LogUtils;
import mods.flammpfeil.slashblade.init.SBItems;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.entries.LootItem;
import net.minecraft.world.level.storage.loot.functions.SetNbtFunction;
import net.minecraft.world.level.storage.loot.predicates.LootItemRandomChanceCondition;
import net.minecraft.world.level.storage.loot.providers.number.ConstantValue;
import net.minecraftforge.event.LootTableLoadEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.ForgeRegistries;
import org.slf4j.Logger;

import java.util.ArrayList;
import java.util.List;

/**
 * 按配置文件把带有 SA 的耀魂宝珠注入到结构宝箱的战利品表中。
 *
 * <p>条目格式见 {@link SlashExpansionConfig.Loot#entries()}：
 * {@code "战利品表ID|SA ID|概率"}。用户可以自由增删条目、调整结构、技能与概率。</p>
 */
@Mod.EventBusSubscriber(modid = SlashExpansion.MOD_ID)
public class LootInjector {

    private static final Logger LOGGER = LogUtils.getLogger();

    private static List<String> cachedRawEntries;
    private static List<ConfigLootEntry> cachedEntries = List.of();

    @SuppressWarnings("deprecation")
    @SubscribeEvent
    public static void onLootTableLoad(LootTableLoadEvent event) {
        if (!SlashExpansionConfig.Loot.enabled()) {
            return;
        }

        ResourceLocation tableId = event.getName();
        List<ConfigLootEntry> entries = entries();
        if (entries.isEmpty()) {
            return;
        }

        Item lootItem = resolveLootItem();
        for (ConfigLootEntry entry : entries) {
            if (!entry.tableId().equals(tableId)) {
                continue;
            }

            CompoundTag nbt = new CompoundTag();
            nbt.putString("SpecialAttackType", entry.saId().toString());

            LootPool pool = LootPool.lootPool()
                    .add(LootItem.lootTableItem(lootItem)
                            .apply(SetNbtFunction.setTag(nbt))
                            .when(LootItemRandomChanceCondition.randomChance(entry.chance()))
                            .setWeight(1)
                    )
                    .setRolls(ConstantValue.exactly(1))
                    .build();

            event.getTable().addPool(pool);
        }
    }

    private static Item resolveLootItem() {
        try {
            ResourceLocation id = ResourceLocation.parse(SlashExpansionConfig.Loot.itemId());
            Item item = ForgeRegistries.ITEMS.getValue(id);
            if (item != null && item != Items.AIR) {
                return item;
            }
            LOGGER.warn("[SlashExpansion] 配置中的战利品物品 {} 不存在，回退为耀魂宝珠", id);
        } catch (Exception e) {
            LOGGER.warn("[SlashExpansion] 配置中的战利品物品 ID 无法解析，回退为耀魂宝珠：{}", e.getMessage());
        }
        return SBItems.proudsoul_sphere;
    }

    /** 解析配置条目（按配置内容缓存，避免每次战利品表加载都重复解析）。 */
    private static synchronized List<ConfigLootEntry> entries() {
        List<String> raw = SlashExpansionConfig.Loot.entries();
        if (raw.equals(cachedRawEntries)) {
            return cachedEntries;
        }

        List<ConfigLootEntry> parsed = new ArrayList<>();
        for (String line : raw) {
            ConfigLootEntry entry = ConfigLootEntry.parse(line);
            if (entry != null) {
                parsed.add(entry);
            }
        }
        cachedRawEntries = raw;
        cachedEntries = List.copyOf(parsed);
        return cachedEntries;
    }

    /** 一条配置好的战利品条目。 */
    private record ConfigLootEntry(ResourceLocation tableId, ResourceLocation saId, float chance) {

        private static ConfigLootEntry parse(String raw) {
            String[] parts = raw.split("\\|");
            if (parts.length != 3) {
                LOGGER.warn("[SlashExpansion] 战利品条目格式错误（应为 \"战利品表|SA ID|概率\"），已忽略：{}", raw);
                return null;
            }
            try {
                ResourceLocation tableId = ResourceLocation.parse(parts[0].trim());
                ResourceLocation saId = ResourceLocation.parse(parts[1].trim());
                float chance = Float.parseFloat(parts[2].trim());
                if (chance < 0.0F || chance > 1.0F) {
                    LOGGER.warn("[SlashExpansion] 战利品概率必须在 0~1 之间，已忽略：{}", raw);
                    return null;
                }
                return new ConfigLootEntry(tableId, saId, chance);
            } catch (Exception e) {
                LOGGER.warn("[SlashExpansion] 战利品条目无法解析，已忽略：{}（{}）", raw, e.getMessage());
                return null;
            }
        }
    }
}
