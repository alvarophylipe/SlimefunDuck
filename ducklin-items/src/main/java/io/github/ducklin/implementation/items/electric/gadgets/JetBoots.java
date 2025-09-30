package io.github.ducklin.implementation.items.electric.gadgets;

import javax.annotation.ParametersAreNonnullByDefault;

import io.github.ducklin.migration.attributes.Rechargeable;
import io.github.ducklin.migration.items.ItemGroup;
import io.github.ducklin.migration.items.SlimefunItem;
import io.github.ducklin.migration.items.SlimefunItemStack;
import io.github.ducklin.migration.recipes.RecipeType;
import org.bukkit.inventory.ItemStack;

import io.github.ducklin.migration.tasks.player.JetBootsTask;

/**
 * {@link JetBoots} allow you to hover for a bit.
 * You can find the actual behaviour in the {@link JetBootsTask} class.
 * 
 * @author TheBusyBiscuit
 * 
 * @see Jetpack
 * @see JetBootsTask
 *
 */
public class JetBoots extends SlimefunItem implements Rechargeable {

    private final double speed;
    private final float capacity;

    @ParametersAreNonnullByDefault
    public JetBoots(ItemGroup itemGroup, SlimefunItemStack item, ItemStack[] recipe, double speed, float capacity) {
        super(itemGroup, item, RecipeType.ENHANCED_CRAFTING_TABLE, recipe);

        this.speed = speed;
        this.capacity = capacity;
    }

    public double getSpeed() {
        return speed;
    }

    @Override
    public float getMaxItemCharge(ItemStack item) {
        return capacity;
    }

}
