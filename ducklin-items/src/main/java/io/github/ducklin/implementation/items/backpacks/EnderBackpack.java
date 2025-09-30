package io.github.ducklin.implementation.items.backpacks;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;

import io.github.ducklin.core.handlers.ItemUseHandler;
import io.github.ducklin.core.services.sounds.SoundEffect;
import io.github.ducklin.migration.attributes.NotPlaceable;
import io.github.ducklin.migration.items.ItemGroup;
import io.github.ducklin.migration.items.SlimefunItemStack;
import io.github.ducklin.migration.recipes.RecipeType;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import io.github.ducklin.implementation.items.SimpleSlimefunItem;

public class EnderBackpack extends SimpleSlimefunItem<ItemUseHandler> implements NotPlaceable {

    @ParametersAreNonnullByDefault
    public EnderBackpack(ItemGroup itemGroup, SlimefunItemStack item, RecipeType recipeType, ItemStack[] recipe) {
        super(itemGroup, item, recipeType, recipe);
    }

    @Override
    public @Nonnull ItemUseHandler getItemHandler() {
        return e -> {
            Player p = e.getPlayer();
            p.openInventory(p.getEnderChest());
            SoundEffect.ENDER_BACKPACK_OPEN_SOUND.playFor(p);
            e.cancel();
        };
    }
}
