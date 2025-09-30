package io.github.ducklin.migration.items;

import java.util.Optional;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import io.github.ducklin.implementation.items.armor.SlimefunArmorPiece;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.Damageable;
import org.bukkit.inventory.meta.ItemMeta;


public final class HashedArmorpiece {

    private int hash;
    private Optional<SlimefunArmorPiece> item;

    /**
     * This initializes a new {@link HashedArmorpiece} with no {@link SlimefunArmorPiece}
     * and a zero hash.
     */
    public HashedArmorpiece() {
        this.hash = 0;
        this.item = Optional.empty();
    }

    /**
     * This will update this {@link HashedArmorpiece} with the given {@link ItemStack}
     * and the corresponding {@link SlimefunItem}
     * 
     * @param stack
     *            The new armorpiece to be stored in this {@link HashedArmorpiece}
     * @param item
     *            The {@link SlimefunItem} corresponding to the provided {@link ItemStack}, may be null
     */
    public void update(@Nullable ItemStack stack, @Nullable SlimefunItem item) {
        if (stack == null || stack.getType() == Material.AIR) {
            this.hash = 0;
        } else {
            ItemStack copy = stack.clone();
            ItemMeta meta = copy.getItemMeta();
            ((Damageable) meta).setDamage(0);
            copy.setItemMeta(meta);
            this.hash = copy.hashCode();
        }

        if (item instanceof SlimefunArmorPiece armorPiece) {
            this.item = Optional.of(armorPiece);
        } else {
            this.item = Optional.empty();
        }
    }

    /**
     * This method checks whether the given {@link ItemStack} is no longer similar to the
     * one represented by this {@link HashedArmorpiece}.
     * 
     * @param stack
     *            The {@link ItemStack} to compare
     * @return Whether the {@link HashedArmorpiece} and the given {@link ItemStack} mismatch
     */
    public boolean hasDiverged(@Nullable ItemStack stack) {
        if (stack == null || stack.getType() == Material.AIR) {
            return hash != 0;
        } else {
            ItemStack copy = stack.clone();
            ItemMeta meta = copy.getItemMeta();
            ((Damageable) meta).setDamage(0);
            copy.setItemMeta(meta);
            return copy.hashCode() != hash;
        }
    }

    /**
     * Returns the {@link SlimefunArmorPiece} that corresponds to this {@link HashedArmorpiece},
     * or an empty {@link Optional}
     * 
     * @return An {@link Optional} describing the result
     */
    public @Nonnull Optional<SlimefunArmorPiece> getItem() {
        return item;
    }

    @Override
    public String toString() {
        return "HashedArmorpiece {hash=" + hash + ",item=" + item.map(SlimefunItem::getId).orElse("null") + '}';
    }

}
