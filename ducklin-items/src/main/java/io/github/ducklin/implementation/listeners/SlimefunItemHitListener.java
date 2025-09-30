package io.github.ducklin.implementation.listeners;

import javax.annotation.Nonnull;

import io.github.ducklin.core.Slimefun;
import io.github.ducklin.core.handlers.WeaponUseHandler;
import io.github.ducklin.migration.items.SlimefunItem;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.inventory.ItemStack;


public class SlimefunItemHitListener implements Listener {

    public SlimefunItemHitListener(@Nonnull Slimefun plugin) {
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onDamage(EntityDamageByEntityEvent e) {
        if (!(e.getDamager() instanceof Player)) {
            return;
        }

        Player p = (Player) e.getDamager();
        ItemStack item = p.getInventory().getItemInMainHand();

        if (!item.getType().isAir()) {
            SlimefunItem sfItem = SlimefunItem.getByItem(item);

            if (sfItem != null && sfItem.canUse(p, true)) {
                sfItem.callItemHandler(WeaponUseHandler.class, handler -> handler.onHit(e, p, item));
            }
        }
    }

}
