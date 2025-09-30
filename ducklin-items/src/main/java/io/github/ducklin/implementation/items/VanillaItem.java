package io.github.ducklin.implementation.items;

import javax.annotation.ParametersAreNonnullByDefault;

import io.github.ducklin.migration.items.ItemGroup;
import io.github.ducklin.migration.items.SlimefunItem;
import io.github.ducklin.migration.recipes.RecipeType;
import org.bukkit.inventory.ItemStack;



public class VanillaItem extends SlimefunItem {


    @ParametersAreNonnullByDefault
    public VanillaItem(ItemGroup itemGroup, ItemStack item, String id, RecipeType recipeType, ItemStack[] recipe) {
        super(itemGroup, item, id, recipeType, recipe);

        setUseableInWorkbench(true);
    }
}
