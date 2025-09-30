package io.github.ducklin.implementation.listeners.entity;

import java.util.Optional;

import javax.annotation.Nonnull;

import io.github.ducklin.core.Slimefun;
import io.github.ducklin.migration.attributes.ProtectionType;
import io.github.ducklin.migration.player.PlayerProfile;
import org.bukkit.entity.Bee;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.inventory.ItemStack;

import io.github.bakedlibs.dough.items.ItemUtils;
/**
 * The {@link Listener} for Hazmat Suit's {@link Bee} sting protection.
 * Only applied if the whole set is worn.
 *
 * @author Linox
 *
 */
public class BeeListener implements Listener {

    public BeeListener(@Nonnull Slimefun plugin) {
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }

    @EventHandler
    public void onDamage(EntityDamageByEntityEvent e) {
        if (e.getDamager() instanceof Bee && e.getEntity() instanceof Player) {
            Player p = (Player) e.getEntity();
            Optional<PlayerProfile> optional = PlayerProfile.find(p);

            if (!optional.isPresent()) {
                PlayerProfile.request(p);
                return;
            }

            PlayerProfile profile = optional.get();

            if (profile.hasFullProtectionAgainst(ProtectionType.BEES)) {
                for (ItemStack armor : p.getInventory().getArmorContents()) {
                    if (armor != null) {
                        ItemUtils.damageItem(armor, 1, false);
                    }
                }

                e.setDamage(0D);
            }
        }
    }

}
