package io.github.ducklin.implementation.listeners.entity;

import java.util.Set;
import java.util.concurrent.ThreadLocalRandom;

import javax.annotation.Nonnull;

import io.github.ducklin.core.Slimefun;
import io.github.ducklin.core.handlers.EntityKillHandler;
import io.github.ducklin.migration.attributes.RandomMobDrop;
import io.github.ducklin.migration.items.SlimefunItem;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.inventory.ItemStack;

import io.github.ducklin.implementation.items.misc.BasicCircuitBoard;

public class MobDropListener implements Listener {

    public MobDropListener(@Nonnull Slimefun plugin) {
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }

    @EventHandler
    public void onEntityKill(EntityDeathEvent e) {
        if (e.getEntity().getKiller() != null) {
            Player p = e.getEntity().getKiller();
            ItemStack item = p.getInventory().getItemInMainHand();

            Set<ItemStack> customDrops = Slimefun.getRegistry().getMobDrops().get(e.getEntityType());

            if (customDrops != null && !customDrops.isEmpty()) {
                for (ItemStack drop : customDrops) {
                    if (canDrop(p, drop)) {
                        e.getDrops().add(drop.clone());
                    }
                }
            }

            if (item.getType() != Material.AIR) {
                SlimefunItem sfItem = SlimefunItem.getByItem(item);

                if (sfItem != null && sfItem.canUse(p, true)) {
                    sfItem.callItemHandler(EntityKillHandler.class, handler -> handler.onKill(e, e.getEntity(), p, item));
                }
            }
        }
    }

    private boolean canDrop(@Nonnull Player p, @Nonnull ItemStack item) {
        SlimefunItem sfItem = SlimefunItem.getByItem(item);

        if (sfItem == null) {
            return true;
        } else if (sfItem.canUse(p, true)) {
            if (sfItem instanceof RandomMobDrop randomMobDrop) {
                int random = ThreadLocalRandom.current().nextInt(100);

                if (randomMobDrop.getMobDropChance() <= random) {
                    return false;
                }
            }

            if (sfItem instanceof BasicCircuitBoard basicCircuitBoard) {
                return basicCircuitBoard.isDroppedFromGolems();
            }

            return true;
        } else {
            return false;
        }
    }
}
