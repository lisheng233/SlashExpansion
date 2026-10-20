package com.lisheng.slashexpansion.config;

import com.lisheng.slashexpansion.SlashExpansion;
import net.minecraft.core.NonNullList;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.PreparableReloadListener;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.util.Unit;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.CraftingBookCategory;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeManager;
import net.minecraft.world.item.crafting.ShapedRecipe;
import net.minecraft.world.item.crafting.ShapelessRecipe;
import net.minecraftforge.event.AddReloadListenerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.ForgeRegistries;
import org.slf4j.Logger;
import com.mojang.logging.LogUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

/**
 * 按配置文件动态生成 SE 合成配方。
 *
 * <p>实现方式：通过 {@link AddReloadListenerEvent} 在服务端资源重载时追加一个监听器。
 * 该监听器的 {@code apply} 阶段晚于原版 {@link RecipeManager}，因此可以拿到已经加载完成的
 * 配方集合，再调用 {@link RecipeManager#replaceRecipes(Iterable)} 用配置生成的配方替换内置配方。</p>
 *
 * <p>这样既不需要反射，也能在 {@code /reload} 后立即生效。</p>
 */
@Mod.EventBusSubscriber(modid = SlashExpansion.MOD_ID)
public final class ConfigRecipeInjector {

    private static final Logger LOGGER = LogUtils.getLogger();
    private static final String GROUP = "slashexpansion";
    private static final String EFFECT_NBT_KEY = "SpecialEffectType";

    private ConfigRecipeInjector() {
    }

    @SubscribeEvent
    public static void onAddReloadListener(AddReloadListenerEvent event) {
        RecipeManager recipeManager = event.getServerResources().getRecipeManager();
        event.addListener(new RecipeOverrideListener(recipeManager));
    }

    /** 读取配置并覆盖配方。 */
    private static void overrideRecipes(RecipeManager recipeManager) {
        try {
            if (!SlashExpansionConfig.Recipes.overrideJsonRecipes()) {
                return;
            }

            Set<ResourceLocation> managedIds = managedIds();
            List<Recipe<?>> recipes = new ArrayList<>(recipeManager.getRecipes());
            recipes.removeIf(recipe -> managedIds.contains(recipe.getId()));

            int injected = 0;
            for (String effectId : SlashExpansionConfig.Recipes.EFFECT_IDS) {
                SlashExpansionConfig.RecipeEntry entry = SlashExpansionConfig.RecipeEntries.byEffectId(effectId);
                if (entry == null || !entry.enabled()) {
                    continue;
                }
                try {
                    recipes.add(buildRecipe(effectId, entry));
                    injected++;
                } catch (Exception e) {
                    LOGGER.error("[SlashExpansion] SE 配方 {} 配置有误，已跳过：{}", effectId, e.getMessage());
                }
            }

            recipeManager.replaceRecipes(recipes);
            LOGGER.debug("[SlashExpansion] 已按配置注入 {} 个 SE 合成配方", injected);
        } catch (Exception e) {
            LOGGER.error("[SlashExpansion] 注入 SE 合成配方失败，本次保持原有配方", e);
        }
    }

    /** 由本模组接管的配方 ID 集合（无论启用与否都会先移除，再按配置重新添加）。 */
    private static Set<ResourceLocation> managedIds() {
        Set<ResourceLocation> ids = new HashSet<>();
        for (String effectId : SlashExpansionConfig.Recipes.EFFECT_IDS) {
            ids.add(ResourceLocation.parse(SlashExpansionConfig.Recipes.recipeIdOf(effectId)));
        }
        return ids;
    }

    private static Recipe<?> buildRecipe(String effectId, SlashExpansionConfig.RecipeEntry entry) {
        ResourceLocation id = ResourceLocation.parse(SlashExpansionConfig.Recipes.recipeIdOf(effectId));
        ItemStack result = buildResult(entry.resultItem(), effectId);

        if (entry.shapeless()) {
            return buildShapeless(id, result, entry.ingredients());
        }
        return buildShaped(id, result, entry.pattern(), entry.keys());
    }

