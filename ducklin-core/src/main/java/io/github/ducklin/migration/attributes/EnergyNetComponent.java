package io.github.ducklin.migration.attributes;

import java.util.logging.Level;

import javax.annotation.Nonnull;

import io.github.ducklin.core.Configuration.Config;
import io.github.ducklin.core.Slimefun;
import io.github.ducklin.migration.BlockStorage;
import io.github.ducklin.migration.utils.NumberUtils;
import io.github.ducklin.migration.utils.SlimefunUtils;
import org.apache.commons.lang.Validate;
import org.bukkit.Location;

import io.github.bakedlibs.dough.blocks.BlockPosition;
import io.github.ducklin.migration.networks.energy.EnergyNet;
import io.github.ducklin.migration.networks.energy.EnergyNetComponentType;


public interface EnergyNetComponent extends ItemAttribute {


    @Nonnull
    EnergyNetComponentType getEnergyComponentType();

    /**
     * This method returns the max amount of electricity this Block can hold.
     * If the capacity is zero, then this Block cannot hold any electricity.
     * 
     * @return The max amount of electricity this Block can store.
     */
    int getCapacity();

    /**
     * This returns whether this {@link EnergyNetComponent} can hold energy charges.
     * It returns true if {@link #getCapacity()} returns a number greater than zero.
     * 
     * @return Whether this {@link EnergyNetComponent} can store energy.
     */
    default boolean isChargeable() {
        return getCapacity() > 0;
    }

    /**
     * This returns the currently stored charge at a given {@link Location}.
     * 
     * @param l
     *            The target {@link Location}
     * 
     * @return The charge stored at that {@link Location}
     */
    default int getCharge(@Nonnull Location l) {
        // Emergency fallback, this cannot hold a charge, so we'll just return zero
        if (!isChargeable()) {
            return 0;
        }

        return getCharge(l, BlockStorage.getLocationInfo(l));
    }

    /**
     * This returns the currently stored charge at a given {@link Location}.
     * This is a more performance saving option if you already have a {@link Config}
     * object for this {@link Location}.
     * 
     * @param l
     *            The target {@link Location}
     * @param data
     *            The data at this {@link Location}
     * 
     * @return The charge stored at that {@link Location}
     */
    default int getCharge(@Nonnull Location l, @Nonnull Config data) {
        Validate.notNull(l, "Location was null!");
        Validate.notNull(data, "data was null!");

        // Emergency fallback, this cannot hold a charge, so we'll just return zero
        if (!isChargeable()) {
            return 0;
        }

        String charge = data.getString("energy-charge");

        if (charge != null) {
            return Integer.parseInt(charge);
        } else {
            return 0;
        }
    }


    default void setCharge(@Nonnull Location l, int charge) {
        Validate.notNull(l, "Location was null!");
        Validate.isTrue(charge >= 0, "You can only set a charge of zero or more!");

        try {
            int capacity = getCapacity();

            // This method only makes sense if we can actually store energy
            if (capacity > 0) {
                charge = NumberUtils.clamp(0, charge, capacity);

                // Do we even need to update the value?
                if (charge != getCharge(l)) {
                    BlockStorage.addBlockInfo(l, "energy-charge", String.valueOf(charge), false);

                    // Update the capacitor texture
                    if (getEnergyComponentType() == EnergyNetComponentType.CAPACITOR) {
                        SlimefunUtils.updateCapacitorTexture(l, charge, capacity);
                    }
                }
            }
        } catch (Exception | LinkageError x) {
            Slimefun.logger().log(Level.SEVERE, x, () -> "Exception while trying to set the energy-charge for \"" + getId() + "\" at " + new BlockPosition(l));
        }
    }

    default void addCharge(@Nonnull Location l, int charge) {
        Validate.notNull(l, "Location was null!");
        Validate.isTrue(charge > 0, "You can only add a positive charge!");

        try {
            int capacity = getCapacity();

            // This method only makes sense if we can actually store energy
            if (capacity > 0) {
                int currentCharge = getCharge(l);

                // Check if there is even space for new energy
                if (currentCharge < capacity) {
                    int newCharge = Math.min(capacity, currentCharge + charge);
                    BlockStorage.addBlockInfo(l, "energy-charge", String.valueOf(newCharge), false);

                    // Update the capacitor texture
                    if (getEnergyComponentType() == EnergyNetComponentType.CAPACITOR) {
                        SlimefunUtils.updateCapacitorTexture(l, charge, capacity);
                    }
                }
            }
        } catch (Exception | LinkageError x) {
            Slimefun.logger().log(Level.SEVERE, x, () -> "Exception while trying to add an energy-charge for \"" + getId() + "\" at " + new BlockPosition(l));
        }
    }

    default void removeCharge(@Nonnull Location l, int charge) {
        Validate.notNull(l, "Location was null!");
        Validate.isTrue(charge > 0, "The charge to remove must be greater than zero!");

        try {
            int capacity = getCapacity();

            // This method only makes sense if we can actually store energy
            if (capacity > 0) {
                int currentCharge = getCharge(l);

                // Check if there is even energy stored
                if (currentCharge > 0) {
                    int newCharge = Math.max(0, currentCharge - charge);
                    BlockStorage.addBlockInfo(l, "energy-charge", String.valueOf(newCharge), false);

                    // Update the capacitor texture
                    if (getEnergyComponentType() == EnergyNetComponentType.CAPACITOR) {
                        SlimefunUtils.updateCapacitorTexture(l, charge, capacity);
                    }
                }
            }
        } catch (Exception | LinkageError x) {
            Slimefun.logger().log(Level.SEVERE, x, () -> "Exception while trying to remove an energy-charge for \"" + getId() + "\" at " + new BlockPosition(l));
        }
    }

}
