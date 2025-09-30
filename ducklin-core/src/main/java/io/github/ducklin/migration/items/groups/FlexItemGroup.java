package io.github.ducklin.migration.items.groups;

import java.util.List;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;

import io.github.ducklin.migration.guide.SlimefunGuideMode;
import io.github.ducklin.migration.items.SlimefunItem;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import io.github.ducklin.migration.items.ItemGroup;
import io.github.ducklin.migration.player.PlayerProfile;


public abstract class FlexItemGroup extends ItemGroup {

    @ParametersAreNonnullByDefault
    protected FlexItemGroup(NamespacedKey key, ItemStack item) {
        this(key, item, 3);
    }

    @ParametersAreNonnullByDefault
    protected FlexItemGroup(NamespacedKey key, ItemStack item, int tier) {
        super(key, item, tier);
    }

    @Override
    public final boolean isVisible(@Nonnull Player p) {
        /*
         * We can stop this method right here.
         * We provide a custom method with more parameters for this.
         * See isVisible(...)
         */
        return true;
    }


    @ParametersAreNonnullByDefault
    public abstract boolean isVisible(Player p, PlayerProfile profile, SlimefunGuideMode layout);

    public abstract void open(Player p, PlayerProfile profile, SlimefunGuideMode layout);

    @Override
    public final void add(@Nonnull SlimefunItem item) {
        throw new UnsupportedOperationException("You cannot add items to a FlexItemGroup!");
    }

    @Override
    public final @Nonnull List<SlimefunItem> getItems() {
        throw new UnsupportedOperationException("A FlexItemGroup has no items!");
    }

    @Override
    public final boolean contains(SlimefunItem item) {
        throw new UnsupportedOperationException("A FlexItemGroup has no items!");
    }

    @Override
    public final void remove(@Nonnull SlimefunItem item) {
        throw new UnsupportedOperationException("A FlexItemGroup has no items, so there is nothing remove!");
    }

}
