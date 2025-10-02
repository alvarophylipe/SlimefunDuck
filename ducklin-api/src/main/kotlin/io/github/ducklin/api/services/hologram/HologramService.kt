package io.github.ducklin.api.services.hologram

import org.bukkit.Location
import org.bukkit.util.Vector
import java.util.function.Consumer

interface HologramService {
    val defaultsOffset: Vector

    fun removeHologram(location: Location): Boolean

    fun setHologramLabel(location: Location, label: String?)

    fun updateHologram(location: Location, consumer: Consumer<Hologram>)
}