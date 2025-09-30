package io.github.ducklin.implementation.items.misc;

import javax.annotation.ParametersAreNonnullByDefault;

import io.github.ducklin.migration.items.ItemGroup;
import io.github.ducklin.migration.items.SlimefunItem;
import io.github.ducklin.migration.items.SlimefunItemStack;
import io.github.ducklin.migration.recipes.RecipeType;
import org.bukkit.entity.Villager;
import org.bukkit.inventory.ItemStack;


import io.github.ducklin.implementation.listeners.VillagerTradingListener;

/**
 * The {@link SyntheticEmerald} is an almost normal emerald.
 * It can even be used to trade with {@link Villager Villagers}.
 * 
 * @author TheBusyBiscuit
 * 
 * @see VillagerTradingListener
 *
 */
public class SyntheticEmerald extends SlimefunItem {

    @ParametersAreNonnullByDefault
    public SyntheticEmerald(ItemGroup itemGroup, SlimefunItemStack item, RecipeType recipeType, ItemStack[] recipe) {
        super(itemGroup, item, recipeType, recipe);

        setUseableInWorkbench(true);
    }

}
