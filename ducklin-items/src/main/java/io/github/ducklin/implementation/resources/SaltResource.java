package io.github.ducklin.implementation.resources;

import org.bukkit.World.Environment;
import org.bukkit.block.Biome;

import io.github.thebusybiscuit.slimefun4.api.MinecraftVersion;
import io.github.thebusybiscuit.slimefun4.api.geo.GEOResource;
import io.github.thebusybiscuit.slimefun4.implementation.Slimefun;
import io.github.ducklin.implementation.SlimefunItems;
import io.github.ducklin.migration.utils.biomes.BiomeMap;

/**
 * A {@link GEOResource} which consists of Salt.
 * 
 * @author TheBusyBiscuit
 *
 */
class SaltResource extends AbstractResource {

    private static final int DEFAULT_OVERWORLD_VALUE = 6;
    private static final int DEFAULT_NETHER_VALUE = 8;

    private final BiomeMap<Integer> biomes;

    SaltResource() {
        super("salt", "Salt", SlimefunItems.SALT.item(), 18, true);
        MinecraftVersion version = Slimefun.getMinecraftVersion();
        biomes = getBiomeMap(this, "/biome-maps/salt_v1.18.json");

    }

    @Override
    public int getDefaultSupply(Environment environment, Biome biome) {
        if (environment == Environment.NORMAL) {
            return biomes.getOrDefault(biome, DEFAULT_OVERWORLD_VALUE);
        } else if (environment == Environment.NETHER) {
            return biomes.getOrDefault(biome, DEFAULT_NETHER_VALUE);
        } else {
            return 0;
        }
    }

}
