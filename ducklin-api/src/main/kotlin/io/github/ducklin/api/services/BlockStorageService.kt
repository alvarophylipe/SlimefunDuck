package io.github.ducklin.api.services

import io.github.ducklin.api.Config
import org.bukkit.Location

interface BlockStorageService {

    fun getLocationInfo(location: Location): Config
    fun getLocationInfo(location: Location, key: String): String?
    fun hasBlockInfo(location: Location): Boolean
    fun checkId(location: Location): String?

    fun addBlockInfo(location: Location, key: String, value: String, updateTicker: Boolean = false)
    fun clearBlockInfo(location: Location, destroy: Boolean = true)

    fun getCharge(location: Location): Int
    fun setCharge(location: Location, charge: Int)
    fun addCharge(location: Location, charge: Int)
    fun removeCharge(location: Location, charge: Int)

    fun hasInventory(location: Location): Boolean

    fun save()
    fun computeChanges(): Int

}