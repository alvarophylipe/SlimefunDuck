package io.github.ducklin.core.handlers;

import io.github.ducklin.migration.items.ItemHandler;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.bukkit.inventory.ItemStack;


@FunctionalInterface
public interface ItemConsumptionHandler extends ItemHandler {

    void onConsume(PlayerItemConsumeEvent e, Player p, ItemStack item);

    @Override
    default Class<? extends ItemHandler> getIdentifier() {
        return ItemConsumptionHandler.class;
    }

}
