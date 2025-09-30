package io.github.ducklin.implementation.listeners;

import javax.annotation.Nonnull;

import io.github.ducklin.core.Slimefun;
import io.github.ducklin.core.handlers.BlockDispenseHandler;
import io.github.ducklin.migration.BlockStorage;
import io.github.ducklin.migration.items.SlimefunItem;
import io.papermc.lib.PaperLib;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.BlockState;
import org.bukkit.block.Dispenser;
import org.bukkit.block.data.Directional;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockDispenseEvent;



public class DispenserListener implements Listener {

    public DispenserListener(@Nonnull Slimefun plugin) {
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }

    @EventHandler
    public void onBlockDispensing(BlockDispenseEvent e) {
        Block b = e.getBlock();

        if (b.getType() == Material.DISPENSER && b.getRelative(BlockFace.DOWN).getType() != Material.HOPPER) {
            SlimefunItem machine = BlockStorage.check(b);

            // Fixes #2959
            if (machine != null && !machine.isDisabledIn(e.getBlock().getWorld())) {
                machine.callItemHandler(BlockDispenseHandler.class, handler -> {
                    BlockState state = PaperLib.getBlockState(b, false).getState();

                    if (state instanceof Dispenser dispenser) {
                        BlockFace face = ((Directional) b.getBlockData()).getFacing();
                        Block block = b.getRelative(face);
                        handler.onBlockDispense(e, dispenser, block, machine);
                    }
                });
            }
        }
    }
}
