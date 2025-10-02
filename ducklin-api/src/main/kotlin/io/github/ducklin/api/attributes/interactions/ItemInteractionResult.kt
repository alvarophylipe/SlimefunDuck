package io.github.ducklin.api.attributes.interactions

import org.bukkit.inventory.ItemStack
import java.util.Collections

class ItemInteractionResult(
    val success: Boolean
) : InteractionResult(success) {

    constructor(
        success: Boolean,
        vararg itemStacks: ItemStack
    ) : this(success) {
        addResultItems(*itemStacks)
    }

    val resultItems: MutableSet<ItemStack> = mutableSetOf()

    fun addResultItems(vararg itemStacks: ItemStack) {
        resultItems.addAll(itemStacks)
    }

    fun resultedItems(): Boolean = resultItems.isNotEmpty()

    fun getResultItems(): Set<ItemStack> = resultItems.toSet()

}