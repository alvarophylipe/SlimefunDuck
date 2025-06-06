package io.github.thebusybiscuit.slimefun4.implementation.items.multiblocks;

import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;

import org.bukkit.Bukkit;
import org.bukkit.Effect;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.BlockState;
import org.bukkit.block.Dispenser;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import io.github.thebusybiscuit.slimefun4.api.events.MultiBlockCraftEvent;
import io.github.thebusybiscuit.slimefun4.api.items.ItemGroup;
import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItemStack;
import io.github.thebusybiscuit.slimefun4.core.multiblocks.MultiBlockMachine;
import io.github.thebusybiscuit.slimefun4.core.services.sounds.SoundEffect;
import io.github.thebusybiscuit.slimefun4.implementation.Slimefun;
import io.github.thebusybiscuit.slimefun4.implementation.SlimefunItems;
import io.github.thebusybiscuit.slimefun4.utils.SlimefunUtils;
import io.papermc.lib.PaperLib;

/**
 * The {@link OreWasher} is a special {@link MultiBlockMachine} which allows you to
 * turn Sifted Ore into ore dusts.
 * 
 * @author TheBusyBiscuit
 * @author Sfiguz7
 *
 */
public class OreWasher extends MultiBlockMachine {

    // @formatter:off
    private final SlimefunItemStack[] dusts = new SlimefunItemStack[] {
        SlimefunItems.IRON_DUST,
        SlimefunItems.GOLD_DUST,
        SlimefunItems.COPPER_DUST,
        SlimefunItems.TIN_DUST,
        SlimefunItems.ZINC_DUST,
        SlimefunItems.ALUMINUM_DUST,
        SlimefunItems.MAGNESIUM_DUST,
        SlimefunItems.LEAD_DUST,
        SlimefunItems.SILVER_DUST
    };
    // @formatter:on

    private final boolean legacyMode;

    @ParametersAreNonnullByDefault
    public OreWasher(ItemGroup itemGroup, SlimefunItemStack item) {
        // @formatter:off
        super(itemGroup, item, new ItemStack[] {
            null, new ItemStack(Material.DISPENSER), null,
            null, new ItemStack(Material.OAK_FENCE), null,
            null, new ItemStack(Material.CAULDRON), null
        }, BlockFace.SELF);
        // @formatter:on

        legacyMode = Slimefun.getCfg().getBoolean("options.legacy-ore-washer");
    }

    @Override
    protected void registerDefaultRecipes(List<ItemStack> recipes) {
        /*
         * Iron and Gold are displayed as Ore Crusher recipes, as that is their primary
         * way of obtaining them. But we also wanna display them here, so we just
         * add these two recipes manually
         */
        recipes.add(SlimefunItems.SIFTED_ORE.item());
        recipes.add(SlimefunItems.IRON_DUST.item());

        recipes.add(SlimefunItems.SIFTED_ORE.item());
        recipes.add(SlimefunItems.GOLD_DUST.item());

        recipes.add(new ItemStack(Material.SAND));
        recipes.add(SlimefunItems.SALT.item());
    }

    @Override
    public @Nonnull List<ItemStack> getDisplayRecipes() {
        return recipes.stream().map(items -> items[0]).toList();
    }

    @Override
    public void onInteract(Player p, Block b) {
        Block dispBlock = b.getRelative(BlockFace.UP);
        BlockState state = PaperLib.getBlockState(dispBlock, false).getState();

        if (state instanceof Dispenser disp) {
            Inventory inv = disp.getInventory();

            for (ItemStack input : inv.getContents()) {
                if (input != null) {
                    if (SlimefunUtils.isItemSimilar(input, SlimefunItems.SIFTED_ORE.item(), true)) {
                        ItemStack output = getRandomDust();
                        Inventory outputInv;

                        if (!legacyMode) {
                            /*
                             * This is a fancy way of checking if there is empty space in the inv
                             * by checking if an unobtainable item could fit in it.
                             * However, due to the way the method findValidOutputInv() functions,
                             * the dummyAdding will never actually be added to the real inventory,
                             * so it really doesn't matter what item the ItemStack is made by.
                             * SlimefunItems.DEBUG_FISH however, signals that it's not supposed
                             * to be given to the player.
                             */
                            ItemStack dummyAdding = SlimefunItems.DEBUG_FISH.item();
                            outputInv = findOutputInventory(dummyAdding, dispBlock, inv);
                        } else {
                            outputInv = findOutputInventory(output, dispBlock, inv);
                        }

                        MultiBlockCraftEvent event = new MultiBlockCraftEvent(p, this, input, output);
                        if (event.isCancelled()) {
                            return;
                        }

                        removeItem(p, b, inv, outputInv, input, event.getOutput(), 1);

                        if (outputInv != null) {
                            outputInv.addItem(SlimefunItems.STONE_CHUNK.item());
                        }

                        return;
                    } else if (SlimefunUtils.isItemSimilar(input, new ItemStack(Material.SAND, 2), false)) {
                        ItemStack output = SlimefunItems.SALT.item();
                        Inventory outputInv = findOutputInventory(output, dispBlock, inv);

                        MultiBlockCraftEvent event = new MultiBlockCraftEvent(p, this, input, output);
                        if (event.isCancelled()) {
                            return;
                        }

                        removeItem(p, b, inv, outputInv, input, event.getOutput(), 2);

                        return;
                    } else if (SlimefunUtils.isItemSimilar(input, SlimefunItems.PULVERIZED_ORE.item(), true)) {
                        ItemStack output = SlimefunItems.PURE_ORE_CLUSTER.item();
                        Inventory outputInv = findOutputInventory(output, dispBlock, inv);
                        MultiBlockCraftEvent event = new MultiBlockCraftEvent(p, this, input, output);

                        Bukkit.getPluginManager().callEvent(event);
                        if (event.isCancelled()) {
                            return;
                        }

                        removeItem(p, b, inv, outputInv, input, event.getOutput(), 1);

                        return;
                    }
                }
            }
            Slimefun.getLocalization().sendMessage(p, "machines.unknown-material", true);
        }
    }

    @ParametersAreNonnullByDefault
    private void removeItem(Player p, Block b, Inventory inputInv, @Nullable Inventory outputInv, ItemStack input, ItemStack output, int amount) {
        if (outputInv != null) {
            ItemStack removing = input.clone();
            removing.setAmount(amount);
            inputInv.removeItem(removing);
            outputInv.addItem(output.clone());

            b.getWorld().playEffect(b.getLocation(), Effect.STEP_SOUND, Material.WATER);
            SoundEffect.ORE_WASHER_WASH_SOUND.playAt(b);
        } else {
            Slimefun.getLocalization().sendMessage(p, "machines.full-inventory", true);
        }
    }

    /**
     * This returns a random dust item from Slimefun.
     * 
     * @return A randomly picked dust item
     */
    public @Nonnull ItemStack getRandomDust() {
        int index = ThreadLocalRandom.current().nextInt(dusts.length);
        return dusts[index].item();
    }

}
