package io.github.ducklin.implementation.items.magical;

import javax.annotation.ParametersAreNonnullByDefault;

import io.github.ducklin.migration.attributes.NotPlaceable;
import io.github.ducklin.migration.attributes.Soulbound;
import io.github.ducklin.migration.items.ItemGroup;
import io.github.ducklin.migration.items.SlimefunItem;
import io.github.ducklin.migration.items.SlimefunItemStack;
import io.github.ducklin.migration.recipes.RecipeType;
import org.bukkit.inventory.ItemStack;

import io.github.ducklin.implementation.items.magical.runes.SoulboundRune;
import io.github.ducklin.implementation.listeners.SoulboundListener;

/**
 * Represents an Item that will not drop upon death.
 * 
 * @author TheBusyBiscuit
 * 
 * @see Soulbound
 * @see SoulboundRune
 * @see SoulboundListener
 *
 */
public class SoulboundItem extends SlimefunItem implements Soulbound, NotPlaceable {

    @ParametersAreNonnullByDefault
    public SoulboundItem(ItemGroup itemGroup, SlimefunItemStack item, RecipeType type, ItemStack[] recipe) {
        super(itemGroup, item, type, recipe);
    }

}
