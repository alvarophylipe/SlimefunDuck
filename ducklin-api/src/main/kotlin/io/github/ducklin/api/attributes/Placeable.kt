package io.github.ducklin.api.attributes

import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack

interface Placeable {
    val drops: Collection<ItemStack>
    fun getDrops(p: Player): Collection<ItemStack>
    fun useVanillaBlockBreaking(): Boolean = false
}