    private static ItemStack buildResult(String itemId, String effectId) {
        ItemStack stack = new ItemStack(itemOf(itemId));
        stack.getOrCreateTag().putString(EFFECT_NBT_KEY, SlashExpansion.MOD_ID + ":" + effectId);
        return stack;
    }

    private static Recipe<?> buildShapeless(ResourceLocation id, ItemStack result, List<String> ingredients) {
        if (ingredients.isEmpty()) {
            throw new IllegalArgumentException("shapeless = true 但没有填写 ingredients");
        }
        NonNullList<Ingredient> items = NonNullList.create();
        for (String ingredientId : ingredients) {
            items.add(Ingredient.of(new ItemStack(itemOf(ingredientId))));
        }
        return new ShapelessRecipe(id, GROUP, CraftingBookCategory.MISC, result, items);
    }

    private static Recipe<?> buildShaped(ResourceLocation id, ItemStack result,
                                         List<String> pattern, List<String> keys) {
        if (pattern.isEmpty()) {
            throw new IllegalArgumentException("缺少 pattern");
        }

        int height = pattern.size();
        int width = pattern.get(0).length();
        if (height < 1 || height > 3 || width < 1 || width > 3) {
            throw new IllegalArgumentException("pattern 只能是 1~3 行、每行 1~3 个字符");
        }

        Map<Character, Ingredient> keyMap = parseKeys(keys);
        NonNullList<Ingredient> items = NonNullList.withSize(width * height, Ingredient.EMPTY);

        for (int y = 0; y < height; y++) {
            String row = pattern.get(y);
            if (row.length() != width) {
                throw new IllegalArgumentException("pattern 第 " + (y + 1) + " 行长度与第 1 行不一致");
            }
            for (int x = 0; x < width; x++) {
                char key = row.charAt(x);
                if (key == ' ') {
                    continue;
                }
                Ingredient ingredient = keyMap.get(key);
                if (ingredient == null) {
                    throw new IllegalArgumentException("pattern 使用了未在 keys 中定义的字符 '" + key + "'");
                }
                items.set(x + y * width, ingredient);
            }
        }

        return new ShapedRecipe(id, GROUP, CraftingBookCategory.MISC, width, height, items, result);
    }

    private static Map<Character, Ingredient> parseKeys(List<String> keys) {
        Map<Character, Ingredient> keyMap = new HashMap<>();
        for (String entry : keys) {
            int split = entry.indexOf('=');
            if (split != 1 || entry.length() <= 2) {
                throw new IllegalArgumentException("keys 条目格式应为 \"字符=物品ID\"，实际为：" + entry);
            }
            char key = entry.charAt(0);
            keyMap.put(key, Ingredient.of(new ItemStack(itemOf(entry.substring(split + 1).trim()))));
        }
        return keyMap;
    }

    private static Item itemOf(String itemId) {
        ResourceLocation id = ResourceLocation.parse(itemId);
        Item item = ForgeRegistries.ITEMS.getValue(id);
        if (item == null || item == Items.AIR) {
            throw new IllegalArgumentException("未知物品 ID：" + itemId);
        }
        return item;
    }

    /** 在资源重载链中晚于原版配方管理器执行，用于覆盖配方。 */
    private static final class RecipeOverrideListener implements PreparableReloadListener {

        private final RecipeManager recipeManager;

        private RecipeOverrideListener(RecipeManager recipeManager) {
            this.recipeManager = recipeManager;
        }

        @Override
        public CompletableFuture<Void> reload(PreparationBarrier barrier, ResourceManager resourceManager,
                                              ProfilerFiller preparationsProfiler, ProfilerFiller reloadProfiler,
                                              Executor backgroundExecutor, Executor gameExecutor) {
            return barrier.wait(Unit.INSTANCE)
                    .thenAcceptAsync(ignored -> overrideRecipes(recipeManager), gameExecutor);
        }
    }
}
