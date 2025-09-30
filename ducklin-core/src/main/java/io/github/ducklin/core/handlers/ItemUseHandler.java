package io.github.ducklin.core.handlers;

import io.github.ducklin.migration.events.PlayerRightClickEvent;
import io.github.ducklin.migration.items.ItemHandler;
import org.bukkit.entity.Player;


@FunctionalInterface
public interface ItemUseHandler extends ItemHandler {

    void onRightClick(PlayerRightClickEvent e);

    @Override
    default Class<? extends ItemHandler> getIdentifier() {
        return ItemUseHandler.class;
    }

}
