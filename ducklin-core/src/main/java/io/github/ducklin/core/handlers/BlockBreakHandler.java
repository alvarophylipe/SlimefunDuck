package io.github.ducklin.core.handlers;

import java.util.List;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;

import io.github.ducklin.migration.events.AndroidMineEvent;
import io.github.ducklin.migration.items.ItemHandler;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.inventory.ItemStack;

public abstract class BlockBreakHandler implements ItemHandler {


    private final boolean allowAndroids;

    private final boolean allowExplosions;

    protected BlockBreakHandler(boolean allowAndroids, boolean allowExplosions) {
        this.allowAndroids = allowAndroids;
        this.allowExplosions = allowExplosions;
    }

    @ParametersAreNonnullByDefault
    public abstract void onPlayerBreak(BlockBreakEvent e, ItemStack item, List<ItemStack> drops);

    @ParametersAreNonnullByDefault
    public void onExplode(Block b, List<ItemStack> drops) {
        // This can be overridden, if necessary
    }

    @ParametersAreNonnullByDefault
    public void onAndroidBreak(AndroidMineEvent e) {
        // This can be overridden, if necessary
    }

    /**
     * This returns whether an explosion is able to break the given {@link Block}.
     * 
     * @param b
     *            The {@link Block}
     * @return Whether explosions can destroy this {@link Block}
     */
    public boolean isExplosionAllowed(@Nonnull Block b) {
        /*
         * By default our flag is returned, but you can override it
         * to be handled on a per-Block basis.
         */
        return allowExplosions;
    }

    public boolean isAndroidAllowed(@Nonnull Block b) {

        return allowAndroids;
    }

    @Override
    public final Class<? extends ItemHandler> getIdentifier() {
        return BlockBreakHandler.class;
    }
}
