package io.github.ducklin.menu

import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.event.inventory.InventoryCloseEvent
import org.bukkit.plugin.Plugin
import java.util.*


class MenuListener(plugin: Plugin) : Listener {
    init {
        plugin.server.pluginManager.registerEvents(this, plugin)
    }

    @EventHandler
    fun onClose(e: InventoryCloseEvent) {
        val menu: ChestMenu? = menus.remove(e.player.uniqueId)
        menu?.getMenuCloseHandler()?.onClose(e.player as? Player ?: return)
    }

    @EventHandler
    fun onClick(e: InventoryClickEvent) {
        val menu = menus[e.whoClicked.uniqueId] ?: return
        val player = e.whoClicked as? Player ?: return
        val action = ClickAction(e.isRightClick, e.isShiftClick)

        if (e.rawSlot.compareTo(e.inventory.size)) {
            val handler = menu.getMenuClickHandler(e.slot)

            when {
                handler == null -> e.isCancelled = !menu.isEmptySlotsClickable() && (e.currentItem == null ||
                        e.currentItem?.type == Material.AIR)

                handler is ChestMenu.AdvancedMenuClickHandler -> e.isCancelled = !handler.onClick(e, player, e.slot, e.cursor, action)

                else -> e.isCancelled = !handler.onClick(player, e.slot, e.currentItem, action)
            }
        } else {
            e.isCancelled = !menu.getPlayerInventoryClickHandler().onClick(player, e.slot, e.currentItem, action)
        }

    }

    companion object {
        @JvmField
        val menus: MutableMap<UUID, ChestMenu> = mutableMapOf()
    }
}
