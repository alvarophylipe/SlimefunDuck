package io.github.ducklin.migration.items;

import java.util.Optional;

import javax.annotation.Nonnull;

import io.github.ducklin.api.items.DuckItem;
import io.github.ducklin.migration.exceptions.IncompatibleItemHandlerException;

/**
 * An {@link ItemHandler} represents a certain action that a {@link DuckItem}
 * can perform.
 * 
 * @author TheBusyBiscuit
 *
 * @see ItemUseHandler
 * @see ItemConsumptionHandler
 * @see BlockUseHandler
 * @see EntityKillHandler
 * @see EntityInteractHandler
 * @see BowShootHandler
 */
@FunctionalInterface
public interface ItemHandler {

    /**
     * This method is used to check whether a given {@link DuckItem} is compatible
     * with this {@link ItemHandler}, it will return an {@link IncompatibleItemHandlerException}
     * if the items are not compatible.
     * 
     * @param item
     *            The {@link DuckItem} to validate
     * 
     * @return An {@link Optional} describing the result, it will contain an {@link IncompatibleItemHandlerException}
     *         should there be an issue
     */
    @Nonnull
    default Optional<IncompatibleItemHandlerException> validate(@Nonnull DuckItem item) {
        return Optional.empty();
    }

    /**
     * This method returns the identifier for this {@link ItemHandler}.
     * We use a {@link Class} identifier to group Item Handlers together.
     * 
     * @return The {@link Class} identifier for this {@link ItemHandler}
     */
    @Nonnull
    Class<? extends ItemHandler> getIdentifier();
}
