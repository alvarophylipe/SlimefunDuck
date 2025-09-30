package io.github.ducklin.implementation.items.weapons;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;

import io.github.ducklin.core.handlers.BowShootHandler;
import io.github.ducklin.migration.items.ItemGroup;
import io.github.ducklin.migration.items.SlimefunItem;
import io.github.ducklin.migration.items.SlimefunItemStack;
import io.github.ducklin.migration.recipes.RecipeType;
import org.bukkit.entity.Arrow;
import org.bukkit.inventory.ItemStack;

public abstract class SlimefunBow extends SlimefunItem {

    @ParametersAreNonnullByDefault
    protected SlimefunBow(ItemGroup itemGroup, SlimefunItemStack item, ItemStack[] recipe) {
        super(itemGroup, item, RecipeType.MAGIC_WORKBENCH, recipe);
    }

    @Override
    public void preRegister() {
        super.preRegister();
        addItemHandler(onShoot());
    }

    @Nonnull
    public abstract BowShootHandler onShoot();

}
