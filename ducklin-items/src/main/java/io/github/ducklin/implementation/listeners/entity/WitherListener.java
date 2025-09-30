package io.github.ducklin.implementation.listeners.entity;

import javax.annotation.Nonnull;

import io.github.ducklin.core.Slimefun;
import io.github.ducklin.implementation.SlimefunItems;
import io.github.ducklin.migration.BlockStorage;
import io.github.ducklin.migration.attributes.WitherProof;
import io.github.ducklin.migration.items.SlimefunItem;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Wither;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityChangeBlockEvent;

/**
 * This {@link Listener} is responsible for implementing the functionality of blocks that
 * were marked as {@link WitherProof} to not be destroyed by a {@link Wither}.
 * 
 * @author TheBusyBiscuit
 * 
 * @see WitherProof
 *
 */
public class WitherListener implements Listener {

    public WitherListener(@Nonnull Slimefun plugin) {
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }

    @EventHandler(ignoreCancelled = true)
    public void onWitherDestroy(EntityChangeBlockEvent e) {
        if (e.getEntity().getType() == EntityType.WITHER) {
            SlimefunItem item = BlockStorage.check(e.getBlock());

            // Hardened Glass is excluded from here
            if (item instanceof WitherProof witherProofBlock && !item.getId().equals(SlimefunItems.HARDENED_GLASS.getItemId())) {
                e.setCancelled(true);
                witherProofBlock.onAttack(e.getBlock(), (Wither) e.getEntity());
            }
        }
    }

}
