package me.mrCookieSlime.Slimefun.Objects.SlimefunItem.abstractItems;

import java.util.function.Predicate;

import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItemStack;
import org.apache.commons.lang.Validate;
import org.bukkit.inventory.ItemStack;

import io.github.thebusybiscuit.slimefun4.utils.SlimefunUtils;
import io.github.thebusybiscuit.slimefun4.utils.itemstack.ItemStackWrapper;

// This class will be rewritten in the "Recipe Rewrite"
public class MachineFuel implements Predicate<ItemStack> {

    private final int ticks;
    private final ItemStack fuel;
    private final ItemStack output;

    // For performance optimizations
    private final ItemStackWrapper wrapper;

    public MachineFuel(int seconds, ItemStack fuel) {
        this(seconds, fuel, null);
    }

    public MachineFuel(int seconds, ItemStack fuel, ItemStack output) {
        Validate.notNull(fuel, "Fuel must never be null!");
        Validate.isTrue(seconds > 0, "Fuel must last at least one second!");

        this.ticks = seconds * 2;
        this.fuel = fuel;
        this.wrapper = ItemStackWrapper.wrap(fuel);
        this.output = output;
    }

    public MachineFuel(int seconds, SlimefunItemStack fuel) {
        this(seconds, fuel.item(), null);
    }

    public MachineFuel(int seconds, SlimefunItemStack fuel, SlimefunItemStack output) {
        Validate.notNull(fuel, "Fuel must never be null!");
        Validate.isTrue(seconds > 0, "Fuel must last at least one second!");

        this.ticks = seconds * 2;
        this.fuel = fuel.item();
        this.wrapper = ItemStackWrapper.wrap(this.fuel);
        this.output = output != null ? output.item() : null;
    }

    public ItemStack getInput() {
        return fuel;
    }

    public ItemStack getOutput() {
        return output;
    }

    /**
     * This method returns how long this {@link MachineFuel} lasts.
     * The result represents Slimefun ticks.
     * 
     * @return How many ticks this fuel type lasts
     */
    public int getTicks() {
        return ticks;
    }

    @Override
    public boolean test(ItemStack item) {
        return SlimefunUtils.isItemSimilar(item, wrapper, true);
    }

}
