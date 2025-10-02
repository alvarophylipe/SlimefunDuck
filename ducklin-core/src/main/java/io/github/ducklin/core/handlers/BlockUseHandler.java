package io.github.ducklin.core.handlers;

import java.util.Optional;

import io.github.ducklin.migration.events.PlayerRightClickEvent;
import io.github.ducklin.migration.exceptions.IncompatibleItemHandlerException;
import io.github.ducklin.migration.attributes.NotPlaceable;
import io.github.ducklin.migration.items.ItemHandler;
import io.github.ducklin.api.items.DuckItem;

@FunctionalInterface
public interface BlockUseHandler extends ItemHandler {

    void onRightClick(PlayerRightClickEvent e);

    @Override
    default Optional<IncompatibleItemHandlerException> validate(DuckItem item) {
        if (item instanceof NotPlaceable || !item.getItem().getType().isBlock()) {
            return Optional.of(new IncompatibleItemHandlerException("Only blocks that are not marked as 'NotPlaceable' can have a BlockUseHandler.", item, this));
        }

        return Optional.empty();
    }

    @Override
    default Class<? extends ItemHandler> getIdentifier() {
        return BlockUseHandler.class;
    }

}
