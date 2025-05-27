package io.github.thebusybiscuit.slimefun4.implementation.items.electric;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;

import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItem;
import io.github.thebusybiscuit.slimefun4.utils.tags.SlimefunTag;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.Chest;
import org.bukkit.block.Container;
import org.bukkit.block.ShulkerBox;
import org.bukkit.entity.Player;
import org.bukkit.entity.Shulker;
import org.bukkit.inventory.ItemStack;

import io.github.bakedlibs.dough.common.ChatColors;
import io.github.thebusybiscuit.slimefun4.api.items.ItemGroup;
import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItemStack;
import io.github.thebusybiscuit.slimefun4.api.recipes.RecipeType;
import io.github.thebusybiscuit.slimefun4.core.attributes.EnergyNetComponent;
import io.github.thebusybiscuit.slimefun4.core.handlers.BlockUseHandler;
import io.github.thebusybiscuit.slimefun4.core.networks.energy.EnergyNet;
import io.github.thebusybiscuit.slimefun4.core.networks.energy.EnergyNetComponentType;
import io.github.thebusybiscuit.slimefun4.implementation.items.SimpleSlimefunItem;

import java.util.EnumSet;
import java.util.Set;

/**
 * This {@link EnergyNetComponent} is a connector for the {@link EnergyNet} networks.
 * They work similar to {@link Capacitor capacitors}.
 *
 * @author Linox
 *
 * @see EnergyNet
 * @see EnergyNetComponent
 *
 */
public class EnergyConnector extends SimpleSlimefunItem<BlockUseHandler> implements EnergyNetComponent {

    private static final Set<Material> BLACKLIST = EnumSet.noneOf(Material.class);

    static {
        BLACKLIST.add(Material.CHEST);
        BLACKLIST.add(Material.TRAPPED_CHEST);
        BLACKLIST.add(Material.ENDER_CHEST);
        BLACKLIST.add(Material.FURNACE);
        BLACKLIST.add(Material.SMITHING_TABLE);
        BLACKLIST.add(Material.CRAFTING_TABLE);
        BLACKLIST.add(Material.DROPPER);
        BLACKLIST.add(Material.DISPENSER);
        BLACKLIST.add(Material.SMOKER);
        BLACKLIST.add(Material.BLAST_FURNACE);
        BLACKLIST.add(Material.HOPPER);
        BLACKLIST.add(Material.BREWING_STAND);
        BLACKLIST.add(Material.DRAGON_EGG);
        BLACKLIST.add(Material.BARREL);
        BLACKLIST.add(Material.AIR);
        BLACKLIST.add(Material.SPONGE);
        BLACKLIST.add(Material.SPAWNER);
        BLACKLIST.add(Material.OBSIDIAN);
        BLACKLIST.add(Material.REINFORCED_DEEPSLATE);
        BLACKLIST.add(Material.GRINDSTONE);
        BLACKLIST.add(Material.STONECUTTER);
    }

    @ParametersAreNonnullByDefault
    public EnergyConnector(ItemGroup itemGroup, SlimefunItemStack item, RecipeType recipeType, ItemStack[] recipe, ItemStack recipeOutput) {
        super(itemGroup, item, recipeType, recipe, recipeOutput);
    }

    @Override
    public @Nonnull BlockUseHandler getItemHandler() {
        return e -> {
            if (!e.getClickedBlock().isPresent()) {
                return;
            }

            Player p = e.getPlayer();
            Block b = e.getClickedBlock().get();

            if (p.isSneaking()) {
                ItemStack mainItem = p.getInventory().getItemInMainHand();
                Material materialItem = mainItem.getType();

                if (mainItem != null &&
                        !BLACKLIST.contains(materialItem) &&
                        materialItem.isBlock() &&
                        !materialItem.hasGravity() &&
                        !SlimefunTag.UNBREAKABLE_MATERIALS.isTagged(materialItem) &&
                        !SlimefunTag.GLASS_PANES.isTagged(materialItem) &&
                        !SlimefunTag.ICE_VARIANTS.isTagged(materialItem) &&
                        !SlimefunTag.SENSITIVE_MATERIALS.isTagged(materialItem) &&
                        materialItem.isOccluding() &&
                        SlimefunItem.getByItem(mainItem) == null) {
                    b.setType(materialItem);
                    e.cancel();
                }
            }

            if (EnergyNet.getNetworkFromLocation(b.getLocation()) != null) {
                p.sendMessage(ChatColors.color("&7Conectado: " + "&2\u2714"));
            } else {
                p.sendMessage(ChatColors.color("&7Conectado: " + "&4\u2718"));
            }
        };
    }

    @Override
    public final @Nonnull EnergyNetComponentType getEnergyComponentType() {
        return EnergyNetComponentType.CONNECTOR;
    }

    @Override
    public int getCapacity() {
        return 0;
    }
}
