package io.github.ducklin.implementation.listeners;

import javax.annotation.Nonnull;

import io.github.ducklin.core.Slimefun;
import io.github.ducklin.migration.BlockStorage;
import io.github.ducklin.migration.attributes.NotHopperable;
import org.bukkit.Location;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryMoveItemEvent;
import org.bukkit.event.inventory.InventoryType;

public class HopperListener implements Listener {

    public HopperListener(@Nonnull Slimefun plugin) {
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }

    @EventHandler
    public void onHopperInsert(InventoryMoveItemEvent e) {
        Location loc = e.getDestination().getLocation();

        if (loc != null && e.getSource().getType() == InventoryType.HOPPER && BlockStorage.check(loc) instanceof NotHopperable) {
            e.setCancelled(true);
        }
    }
}
