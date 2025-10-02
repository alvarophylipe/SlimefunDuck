package io.github.ducklin.api.services

import org.bukkit.Keyed
import org.bukkit.NamespacedKey
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.meta.ItemMeta
import org.bukkit.persistence.PersistentDataContainer

interface ItemDataService : Keyed {

    fun isSlimefunItem(item: ItemStack): Boolean

    fun getItemId(item: ItemStack): String?

    fun getDataContainer(item: ItemStack): PersistentDataContainer?

    fun setItemData(item: ItemStack, id: String)

    fun setItemData(meta: ItemMeta, id: String)

    fun getItemData(item: ItemStack): String?

    fun getItemData(meta: ItemMeta): String?

    fun hasEqualItemData(meta1: ItemMeta, meta2: ItemMeta): Boolean

}