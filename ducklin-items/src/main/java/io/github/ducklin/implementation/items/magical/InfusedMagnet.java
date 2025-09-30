package io.github.ducklin.implementation.items.magical;

import javax.annotation.ParametersAreNonnullByDefault;

import io.github.ducklin.migration.items.ItemGroup;
import io.github.ducklin.migration.items.ItemSetting;
import io.github.ducklin.migration.items.SlimefunItemStack;
import io.github.ducklin.migration.items.settings.DoubleRangeSetting;
import io.github.ducklin.migration.recipes.RecipeType;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import io.github.ducklin.implementation.items.blocks.UnplaceableBlock;


public class InfusedMagnet extends UnplaceableBlock {

    private final ItemSetting<Double> radius = new DoubleRangeSetting(this, "pickup-radius", 0.1, 6.0, Double.MAX_VALUE);

    @ParametersAreNonnullByDefault
    public InfusedMagnet(ItemGroup itemGroup, SlimefunItemStack item, RecipeType recipeType, ItemStack[] recipe) {
        super(itemGroup, item, recipeType, recipe);

        addItemSetting(radius);
    }

    /**
     * This returns the radius in which items are picked up
     * 
     * @return The radius of the {@link InfusedMagnet}
     */
    public double getRadius() {
        return radius.getValue();
    }

}
