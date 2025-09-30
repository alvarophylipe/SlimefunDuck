package io.github.ducklin.integrations;

import javax.annotation.Nonnull;

import io.github.ducklin.migration.events.AutoDisenchantEvent;
import io.github.ducklin.migration.events.BlockPlacerPlaceEvent;
import io.github.ducklin.migration.items.SlimefunItem;
import io.github.ducklin.core.Slimefun;
import io.github.ducklin.implementation.items.VanillaItem;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;

import com.gmail.nossr50.mcMMO;
import com.gmail.nossr50.events.skills.salvage.McMMOPlayerSalvageCheckEvent;
import com.gmail.nossr50.util.skills.SkillUtils;


/**
 * This handles all integrations with {@link mcMMO}.
 * 
 * @author TheBusyBiscuit
 *
 */
class McMMOIntegration implements Listener {

    private final Slimefun plugin;

    McMMOIntegration(@Nonnull Slimefun plugin) {
        this.plugin = plugin;
    }

    public void register() {
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onBlockPlacerPlace(BlockPlacerPlaceEvent e) {
        // This registers blocks placed by the BlockPlacer as "player-placed"
        try {
            mcMMO.getChunkManager().setTrue(e.getBlock());
        } catch (Exception | LinkageError x) {
            Slimefun.getIntegrations().logError("mcMMO", x);
        }
    }

    @EventHandler(ignoreCancelled = true)
    public void onItemSalvage(McMMOPlayerSalvageCheckEvent e) {
        // Prevent Slimefun items from being salvaged
        if (!isSalvageable(e.getSalvageItem())) {
            e.setCancelled(true);
            Slimefun.getLocalization().sendMessage(e.getPlayer(), "anvil.mcmmo-salvaging");
        }
    }

    @EventHandler(ignoreCancelled = true)
    public void onAutoDisenchant(AutoDisenchantEvent e) {
        try {
            SkillUtils.removeAbilityBuff(e.getItem());
        } catch (Exception | LinkageError x) {
            Slimefun.getIntegrations().logError("mcMMO", x);
        }
    }

    /**
     * This method checks if an {@link ItemStack} can be salvaged or not.
     * We basically don't want players to salvage any {@link SlimefunItem} unless
     * it is a {@link VanillaItem}.
     * 
     * @param item
     *            The {@link ItemStack} to check
     * 
     * @return Whether this item can be safely salvaged
     */
    private boolean isSalvageable(@Nonnull ItemStack item) {
        SlimefunItem sfItem = SlimefunItem.getByItem(item);
        return sfItem == null || sfItem instanceof VanillaItem;
    }

}
