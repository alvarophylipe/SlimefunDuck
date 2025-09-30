package io.github.ducklin.migration.exceptions;

import javax.annotation.ParametersAreNonnullByDefault;

import io.github.ducklin.migration.items.SlimefunItem;

import io.github.ducklin.migration.items.ItemHandler;


public class IncompatibleItemHandlerException extends RuntimeException {

    private static final long serialVersionUID = -6723066421114874138L;

    /**
     * Constructs a new {@link IncompatibleItemHandlerException} with the given {@link SlimefunItem} and
     * {@link ItemHandler}
     * 
     * @param message
     *            The reason why they are incompatible
     * @param item
     *            The {@link SlimefunItem} that was affected by this
     * @param handler
     *            The {@link ItemHandler} which someone tried to add
     */
    @ParametersAreNonnullByDefault
    public IncompatibleItemHandlerException(String message, SlimefunItem item, ItemHandler handler) {
        super("The item handler type: \"" + handler.getIdentifier().getSimpleName() + "\" is not compatible with " + item + " (" + message + ')');
    }

}
