package me.mrCookieSlime.Slimefun.api.inventory

import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItem
import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItem.Companion.getById
import io.github.thebusybiscuit.slimefun4.implementation.Slimefun
import io.github.thebusybiscuit.slimefun4.implementation.Slimefun.Companion.getRegistry
import io.github.thebusybiscuit.slimefun4.implementation.Slimefun.Companion.runSync
import io.github.thebusybiscuit.slimefun4.utils.ChestMenuUtils
import me.mrCookieSlime.CSCoreLibPlugin.general.Inventory.ChestMenu
import me.mrCookieSlime.Slimefun.api.item_transport.ItemTransportFlow
import org.bukkit.Location
import org.bukkit.block.Block
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack

abstract class BlockMenuPreset (
    val id: String,
    val title: String,
    private val universal: Boolean = false
) : ChestMenu(title) {

    private val occupiedSlots = mutableSetOf<Int>()
    var size: Int = -1
    private var locked = false

    init {
        require(id.isNotBlank()) { "You need to specify an id!" }
        init()
        getRegistry().menuPresets[id] = this
    }

    abstract fun init()
    abstract fun canOpen(b: Block, p: Player): Boolean
    abstract fun getSlotsAccessedByItemTransport(flow: ItemTransportFlow): IntArray

    open fun onItemStackChange(
        menu: DirtyChestMenu,
        slot: Int,
        previous: ItemStack?,
        next: ItemStack?
    ): ItemStack? = next

    open fun newInstance(menu: BlockMenu, b: Block) {}

    open fun getSlotsAccessedByItemTransport(
        menu: DirtyChestMenu,
        flow: ItemTransportFlow,
        item: ItemStack?
    ): IntArray = getSlotsAccessedByItemTransport(flow)

    final override fun replaceExistingItem(slot: Int, item: ItemStack?) {
        error("BlockMenuPreset does not support this method.")
    }

    final override fun addItem(slot: Int, item: ItemStack?): ChestMenu {
        checkIfLocked()
        occupiedSlots += slot
        return super.addItem(slot, item)
    }

    final override fun addMenuClickHandler(slot: Int, handler: MenuClickHandler): ChestMenu {
        checkIfLocked()
        return super.addMenuClickHandler(slot, handler)
    }

    private fun checkIfLocked() = check(!locked) {
        "You cannot modify the BlockMenuPreset anymore, modify the individual instances instead."
    }

    fun drawBackground(item: ItemStack, slots: IntArray) {
        require(slots.isNotEmpty()) {  "Slots cannot be empty." }
        checkIfLocked()
        slots.forEach { addItem(it, item, ChestMenuUtils.getEmptyClickHandler()) }
    }

    fun drawBackground(slots: IntArray) = drawBackground(ChestMenuUtils.getBackground(), slots)

    fun setSize(size: Int): ChestMenu {
        checkIfLocked()

        require(size % 9 == 0 && size in 0..54) {
            "Size must be multiple of 9 (0-54), got: $size"
        }

        this.size = size
        return this
    }

    val presetSize: Int get() = size
    val presetSlots: Set<Int> get() = occupiedSlots.toSet()

    val inventorySlots: Set<Int>
        get() = buildSet {
            val limit = if (size == -1) toInventory()!!.size else size
            for (i in 0 until limit) if (i !in occupiedSlots) add(i)
        }

    fun clone(menu: DirtyChestMenu) {
        menu.setPlayerInventoryClickable(true)

        occupiedSlots.forEach { menu.addItem(it, getItemInSlot(it)) }

        if (size > -1) menu.addItem(size - 1, null)

        if (menu is BlockMenu) {
            runSync {
                runCatching { newInstance(menu, menu.block.location) }
                    .onFailure { slimefunItem.error("Failed to create BlockMenu", Exception("Failed to create BlockMenu")) }
            }
        }

        for (slot in 0..53) getMenuClickHandler(slot)?.let {
            menu.addMenuClickHandler(slot, it)
        }

        menu.addMenuOpeningHandler(getMenuOpeningHandler())
        menu.addMenuCloseHandler(getMenuCloseHandler())
    }

    fun newInstance(menu: BlockMenu, l: Location) {
        require(!l.world!!.isChunkLoaded(l.blockX shr 4, l.blockZ shr 4)) {
            "Cannot create BlockMenu asynchronously"
        }

        runSync {
            locked = true
            runCatching { newInstance(menu, l.block) }
                .onFailure { slimefunItem.error("Failed to create BlockMenu", it) }
        }
    }

    val slimefunItem: SlimefunItem get() = getById(id)!!

    companion object {
        fun getPreset(id: String?): BlockMenuPreset? = id?.let { getRegistry().menuPresets[it] }
        fun isInventory(id: String): Boolean = getRegistry().menuPresets.contains(id)
        fun isUniversalInventory(id: String): Boolean = getRegistry().menuPresets[id]?.universal == true
    }
}
