package io.github.ducklin.api.attributes

import org.bukkit.inventory.meta.ItemMeta

interface DistinctiveItem : ItemAttribute {
    fun canStack(itemMetaOne: ItemMeta, itemMetaTwo: ItemMeta): Boolean
}