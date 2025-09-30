package io.github.ducklin.core.handlers;

import java.util.Optional;

import io.github.ducklin.migration.exceptions.IncompatibleItemHandlerException;
import io.github.ducklin.migration.items.ItemHandler;
import io.github.ducklin.migration.items.SlimefunItem;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;

import io.github.ducklin.core.multiblocks.MultiBlock;
import io.github.ducklin.core.multiblocks.MultiBlockMachine;

/**
 * This {@link ItemHandler} is called whenever a {@link Player} interacts with
 * this {@link MultiBlock}.
 * Note that this {@link MultiBlockInteractionHandler} should be assigned to
 * a class that inherits from {@link MultiBlockMachine}.
 * 
 * @author TheBusyBiscuit
 * 
 * @see ItemHandler
 * @see MultiBlock
 * @see MultiBlockMachine
 *
 */
@FunctionalInterface
public interface MultiBlockInteractionHandler extends ItemHandler {

    boolean onInteract(Player p, MultiBlock mb, Block b);

    @Override
    default Optional<IncompatibleItemHandlerException> validate(SlimefunItem item) {
        if (!(item instanceof MultiBlockMachine)) {
            return Optional.of(new IncompatibleItemHandlerException("Only classes inheriting 'MultiBlockMachine' can have a MultiBlockInteractionHandler", item, this));
        }

        return Optional.empty();
    }

    @Override
    default Class<? extends ItemHandler> getIdentifier() {
        return MultiBlockInteractionHandler.class;
    }
}
