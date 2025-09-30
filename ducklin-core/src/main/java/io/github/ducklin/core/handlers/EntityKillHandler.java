package io.github.ducklin.core.handlers;

import io.github.ducklin.migration.items.ItemHandler;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.inventory.ItemStack;


@FunctionalInterface
public interface EntityKillHandler extends ItemHandler {

    void onKill(EntityDeathEvent e, Entity entity, Player killer, ItemStack item);

    @Override
    default Class<? extends ItemHandler> getIdentifier() {
        return EntityKillHandler.class;
    }
}
