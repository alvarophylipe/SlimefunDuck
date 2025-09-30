package io.github.ducklin.implementation.items.tools;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;

import io.github.ducklin.core.handlers.ItemUseHandler;
import io.github.ducklin.core.services.sounds.SoundEffect;
import io.github.ducklin.migration.attributes.NotPlaceable;
import io.github.ducklin.migration.items.ItemGroup;
import io.github.ducklin.migration.items.SlimefunItemStack;
import io.github.ducklin.migration.recipes.RecipeType;
import org.bukkit.SoundCategory;
import org.bukkit.entity.Player;
import org.bukkit.inventory.CraftingInventory;
import org.bukkit.inventory.ItemStack;

import io.github.ducklin.implementation.items.SimpleSlimefunItem;

/**
 * The {@link PortableCrafter} is one of the oldest items in Slimefun.
 * It allows a {@link Player} to open up the {@link CraftingInventory} via right click.
 * 
 * @author TheBusyBiscuit
 *
 */
public class PortableCrafter extends SimpleSlimefunItem<ItemUseHandler> implements NotPlaceable {

    @ParametersAreNonnullByDefault
    public PortableCrafter(ItemGroup itemGroup, SlimefunItemStack item, RecipeType recipeType, ItemStack[] recipe) {
        super(itemGroup, item, recipeType, recipe);
    }

    @Override
    public @Nonnull ItemUseHandler getItemHandler() {
        return e -> {
            e.cancel();

            Player p = e.getPlayer();
            p.openWorkbench(p.getLocation(), true);
            SoundEffect.PORTABLE_CRAFTER_OPEN_SOUND.playAt(p.getLocation(), SoundCategory.PLAYERS);
        };
    }
}
