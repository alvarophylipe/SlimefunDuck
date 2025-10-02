package io.github.ducklin.migration.exceptions;

import javax.annotation.ParametersAreNonnullByDefault;

import io.github.ducklin.api.items.DuckItem;

/**
 * An {@link IdConflictException} is thrown whenever two Addons try to add
 * a {@link DuckItem} with the same id.
 * 
 * @author TheBusyBiscuit
 *
 */
public class IdConflictException extends RuntimeException {

    private static final long serialVersionUID = -733012666374895255L;

    /**
     * Constructs a new {@link IdConflictException} with the given items.
     * 
     * @param item1
     *            The first {@link DuckItem} with this id
     * @param item2
     *            The second {@link DuckItem} with this id
     */
    @ParametersAreNonnullByDefault
    public IdConflictException(DuckItem item1, DuckItem item2) {
        super("Two items have conflicting ids: " + item1.toString() + " and " + item2.toString());
    }

}
