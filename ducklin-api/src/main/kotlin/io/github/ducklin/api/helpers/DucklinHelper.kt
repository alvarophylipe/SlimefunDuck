package io.github.ducklin.api.helpers

import io.github.ducklin.api.DucklinAPI
import io.github.ducklin.api.attributes.Soulbound
import io.github.ducklin.api.items.DucklinItem
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor
import net.kyori.adventure.text.format.TextDecoration
import org.bukkit.Material
import org.bukkit.World
import org.bukkit.entity.Item
import org.bukkit.inventory.ItemStack
import org.bukkit.metadata.FixedMetadataValue

object DucklinHelper {

    private const val NO_PICKUP = "no_pickup"
    private val SOULBOUND_LORE = Component.text("Soulbound", NamedTextColor.AQUA).decoration(TextDecoration.ITALIC, false)

    fun hasNoPickupFlag(item: Item): Boolean = item.hasMetadata(NO_PICKUP)

    fun markAsNoPickup(item: Item, context: String) {
        item.setMetadata(NO_PICKUP, FixedMetadataValue(DucklinAPI.plugin, context))
        item.pickupDelay = Short.MAX_VALUE.toInt()
    }

    fun isSoulbound(item: ItemStack?): Boolean = isSoulbound(item, null)

    fun isSoulbound(item: ItemStack?, world: World?): Boolean {
        if (item == null || item.type == Material.AIR) return false

        val meta = item.itemMeta ?: return false

        // Implementar Soulbound Rune

        val dkItem = DucklinItem.getByItem(item)

        if (dkItem is Soulbound) {
            return if (world != null) !dk.isDisabled
        }
    }





}