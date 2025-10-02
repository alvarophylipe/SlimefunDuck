package io.github.ducklin.api.attributes

import io.github.ducklin.api.DucklinAPI
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack

interface RecipeDisplayItem : ItemAttribute {
    val displayRecipes: List<ItemStack>
    fun getLabelLocalPath(): String =  "guide.tooltips.recipes.machine"
    fun getRecipeSectionLabel(p: Player): String =
        "&7\u21E9 " + DucklinAPI.services.localizationService.getMessage(p, getLabelLocalPath()) + " \u21E9"
}