package io.github.ducklin.implementation.items;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;

import io.github.ducklin.migration.items.ItemGroup;
import io.github.ducklin.migration.items.ItemHandler;
import io.github.ducklin.migration.items.SlimefunItem;
import io.github.ducklin.migration.items.SlimefunItemStack;
import io.github.ducklin.migration.recipes.RecipeType;
import org.bukkit.inventory.ItemStack;


public abstract class SimpleSlimefunItem<T extends ItemHandler> extends SlimefunItem {

    @ParametersAreNonnullByDefault
    protected SimpleSlimefunItem(ItemGroup itemGroup, SlimefunItemStack item, RecipeType recipeType, ItemStack[] recipe) {
        super(itemGroup, item, recipeType, recipe);
    }

    @ParametersAreNonnullByDefault
    protected SimpleSlimefunItem(ItemGroup itemGroup, SlimefunItemStack item, RecipeType recipeType, ItemStack[] recipe, @Nullable ItemStack recipeOutput) {
        super(itemGroup, item, recipeType, recipe, recipeOutput);
    }

    @Override
    public void preRegister() {
        addItemHandler(getItemHandler());
    }

    /**
     * This returns the {@link ItemHandler} that will be added to this {@link SlimefunItem}.
     * 
     * @return The {@link ItemHandler} that should be added to this {@link SlimefunItem}
     */
    public abstract @Nonnull T getItemHandler();

}
