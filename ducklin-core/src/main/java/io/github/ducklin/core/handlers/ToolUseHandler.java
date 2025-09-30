package io.github.ducklin.core.handlers;

import java.util.List;

import io.github.ducklin.migration.items.ItemHandler;
import org.bukkit.block.Block;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.inventory.ItemStack;

@FunctionalInterface
public interface ToolUseHandler extends ItemHandler {

    void onToolUse(BlockBreakEvent e, ItemStack tool, int fortune, List<ItemStack> drops);

    @Override
    default Class<? extends ItemHandler> getIdentifier() {
        return ToolUseHandler.class;
    }
}
