package io.github.ducklin.implementation.items.backpacks;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;

import io.github.ducklin.core.Slimefun;
import io.github.ducklin.core.handlers.ItemUseHandler;
import io.github.ducklin.migration.attributes.DistinctiveItem;
import io.github.ducklin.migration.items.ItemGroup;
import io.github.ducklin.migration.items.SlimefunItem;
import io.github.ducklin.migration.items.SlimefunItemStack;
import io.github.ducklin.migration.recipes.RecipeType;
import io.github.ducklin.migration.utils.SlimefunUtils;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;


import io.github.ducklin.implementation.items.SimpleSlimefunItem;
import io.github.ducklin.implementation.listeners.BackpackListener;
import io.github.ducklin.migration.utils.tags.SlimefunTag;


public class SlimefunBackpack extends SimpleSlimefunItem<ItemUseHandler> implements DistinctiveItem {

    private final int size;

    @ParametersAreNonnullByDefault
    public SlimefunBackpack(int size, ItemGroup itemGroup, SlimefunItemStack item, RecipeType recipeType, ItemStack[] recipe) {
        super(itemGroup, item, recipeType, recipe);

        this.size = size;
    }

    /**
     * This returns the size of this {@link SlimefunBackpack}.
     * 
     * @return The size of this backpack
     */
    public int getSize() {
        return size;
    }


    public boolean isItemAllowed(@Nonnull ItemStack item, @Nullable SlimefunItem itemAsSlimefunItem) {
        // Shulker Boxes are not allowed!
        if (SlimefunTag.SHULKER_BOXES.isTagged(item.getType())) {
            return false;
        }

        return !(itemAsSlimefunItem instanceof SlimefunBackpack);
    }

    @Override
    public ItemUseHandler getItemHandler() {
        return e -> {
            e.cancel();

            BackpackListener listener = Slimefun.getBackpackListener();

            if (listener != null) {
                listener.openBackpack(e.getPlayer(), e.getItem(), this);
            }
        };
    }

    @Override
    public boolean canStack(@Nonnull ItemMeta itemMetaOne, @Nonnull ItemMeta itemMetaTwo) {
        boolean hasLoreItem = itemMetaTwo.hasLore();
        boolean hasLoreSfItem = itemMetaOne.hasLore();

        if (hasLoreItem && hasLoreSfItem && SlimefunUtils.equalsLore(itemMetaTwo.getLore(), itemMetaOne.getLore())) {
            return true;
        }
        return !hasLoreItem && !hasLoreSfItem;
    }
}
