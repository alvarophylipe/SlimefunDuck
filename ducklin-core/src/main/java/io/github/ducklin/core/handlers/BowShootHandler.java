package io.github.ducklin.core.handlers;

import java.util.Optional;

import io.github.ducklin.migration.exceptions.IncompatibleItemHandlerException;
import io.github.ducklin.migration.items.ItemHandler;
import io.github.ducklin.api.items.DuckItem;
import org.bukkit.Material;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

@FunctionalInterface
public interface BowShootHandler extends ItemHandler {

    void onHit(EntityDamageByEntityEvent e, LivingEntity n);

    @Override
    default Optional<IncompatibleItemHandlerException> validate(DuckItem item) {
        if (item.getItem().getType() != Material.BOW) {
            return Optional.of(new IncompatibleItemHandlerException("Only bows can have a BowShootHandler.", item, this));
        }

        return Optional.empty();
    }

    @Override
    default Class<? extends ItemHandler> getIdentifier() {
        return BowShootHandler.class;
    }
}
