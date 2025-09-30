package io.github.ducklin.implementation.items.food;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;

import io.github.ducklin.core.handlers.ItemConsumptionHandler;
import io.github.ducklin.migration.items.ItemGroup;
import io.github.ducklin.migration.items.SlimefunItemStack;
import io.github.ducklin.migration.recipes.RecipeType;
import io.github.ducklin.migration.utils.SlimefunUtils;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import io.github.ducklin.implementation.items.SimpleSlimefunItem;


public class Juice extends SimpleSlimefunItem<ItemConsumptionHandler> {

    private final List<PotionEffect> effects;

    @ParametersAreNonnullByDefault
    public Juice(ItemGroup itemGroup, SlimefunItemStack item, RecipeType recipeType, ItemStack[] recipe) {
        this(itemGroup, item, recipeType, recipe, null);
    }

    @ParametersAreNonnullByDefault
    public Juice(ItemGroup itemGroup, SlimefunItemStack item, RecipeType recipeType, ItemStack[] recipe, @Nullable ItemStack recipeOutput) {
        super(itemGroup, item, recipeType, recipe, recipeOutput);

        ItemMeta meta = item.getItemMeta();

        if (meta instanceof PotionMeta potionMeta) {
            effects = potionMeta.getCustomEffects();
        } else {
            effects = new ArrayList<>();
        }
    }

    @Override
    public ItemConsumptionHandler getItemHandler() {
        return (e, p, item) -> {
            /*
             * Fix for Saturation on potions is no longer working,
             * Minecraft has been broken when it comes to Saturation potions for a long time
             */
            for (PotionEffect effect : effects) {
                if (effect.getType() == PotionEffectType.SATURATION || effect.getType() == PotionEffectType.ABSORPTION) {
                    p.addPotionEffect(effect);
                    break;
                }
            }

            removeGlassBottle(p, item);
        };
    }

    /**
     * Determines from which hand the juice is being drunk, and its amount
     * 
     * @param p
     *            The {@link Player} that triggered this
     * @param item
     *            The {@link ItemStack} in question
     */
    @ParametersAreNonnullByDefault
    private void removeGlassBottle(Player p, ItemStack item) {
        if (SlimefunUtils.isItemSimilar(item, p.getInventory().getItemInMainHand(), true)) {
            if (p.getInventory().getItemInMainHand().getAmount() == 1) {
                p.getEquipment().getItemInMainHand().setAmount(0);
            } else {
                p.getInventory().removeItem(new ItemStack(Material.GLASS_BOTTLE, 1));
            }
        } else if (SlimefunUtils.isItemSimilar(item, p.getInventory().getItemInOffHand(), true)) {
            if (p.getInventory().getItemInOffHand().getAmount() == 1) {
                p.getEquipment().getItemInOffHand().setAmount(0);
            } else {
                p.getInventory().removeItem(new ItemStack(Material.GLASS_BOTTLE, 1));
            }
        }
    }

}
