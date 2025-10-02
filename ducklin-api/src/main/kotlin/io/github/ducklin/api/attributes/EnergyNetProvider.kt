package io.github.ducklin.api.attributes

import io.github.ducklin.api.Config
import io.github.ducklin.api.items.energy.EnergyNetComponentType
import org.bukkit.Location

interface EnergyNetProvider : EnergyNetComponent {
    override val energyComponentType: EnergyNetComponentType; get() = EnergyNetComponentType.GENERATOR
    fun getGeneratedOutput(location: Location, data: Config): Int
    fun willExplode(location: Location, data: Config): Boolean
}