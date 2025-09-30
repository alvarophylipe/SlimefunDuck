package io.github.ducklin.implementation.items.food;

import javax.annotation.ParametersAreNonnullByDefault;

import io.github.ducklin.core.handlers.ItemConsumptionHandler;
import io.github.ducklin.migration.items.ItemGroup;
import io.github.ducklin.migration.items.ItemSetting;
import io.github.ducklin.migration.items.SlimefunItemStack;
import io.github.ducklin.migration.items.settings.IntRangeSetting;
import io.github.ducklin.migration.recipes.RecipeType;
import org.bukkit.inventory.ItemStack;

import io.github.ducklin.implementation.items.SimpleSlimefunItem;

/**
 * {@link MeatJerky} is just a piece of meat that gives some extra saturation.
 * {@link MeatJerky} is available for all meat variants.
 * 
 * @author TheBusyBiscuit
 * 
 * @see MonsterJerky
 *
 */
public class MeatJerky extends SimpleSlimefunItem<ItemConsumptionHandler> {

    private final ItemSetting<Integer> saturation = new IntRangeSetting(this, "saturation-level", 0, 6, Integer.MAX_VALUE);

    @ParametersAreNonnullByDefault
    public MeatJerky(ItemGroup itemGroup, SlimefunItemStack item, RecipeType recipeType, ItemStack[] recipe) {
        super(itemGroup, item, recipeType, recipe);

        addItemSetting(saturation);
    }

    @Override
    public ItemConsumptionHandler getItemHandler() {
        return (e, p, item) -> p.setSaturation(p.getSaturation() + saturation.getValue());
    }

}
