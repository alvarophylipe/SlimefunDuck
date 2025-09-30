package io.github.ducklin.core.handlers;

import javax.annotation.Nonnull;

import io.github.ducklin.migration.items.ItemHandler;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.inventory.ItemStack;


/**
 * This is triggered when a {@link Player} attacks an {@link Entity}.
 *
 * @author Mooy1
 *
 */
@FunctionalInterface
public interface WeaponUseHandler extends ItemHandler {

    void onHit(@Nonnull EntityDamageByEntityEvent e, @Nonnull Player player, @Nonnull ItemStack item);

    @Override
    default Class<? extends ItemHandler> getIdentifier() {
        return WeaponUseHandler.class;
    }

}
