package io.github.thebusybiscuit.slimefun4.implementation.items.androids;

import javax.annotation.Nonnull;

import org.bukkit.inventory.ItemStack;

import io.github.bakedlibs.dough.items.CustomItemStack;
import io.github.thebusybiscuit.slimefun4.utils.HeadTexture;

/**
 * This enum covers all different fuel sources a {@link ProgrammableAndroid} can have.
 *
 * @author TheBusyBiscuit
 *
 */
public enum AndroidFuelSource {

    /**
     * This {@link ProgrammableAndroid} runs on solid fuel, e.g. Wood or coal
     */
    SOLID("", "&fEste Android funciona com Combustível sólido", "&fex.: Carvão, Madeira, etc..."),

    /**
     * This {@link ProgrammableAndroid} runs on liquid fuel, e.g. Fuel, Oil or Lava
     */
    LIQUID("", "&fEste Android funciona com Combustível líquido", "&fex.: Lava, Óleo, Combustível, etc..."),

    /**
     * This {@link ProgrammableAndroid} runs on nuclear fuel, e.g. Uranium
     */
    NUCLEAR("", "&fEste Android funciona com Combustível radioativo", "&fex.: Urânio, Netúnio or Urânio Enriquecido");

    private final String[] lore;

    AndroidFuelSource(@Nonnull String... lore) {
        this.lore = lore;
    }

    /**
     * This returns a display {@link ItemStack} for this {@link AndroidFuelSource}.
     *
     * @return An {@link ItemStack} to display
     */
    @Nonnull
    public ItemStack getItem() {
        return CustomItemStack.create(HeadTexture.GENERATOR.getAsItemStack(), "&8\u21E9 &cEntrada de Combustível &8\u21E9", lore);
    }

}
