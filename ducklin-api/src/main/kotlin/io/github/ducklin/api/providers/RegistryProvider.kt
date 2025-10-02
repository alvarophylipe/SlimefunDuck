package io.github.ducklin.api.providers

import io.github.ducklin.api.items.DucklinItem
import io.github.ducklin.api.items.ItemGroup
import org.bukkit.inventory.ItemStack

interface RegistryProvider {

    val automaticallyLoadItems: Boolean
    val enableResearches: Boolean
    val freeCreativeResearches: Boolean
    val researchFireworks: Boolean
    val disableLearningAnimation: Boolean
    val logDuplicateBlockEntries: Boolean
    val talismanActionBarMessages: Boolean

    fun getItemById(id: String): DucklinItem?

    fun getItemByItem(item: ItemStack): DucklinItem?

    fun getAllDucklinItems(): Collection<DucklinItem>

    fun getAllItemGroups(): Collection<ItemGroup>

    fun getEnabledDucklinItems(): Collection<DucklinItem>
}