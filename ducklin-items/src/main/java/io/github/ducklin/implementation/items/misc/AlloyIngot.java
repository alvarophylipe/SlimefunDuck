package io.github.ducklin.implementation.items.misc;

import javax.annotation.ParametersAreNonnullByDefault;

import io.github.ducklin.migration.items.ItemGroup;
import io.github.ducklin.migration.items.SlimefunItem;
import io.github.ducklin.migration.items.SlimefunItemStack;
import io.github.ducklin.migration.recipes.RecipeType;
import org.bukkit.inventory.ItemStack;

import io.github.ducklin.implementation.items.multiblocks.Smeltery;

/**
 * An {@link AlloyIngot} is a blend of different metals and resources.
 * These ingots can be crafted using a {@link Smeltery}.
 * 
 * @author TheBusyBiscuit
 * 
 * @see Smeltery
 *
 */
public class AlloyIngot extends SlimefunItem {

    @ParametersAreNonnullByDefault
    public AlloyIngot(ItemGroup itemGroup, SlimefunItemStack item, ItemStack[] recipe) {
        super(itemGroup, item, RecipeType.SMELTERY, recipe);
    }

}
