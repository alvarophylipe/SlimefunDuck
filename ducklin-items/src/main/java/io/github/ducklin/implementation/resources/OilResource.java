package io.github.ducklin.implementation.resources;

import org.bukkit.World.Environment;
import org.bukkit.block.Biome;

import io.github.thebusybiscuit.slimefun4.api.MinecraftVersion;
import io.github.thebusybiscuit.slimefun4.api.geo.GEOResource;
import io.github.thebusybiscuit.slimefun4.implementation.Slimefun;
import io.github.ducklin.implementation.SlimefunItems;
import io.github.ducklin.implementation.items.geo.GEOMiner;
import io.github.ducklin.implementation.items.geo.OilPump;
import io.github.ducklin.migration.utils.biomes.BiomeMap;

/**
 * A {@link GEOResource} which consists of buckets of Oil.
 * It cannot be obtained via a {@link GEOMiner} but instead requires
 * and {@link OilPump}.
 * 
 * @author TheBusyBiscuit
 *
 * @see OilPump
 *
 */
class OilResource extends AbstractResource {

    private static final int DEFAULT_OVERWORLD_VALUE = 10;

    private final BiomeMap<Integer> biomes;

    OilResource() {
        super("oil", "Oil", SlimefunItems.OIL_BUCKET.item(), 8, false);

        MinecraftVersion version = Slimefun.getMinecraftVersion();
        biomes = getBiomeMap(this, "/biome-maps/oil_v1.18.json");
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
