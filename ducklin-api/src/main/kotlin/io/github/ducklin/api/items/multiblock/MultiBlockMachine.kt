package io.github.ducklin.api.items.multiblock

import io.github.ducklin.api.attributes.NotPlaceable
import io.github.ducklin.api.attributes.RecipeDisplayItem
import io.github.ducklin.api.items.DucklinItem
import io.github.ducklin.api.items.DucklinItemStack
import io.github.ducklin.api.items.ItemGroup
import io.github.ducklin.api.recipes.RecipeType
import org.bukkit.block.BlockFace
import org.bukkit.inventory.ItemStack

class MultiBlockMachine(
    itemGroup: ItemGroup,
    item: DucklinItemStack,
    recipe: Array<ItemStack?>,
    machineRecipes: List<ItemStack> = emptyList(),
    trigger: BlockFace
) : DucklinItem(itemGroup, item, RecipeType.MULTIBLOCK, recipe), NotPlaceable, RecipeDisplayItem {



}