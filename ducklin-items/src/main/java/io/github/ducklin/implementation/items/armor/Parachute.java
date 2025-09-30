package io.github.ducklin.implementation.items.armor;

import javax.annotation.ParametersAreNonnullByDefault;

import io.github.ducklin.migration.items.ItemGroup;
import io.github.ducklin.migration.items.SlimefunItem;
import io.github.ducklin.migration.items.SlimefunItemStack;
import io.github.ducklin.migration.recipes.RecipeType;
import org.bukkit.inventory.ItemStack;

import io.github.ducklin.implementation.items.electric.gadgets.Jetpack;
import io.github.ducklin.migration.tasks.player.ParachuteTask;

/**
 * The {@link Parachute} is a {@link SlimefunItem} that can be equipped as a chestplate.
 * It allows you slowly glide to the ground while holding shift.
 * 
 * This class does not contain much code to see, check our the {@link ParachuteTask} class
 * for the actual logic behind this.
 * 
 * @author TheBusyBiscuit
 * 
 * @see ParachuteTask
 * @see Jetpack
 *
 */
public class Parachute extends SlimefunItem {

    @ParametersAreNonnullByDefault
    public Parachute(ItemGroup itemGroup, SlimefunItemStack item, RecipeType recipeType, ItemStack[] recipe) {
        super(itemGroup, item, recipeType, recipe);
    }

}
