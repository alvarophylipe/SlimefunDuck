package me.mrCookieSlime.Slimefun.api.inventory

import io.github.bakedlibs.dough.inventory.InvUtils
import io.github.bakedlibs.dough.items.ItemUtils
import io.github.thebusybiscuit.slimefun4.utils.itemstack.ItemStackWrapper
import me.mrCookieSlime.CSCoreLibPlugin.general.Inventory.ChestMenu
import org.bukkit.Material
import org.bukkit.block.Block
import org.bukkit.entity.HumanEntity
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import javax.annotation.Nonnull


abstract class DirtyChestMenu(
    var preset: BlockMenuPreset
) : ChestMenu(preset.title) {

    protected var changes = 1
    val unsavedChanges: Int get() = changes

    fun hasViewer() = toInventory()?.viewers?.isNotEmpty() == true
    fun markDirty() = changes++
    fun isDirty() = changes > 0

    fun canOpen(b: Block, p: Player): Boolean = preset.canOpen(b, p)

    override fun open(vararg players: Player) {
        super.open(*players)
        markDirty()
    }

    fun close() = toInventory()?.viewers?.toList()?.forEach(HumanEntity::closeInventory)


    fun fits(@Nonnull item: ItemStack, vararg slots: Int): Boolean {
        if (item.type == Material.AIR) return false

        return slots.any { getItemInSlot(it) == null } ||
                InvUtils.fits(toInventory()!!, ItemStackWrapper.wrap(item), *slots)
    }

    fun pushItem(item: ItemStack, vararg slots: Int): ItemStack? {
        require(item.type != Material.AIR) { "Cannot push AIR" }

        var left = item.amount
        val wrapper = ItemStackWrapper.wrap(item)


        for (slot in slots) {
            if (left <= 0) return null

            val stack = getItemInSlot(slot)

            if (stack == null) {
                replaceExistingItem(slot, item.clone().apply { amount = left })
                return null
            }

            val max = minOf(stack.maxStackSize, toInventory()!!.maxStackSize)

            if (stack.amount < max && ItemUtils.canStack(wrapper, stack)) {
                val move = max - stack.amount
                stack.amount = minOf(stack.amount + left, max)
                left -= move
            }
        }

        return if (left > 0) ItemStack(item.type, left) else null
    }

    fun consumeItem(slot: Int, amount: Int = 1, replaceConsumables: Boolean = false) {
        ItemUtils.consumeItem(getItemInSlot(slot)!!, amount, replaceConsumables)
        markDirty()
    }

    override fun replaceExistingItem(slot: Int, item: ItemStack?) = replaceExistingItem(slot, item, true)

    fun replaceExistingItem(slot: Int, item: ItemStack?, event: Boolean = true) {
        var newItem = item

        if (event) {
            val previous = getItemInSlot(slot)
            newItem = preset.onItemStackChange(this, slot, previous, item)
        }

        super.replaceExistingItem(slot, newItem)
        markDirty()
    }
}
