package io.github.ducklin.implementation.items.tools;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;

import io.github.ducklin.core.handlers.ItemUseHandler;
import io.github.ducklin.core.services.sounds.SoundEffect;
import io.github.ducklin.migration.attributes.NotPlaceable;
import io.github.ducklin.migration.items.ItemGroup;
import io.github.ducklin.migration.items.SlimefunItemStack;
import io.github.ducklin.migration.recipes.RecipeType;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import io.github.ducklin.implementation.items.SimpleSlimefunItem;

/**
 * The {@link PortableDustbin} is one of the oldest items in Slimefun.
 * It simply opens an empty {@link Inventory} in which you can dump any
 * unwanted {@link ItemStack}. When closing the {@link Inventory}, all items
 * will be voided.
 * 
 * @author TheBusyBiscuit
 */
public class PortableDustbin extends SimpleSlimefunItem<ItemUseHandler> implements NotPlaceable {

    @ParametersAreNonnullByDefault
    public PortableDustbin(ItemGroup itemGroup, SlimefunItemStack item, RecipeType recipeType, ItemStack[] recipe) {
        super(itemGroup, item, recipeType, recipe);
    }

    @Override
    public @Nonnull ItemUseHandler getItemHandler() {
        return e -> {
            e.cancel();

            Player p = e.getPlayer();
            p.openInventory(Bukkit.createInventory(null, 9 * 3, ChatColor.DARK_RED + "Delete Items"));
            SoundEffect.PORTABLE_DUSTBIN_OPEN_SOUND.playFor(p);
        };
    }
}
