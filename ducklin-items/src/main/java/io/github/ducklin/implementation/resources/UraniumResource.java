package io.github.ducklin.implementation.resources;

import org.bukkit.World.Environment;
import org.bukkit.block.Biome;

import io.github.thebusybiscuit.slimefun4.api.MinecraftVersion;
import io.github.thebusybiscuit.slimefun4.api.geo.GEOResource;
import io.github.thebusybiscuit.slimefun4.implementation.Slimefun;
import io.github.ducklin.implementation.SlimefunItems;
import io.github.ducklin.migration.utils.biomes.BiomeMap;

/**
 * A {@link GEOResource} which consists of small chunks of Uranium.
 * 
 * @author TheBusyBiscuit
 *
 */
class UraniumResource extends AbstractResource {

    private static final int DEFAULT_OVERWORLD_VALUE = 4;

    private final BiomeMap<Integer> biomes;

    UraniumResource() {
        super("uranium", "Small Chunks of Uranium", SlimefunItems.SMALL_URANIUM.item(), 2, true);

        MinecraftVersion version = Slimefun.getMinecraftVersion();
        biomes = getBiomeMap(this, "/biome-maps/uranium_v1.18.json");

    }

    @Override
    public int getDefaultSupply(Environment environment, Biome biome) {
        if (environment != Environment.NORMAL) {
            return 0;
        } else {
            return biomes.getOrDefault(biome, DEFAULT_OVERWORLD_VALUE);
        }
    }

}
