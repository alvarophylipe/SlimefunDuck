package io.github.ducklin.core.handlers;

import io.github.ducklin.migration.items.ItemHandler;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.inventory.ItemStack;

@FunctionalInterface
public interface EntityInteractHandler extends ItemHandler {

    void onInteract(PlayerInteractEntityEvent e, ItemStack item, boolean offHand);

    @Override
    default Class<? extends ItemHandler> getIdentifier() {
        return EntityInteractHandler.class;
    }
}