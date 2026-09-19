package com.lincstoel.hotpeppers;

import net.minecraft.core.HolderLookup;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingBookCategory;
import net.minecraft.world.item.crafting.CraftingInput;
import net.minecraft.world.item.crafting.CustomRecipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.level.Level;

/**
 * 1 food item + 1-3 hot peppers -> the same food, spicier. A dynamic/"special" recipe rather than a
 * shaped one because the ingredient (any food) and the result (that food, renamed) aren't fixed items.
 */
public class SpicyFoodRecipe extends CustomRecipe {

    private static final int MAX_RANK = 3;

    public SpicyFoodRecipe(CraftingBookCategory category) {
        super(category);
    }

    @Override
    public boolean matches(CraftingInput input, Level level) {
        ItemStack food = ItemStack.EMPTY;
        int pepperCount = 0;

        for (ItemStack stack : input.items()) {
            if (stack.isEmpty()) {
                continue;
            }
            if (stack.is(HotPeppers.HOT_PEPPER)) {
                pepperCount += stack.getCount();
            } else if (stack.has(DataComponents.FOOD)) {
                if (!food.isEmpty() || stack.getCount() != 1) {
                    return false;
                }
                food = stack;
            } else {
                return false;
            }
        }

        if (food.isEmpty() || pepperCount == 0 || pepperCount > MAX_RANK) {
            return false;
        }
        return Spice.getSpiceLevel(food) + pepperCount <= MAX_RANK;
    }

    @Override
    public ItemStack assemble(CraftingInput input, HolderLookup.Provider registries) {
        ItemStack food = ItemStack.EMPTY;
        int pepperCount = 0;

        for (ItemStack stack : input.items()) {
            if (stack.is(HotPeppers.HOT_PEPPER)) {
                pepperCount += stack.getCount();
            } else if (!stack.isEmpty()) {
                food = stack;
            }
        }

        int rank = Spice.getSpiceLevel(food) + pepperCount;
        // Derived from the default stack, never the (possibly already-prefixed) input, so re-crafting
        // never compounds the name into "Spicy Spicy Bread".
        Component baseName = new ItemStack(food.getItem()).getHoverName();

        ItemStack result = Spice.withSpiceLevel(food.copyWithCount(1), rank);
        result.set(DataComponents.ITEM_NAME, Component.translatable("item.hot_peppers.spicy_prefix." + rank, baseName));
        return result;
    }

    @Override
    public boolean canCraftInDimensions(int width, int height) {
        return width * height >= 2;
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return HotPeppers.SPICY_FOOD.get();
    }
}
