package io.github.ducklin.api.services

import org.bukkit.Location

interface CapacitorService {
    fun updateTexture(location: Location, charge: Int, capacity: Int)
}