package io.github.ducklin.menu

import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer
import org.bukkit.Bukkit
import org.bukkit.ChatColor
import org.bukkit.entity.Player
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.inventory.Inventory
import org.bukkit.inventory.ItemStack


open class ChestMenu(private val title: String) {

    private var clickable = false
    private var emptyClickable = true
    private var inv: Inventory? = null
    private val items = mutableListOf<ItemStack?>()
    private val handlers = mutableMapOf<Int, MenuClickHandler>()
    var open: MenuOpeningHandler = MenuOpeningHandler {  }
    private var close: MenuCloseHandler = MenuCloseHandler {  }
    private var playerclick: MenuClickHandler = MenuClickHandler { _, _, _, _ -> isPlayerInventoryClickable() }

    init {
        ChatColor.translateAlternateColorCodes('&', title)
    }

    fun setPlayerInventoryClickable(clickable: Boolean) = apply { this.clickable = clickable }

    fun isPlayerInventoryClickable() = clickable

    fun setEmptySlotsClickable(emptyClickable: Boolean) = apply { this.emptyClickable = emptyClickable }

    fun isEmptySlotsClickable() = emptyClickable

    fun addPlayerInventoryClickHandler(handler: MenuClickHandler) = apply { playerclick = handler }

    open fun addItem(slot: Int, item: ItemStack?) = apply {
        while (items.size <= slot) items.add(null)
        items[slot] = item
    }

    open fun addItem(slot: Int, item: ItemStack?, clickHandler: MenuClickHandler) = apply {
        addItem(slot, item)
        addMenuClickHandler(slot, clickHandler)
    }

    fun getItemInSlot(slot: Int): ItemStack? {
        setup()
        return inv?.getItem(slot)
    }

    open fun addMenuClickHandler(slot: Int, handler: MenuClickHandler) = apply { handlers[slot] = handler }

    open fun addMenuOpeningHandler(handler: MenuOpeningHandler) = apply { open = handler }

    open fun addMenuCloseHandler(handler: MenuCloseHandler) = apply { close = handler }

    fun build() = this

    fun getContents(): Array<ItemStack?> {
        setup()
        return inv?.contents ?: emptyArray()
    }

    private fun setup() {
        if (inv != null) return
        val size = ((items.size + 8) / 9) * 9
        inv = Bukkit.createInventory(null, size, LegacyComponentSerializer.legacyAmpersand().deserialize(title))
        items.forEachIndexed { index, item -> inv?.setItem(index, item?.clone()) }
    }

    fun reset(update: Boolean) {
        if (update) inv?.clear() else setup()
        items.forEachIndexed { index, item -> inv?.setItem(index, item?.clone()) }
    }

    open fun replaceExistingItem(slot: Int, item: ItemStack?) {
        setup()
        inv?.setItem(slot, item)
    }

    fun getMenuClickHandler(slot: Int) = handlers[slot]

    fun getMenuCloseHandler() = close

    fun getMenuOpeningHandler() = open

    fun getPlayerInventoryClickHandler() = playerclick

    fun toInventory(): Inventory? = inv

    fun interface MenuClickHandler {
        fun onClick(p: Player, slot: Int, item: ItemStack?, action: ClickAction): Boolean
    }

    interface AdvancedMenuClickHandler : MenuClickHandler {
        fun onClick(e: InventoryClickEvent, p: Player, slot: Int, cursor: ItemStack?, action: ClickAction): Boolean
    }

    fun interface MenuOpeningHandler {
        fun onOpen(p: Player)
    }

    fun interface MenuCloseHandler {
        fun onClose(p: Player)
    }

    open fun open(vararg players: Player) {
        setup()
        players.forEach { p ->
            p.openInventory(inv!!)
            MenuListener.menus[p.uniqueId] = this
            getMenuOpeningHandler().onOpen(p)
        }
    }
}
