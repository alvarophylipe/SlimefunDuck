package io.github.ducklin.implementation.listeners;

import javax.annotation.Nonnull;

import io.github.ducklin.core.Slimefun;
import io.github.ducklin.migration.utils.RadiationUtils;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;

import io.github.ducklin.migration.tasks.armor.RadiationTask;

/**
 * {@link RadioactivityListener} handles radioactivity level resets
 * on death
 *
 * @author Semisol
 */
public class RadioactivityListener implements Listener {

    public RadioactivityListener(@Nonnull Slimefun plugin) {
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }

    @EventHandler
    public void onPlayerDeath(@Nonnull PlayerDeathEvent e) {
        RadiationUtils.clearExposure(e.getEntity());
        RadiationTask.addGracePeriod(e.getEntity());
    }
}
