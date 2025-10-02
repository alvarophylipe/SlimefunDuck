package io.github.ducklin.api.services

import io.github.ducklin.api.items.DucklinItem
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.meta.ItemMeta

interface ItemTextureService {

    fun register(items: Collection<DucklinItem>, save: Boolean)

    fun setTexture(item: ItemStack, id: String)

    fun setTexture(meta: ItemMeta, id: String)

}