package io.github.ducklin.api.attributes


import io.github.ducklin.api.Config
import io.github.ducklin.api.DucklinAPI
import io.github.ducklin.api.helpers.Numbers
import io.github.ducklin.api.items.energy.EnergyNetComponentType
import org.bukkit.Location

interface EnergyNetComponent : ItemAttribute {

    val energyComponentType: EnergyNetComponentType
    val capacity: Int
    val isChargeable: Boolean; get() = capacity > 0

    fun getCharge(location: Location): Int {
        if (!isChargeable) return 0
        return getCharge(location, DucklinAPI.services.getStorage(location.world).getLocationInfo(location))
    }

    fun getCharge(location: Location, data: Config): Int {

        require(location.world.name.isNotEmpty()) {
            "Location was null!"
        }

        require(isChargeable) {
            "This component do not store energy"
        }

        if (!isChargeable) return 0
        return data.getString("energy-charge")?.toIntOrNull() ?: 0
    }

    fun setCharge(location: Location, charge: Int) {
        require(charge >= 0) {
            "Charge must be >= 0"
        }

        if (capacity <= 0) return

        val clamped = Numbers.clamp(0, charge, capacity)
        if (clamped == getCharge(location)) return

        DucklinAPI.services.getStorage(location.world).addBlockInfo(location, "energy-charge", clamped.toString(), false)

        if (energyComponentType == EnergyNetComponentType.CAPACITOR) {
            DucklinAPI.services.capacitorService.updateTexture(location, clamped, capacity)
        }
    }

    fun addCharge(location: Location, charge: Int) {
        require(charge > 0) { "You can only add a positive charge!" }
        if (capacity <= 0) return
        val current = getCharge(location)
        if (current >= capacity) return
        val new = (current + charge).coerceAtMost(capacity)
        DucklinAPI.services.getStorage(location.world).addBlockInfo(location, "energy-charge", new.toString(), false)
        if (energyComponentType == EnergyNetComponentType.CAPACITOR) {
            DucklinAPI.services.capacitorService.updateTexture(location, new, capacity)
        }
    }

    fun removeCharge(location: Location, charge: Int) {
        require(charge > 0) { "The charge to remove must be greater than zero!" }
        if (capacity <= 0) return
        val current = getCharge(location)
        if (current <= 0) return
        val new = (current - charge).coerceAtLeast(0)
        DucklinAPI.services.getStorage(location.world).addBlockInfo(location, "energy-charge", new.toString(), false)
        if (energyComponentType == EnergyNetComponentType.CAPACITOR) {
            DucklinAPI.services.capacitorService.updateTexture(location, new, capacity)
        }
    }
}