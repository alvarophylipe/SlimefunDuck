package io.github.ducklin.api.attributes

import io.github.ducklin.api.DucklinAPI
import org.bukkit.block.Block
import org.bukkit.util.Vector

interface HologramOwner : ItemAttribute {

    fun updateHologram(b: Block, text: String) = b.location.add(getHologramOffSet(b)).let {
        DucklinAPI.services.hologramService.setHologramLabel(it, text)
    }

    fun removeHologram(b: Block) = b.location.add(getHologramOffSet(b)).let {
        DucklinAPI.services.hologramService.removeHologram(it)
    }

    fun getHologramOffSet(b: Block): Vector = DucklinAPI.services.hologramService.defaultsOffset
}