package io.github.ducklin.implementation.items.electric.gadgets;

import javax.annotation.ParametersAreNonnullByDefault;

import io.github.ducklin.migration.attributes.Rechargeable;
import io.github.ducklin.migration.items.ItemGroup;
import io.github.ducklin.migration.items.SlimefunItem;
import io.github.ducklin.migration.items.SlimefunItemStack;
import io.github.ducklin.migration.recipes.RecipeType;
import org.bukkit.inventory.ItemStack;

import io.github.ducklin.migration.tasks.player.JetpackTask;

/**
 * {@link JetBoots} allow you to fly up into the air.
 * You can find the actual behaviour in the {@link JetpackTask} class.
 * 
 * @author TheBusyBiscuit
 * 
 * @see JetBoots
 * @see JetpackTask
 *
 */
public class Jetpack extends SlimefunItem implements Rechargeable {

    private final double thrust;
    private final float capacity;

    @ParametersAreNonnullByDefault
    public Jetpack(ItemGroup itemGroup, SlimefunItemStack item, ItemStack[] recipe, double thrust, float capacity) {
        super(itemGroup, item, RecipeType.ENHANCED_CRAFTING_TABLE, recipe);

        this.thrust = thrust;
        this.capacity = capacity;
    }

    public double getThrust() {
        return thrust;
    }

    @Override
    public float getMaxItemCharge(ItemStack item) {
        return capacity;
    }
}
