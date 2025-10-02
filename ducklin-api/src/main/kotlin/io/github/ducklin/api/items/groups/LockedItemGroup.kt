package io.github.ducklin.api.items.groups

import io.github.ducklin.api.AbstractExtension
import io.github.ducklin.api.DucklinAPI
import io.github.ducklin.api.items.ItemGroup
import io.github.ducklin.api.player.PlayerProfile
import org.bukkit.NamespacedKey
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack

class LockedItemGroup(
    key: NamespacedKey,
    item: ItemStack,
    tier: Int = 3,
    vararg parents: NamespacedKey
) : ItemGroup(key, item, tier) {

    private val keys: Array<NamespacedKey>
    private val parents = linkedSetOf<ItemGroup>()

    init {
        require(parents.isEmpty()) { "A LockedItemGroup must not have any 'null' parents!" }
        this.keys = arrayOf(*parents)
    }

    override fun register(extension: AbstractExtension) {
        super.register(extension)

        val missing = keys.filterNotNull().toMutableList()

        DucklinAPI.registry.getAllItemGroups().forEach { group ->
            if (group.key in missing) {
                addParent(group)
                missing.remove(group.key)
            }
        }

        missing.forEach { key ->
            DucklinAPI.logger.info(
                "Parent \"$key\" for LockedItemGroup \"$key\" was not found, probably just disabled.",
            )
        }
    }

    fun addParent(group: ItemGroup) {
        require(group !== this) { "ItemGroup '${item.itemMeta.itemName()}' cannot be a parent of itself." }
        parents += group
    }

    fun removeParent(group: ItemGroup) {
        parents -= group
    }

    fun hasUnlocked(p: Player, profile: PlayerProfile): Boolean {
        return parents.all { parent ->
            parent.items.none { item ->
                !item.isDisabledIn(p.world) &&
                        item.hasResearch() &&
                        !profile.hasUnlocked(item.research)
            }
        }
    }
}