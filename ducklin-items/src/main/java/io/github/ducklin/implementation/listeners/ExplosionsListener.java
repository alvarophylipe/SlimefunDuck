package io.github.ducklin.implementation.listeners;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;

import io.github.ducklin.core.Slimefun;
import io.github.ducklin.core.handlers.BlockBreakHandler;
import io.github.ducklin.migration.BlockStorage;
import io.github.ducklin.migration.attributes.WitherProof;
import io.github.ducklin.migration.items.SlimefunItem;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.EntityType;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockExplodeEvent;
import org.bukkit.event.entity.EntityChangeBlockEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.inventory.ItemStack;

public class ExplosionsListener implements Listener {

    public ExplosionsListener(@Nonnull Slimefun plugin) {
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onEntityExplode(EntityExplodeEvent e) {
        removeResistantBlocks(e.blockList().iterator());
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onBlockExplode(BlockExplodeEvent e) {
        removeResistantBlocks(e.blockList().iterator());
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onEntityBreak(EntityChangeBlockEvent e) {
        if (e.getEntity().getType() == EntityType.WITHER || e.getEntity().getType() == EntityType.WITHER_SKULL) {
            removeResistantBlock(e.getBlock());
        }
    }

    private void removeResistantBlocks(@Nonnull Iterator<Block> blocks) {
        while (blocks.hasNext()) {
            Block block = blocks.next();
            SlimefunItem item = BlockStorage.check(block);

            if (item != null) {
                blocks.remove();
                removeResistantBlock(block, item);
            }
        }
    }

    private void removeResistantBlock(@Nonnull Block block) {
        SlimefunItem slimefunItem = BlockStorage.check(block);

        if (slimefunItem != null) {
            removeResistantBlock(block, slimefunItem);
        }
    }

    private void removeResistantBlock(@Nonnull Block block, @Nonnull SlimefunItem slimefunItem) {
        // Fixes #3414 - This check removes the ghost block created by withers.
        if (!(slimefunItem instanceof WitherProof)
            && !slimefunItem.callItemHandler(BlockBreakHandler.class, handler -> handleExplosion(handler, block))
        ) {
            BlockStorage.clearBlockInfo(block, true);
            block.setType(Material.AIR);
        }
    }

    @ParametersAreNonnullByDefault
    private void handleExplosion(BlockBreakHandler handler, Block block) {
        if (handler.isExplosionAllowed(block)) {
            BlockStorage.clearBlockInfo(block, true);
            block.setType(Material.AIR);

            List<ItemStack> drops = new ArrayList<>();
            handler.onExplode(block, drops);

            for (ItemStack drop : drops) {
                if (drop != null && !drop.getType().isAir()) {
                    block.getWorld().dropItemNaturally(block.getLocation(), drop);
                }
            }
        }
    }
}
