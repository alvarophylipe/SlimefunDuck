package io.github.ducklin.implementation.listeners;

import javax.annotation.Nonnull;

import io.github.ducklin.core.Slimefun;
import io.github.ducklin.core.SlimefunRegistry;
import io.github.ducklin.core.handlers.ItemDropHandler;
import io.github.ducklin.migration.items.ItemHandler;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerDropItemEvent;

/**
 * Listens to the {@link PlayerDropItemEvent} to call any {@link ItemDropHandler}.
 *
 * @author TheBusyBiscuit
 * 
 * @see ItemDropHandler
 */
public class ItemDropListener implements Listener {

    public ItemDropListener(@Nonnull Slimefun plugin) {
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }

    @EventHandler
    public void onItemDrop(PlayerDropItemEvent e) {
        SlimefunRegistry registry = Slimefun.getRegistry();

        for (ItemHandler handler : registry.getGlobalItemHandlers(ItemDropHandler.class)) {
            if (((ItemDropHandler) handler).onItemDrop(e, e.getPlayer(), e.getItemDrop())) {
                return;
            }
        }
    }
}
