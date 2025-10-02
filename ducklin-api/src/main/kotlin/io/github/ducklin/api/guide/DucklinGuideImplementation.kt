package io.github.ducklin.api.guide

import io.github.ducklin.api.player.PlayerProfile
import org.bukkit.inventory.ItemStack

interface DucklinGuideImplementation {
    val mode: DucklinGuideMode
    val item: ItemStack
    fun openMainMenu(profile: PlayerProfile, page: Int)
    fun openItemGroup(profile: PlayerProfile, ItemGroup)

}