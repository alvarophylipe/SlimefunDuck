package io.github.ducklin.implementation.items.armor;

import java.util.Arrays;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;

import io.github.bakedlibs.dough.common.Validate;
import io.github.ducklin.migration.items.ItemGroup;
import io.github.ducklin.migration.items.SlimefunItemStack;
import io.github.ducklin.migration.recipes.RecipeType;
import org.bukkit.Color;
import org.bukkit.DyeColor;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;

import io.github.ducklin.migration.utils.tags.SlimefunTag;

/**
 * Represents a {@link SlimefunArmorPiece} with rainbow properties (leather armor changing color).
 *
 * @author martinbrom
 */
public class RainbowArmorPiece extends SlimefunArmorPiece {

    private final Color[] colors;

    @ParametersAreNonnullByDefault
    public RainbowArmorPiece(ItemGroup itemGroup, SlimefunItemStack item, RecipeType recipeType, ItemStack[] recipe, DyeColor[] dyeColors) {
        super(itemGroup, item, recipeType, recipe, new PotionEffect[0]);

        // TODO Change this validation over to our custom validation blocked by https://github.com/baked-libs/dough/pull/184
        Validate.notEmpty(dyeColors, "RainbowArmorPiece colors cannot be empty!");

        if (!SlimefunTag.LEATHER_ARMOR.isTagged(item.getType())) {
            throw new IllegalArgumentException("Rainbow armor needs to be a leather armor piece!");
        }

        colors = Arrays.stream(dyeColors)
            .map(DyeColor::getColor)
            .toArray(Color[]::new);
    }

    /**
     * Returns the {@link Color}s this {@link RainbowArmorPiece} cycles between
     *
     * @return The {@link Color}s of this {@link RainbowArmorPiece}
     */
    public @Nonnull Color[] getColors() {
        return colors;
    }

}
