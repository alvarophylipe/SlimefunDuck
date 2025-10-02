package io.github.ducklin.api.items.groups

import io.github.ducklin.api.guide.DucklinGuideMode
import io.github.ducklin.api.items.DucklinItem
import io.github.ducklin.api.items.ItemGroup
import io.github.ducklin.api.player.PlayerProfile
import org.bukkit.NamespacedKey
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack

abstract class FlexItemGroup (
    key: NamespacedKey,
    item: ItemStack,
    tier: Int = 3
) : ItemGroup(key, item, tier) {

    override fun isVisible(p: Player): Boolean = true

    abstract fun isVisible(p: Player, profile: PlayerProfile, layout: DucklinGuideMode): Boolean
    abstract fun open(p: Player, profile: PlayerProfile, layout: DucklinGuideMode)

    fun getItems(): List<DucklinItem> = throw UnsupportedOperationException("You cannot add items to a FlexItemGroup!")

    override fun add(item: DucklinItem) = throw UnsupportedOperationException("You cannot add items to a FlexItemGroup!")

    override fun contains(item: DucklinItem?): Boolean = throw UnsupportedOperationException("A FlexItemGroup has no items!")

    override fun remove(item: DucklinItem) = throw UnsupportedOperationException("A FlexItemGroup has no items, so there is nothing remove!")

}