package io.github.ducklin.migration.events;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;

import io.github.ducklin.api.items.DuckItem;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.bukkit.inventory.ItemStack;

/**
 * This {@link Event} is fired whenever a {@link DuckItem} placed as a {@link Block} in the world is broken.
 * 
 * @author J3fftw1
 */
public class SlimefunBlockBreakEvent extends Event implements Cancellable {

    private static final HandlerList handlers = new HandlerList();

    private final Block blockBroken;
    private final DuckItem duckItem;
    private final ItemStack heldItem;
    private final Player player;

    private boolean cancelled = false;

    /**
     * @param player
     *        The {@link Player} who broke this {@link DuckItem}
     * @param heldItem
     *        The {@link ItemStack} held by the {@link Player}
     * @param blockBroken
     *        The {@link Block} broken by the {@link Player}
     * @param duckItem
     *        The {@link DuckItem} within the {@link ItemStack}
     */
    @ParametersAreNonnullByDefault
    public SlimefunBlockBreakEvent(Player player, ItemStack heldItem, Block blockBroken, DuckItem duckItem) {
        super();
        
        this.player = player;
        this.heldItem = heldItem;
        this.blockBroken = blockBroken;
        this.duckItem = duckItem;
    }

    /**
     * This gets the broken {@link Block}
     * 
     * @return The broken {@link Block}
     */
    public @Nonnull Block getBlockBroken() {
        return blockBroken;
    }

    /**
     * This gets the {@link DuckItem} being broken
     * 
     * @return The {@link DuckItem} being broken
     */
    public @Nonnull DuckItem getSlimefunItem() {
        return duckItem;
    }

    /**
     * The {@link ItemStack} held by the {@link Player}
     * 
     * @return The held {@link ItemStack}
     */
    public @Nonnull ItemStack getHeldItem() {
        return heldItem;
    }

    /**
     * This gets the {@link Player}
     * 
     * @return The {@link Player}
     */
    public @Nonnull Player getPlayer() {
        return player;
    }

    @Override
    public boolean isCancelled() {
        return cancelled;
    }

    @Override
    public void setCancelled(boolean cancelled) {
        this.cancelled = cancelled;
    }

    public static @Nonnull HandlerList getHandlerList() {
        return handlers;
    }

    @Override
    public @Nonnull HandlerList getHandlers() {
        return getHandlerList();
    }
}
