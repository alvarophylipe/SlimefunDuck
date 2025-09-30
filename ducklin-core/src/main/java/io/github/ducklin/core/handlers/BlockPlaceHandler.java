package io.github.ducklin.core.handlers;

import javax.annotation.Nonnull;

import io.github.ducklin.migration.events.BlockPlacerPlaceEvent;
import io.github.ducklin.migration.items.ItemHandler;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.block.BlockPlaceEvent;

public abstract class BlockPlaceHandler implements ItemHandler {

    private final boolean allowBlockPlacers;

    protected BlockPlaceHandler(boolean allowBlockPlacers) {
        this.allowBlockPlacers = allowBlockPlacers;
    }

    /**
     * This method is called whenever a {@link Player} placed this {@link Block}.
     * 
     * @param e
     *            The corresponding {@link BlockPlaceEvent}
     */
    public abstract void onPlayerPlace(@Nonnull BlockPlaceEvent e);

    public void onBlockPlacerPlace(@Nonnull BlockPlacerPlaceEvent e) {
        // This can be overridden, if necessary
    }

    public boolean isBlockPlacerAllowed() {
        return allowBlockPlacers;
    }

    @Override
    public final Class<? extends ItemHandler> getIdentifier() {
        return BlockPlaceHandler.class;
    }
}
