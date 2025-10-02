package io.github.ducklin.migration.exceptions;

import javax.annotation.ParametersAreNonnullByDefault;

import io.github.ducklin.api.items.DuckItem;
import org.bukkit.plugin.Plugin;

/**
 * An {@link UnregisteredItemException} is thrown whenever a {@link Plugin} tried to
 * access a method prematurely from {@link DuckItem} that can only be called after the
 * {@link DuckItem} was registered.
 * 
 * In other words... calling this method this early can not result in a logical output, making
 * this an {@link Exception}.
 * 
 * @author TheBusyBiscuit
 *
 */
public class UnregisteredItemException extends RuntimeException {

    private static final long serialVersionUID = -4684752240435069678L;

    /**
     * Constructs a new {@link UnregisteredItemException} with the given {@link DuckItem}
     * 
     * @param item
     *            The {@link DuckItem} that was affected by this
     */
    @ParametersAreNonnullByDefault
    public UnregisteredItemException(DuckItem item) {
        super(item.toString() + " has not been registered yet.");
    }

}
