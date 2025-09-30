package io.github.ducklin.core.handlers;

import java.util.Optional;

import io.github.ducklin.migration.exceptions.IncompatibleItemHandlerException;
import io.github.ducklin.migration.items.ItemHandler;
import io.github.ducklin.migration.items.SlimefunItem;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.Dispenser;
import org.bukkit.event.block.BlockDispenseEvent;


import io.github.ducklin.migration.attributes.NotPlaceable;


@FunctionalInterface
public interface BlockDispenseHandler extends ItemHandler {

    @Override
    default Optional<IncompatibleItemHandlerException> validate(SlimefunItem item) {
        if (item instanceof NotPlaceable || item.getItem().getType() != Material.DISPENSER) {
            return Optional.of(new IncompatibleItemHandlerException("Only dispensers that are not marked as 'NotPlaceable' can have a BlockDispenseHandler.", item, this));
        }

        return Optional.empty();
    }

    void onBlockDispense(BlockDispenseEvent e, Dispenser dispenser, Block facedBlock, SlimefunItem machine);

    @Override
    default Class<? extends ItemHandler> getIdentifier() {
        return BlockDispenseHandler.class;
    }
}
