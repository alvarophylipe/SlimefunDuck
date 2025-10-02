package io.github.ducklin.api.services.hologram

import net.kyori.adventure.text.minimessage.MiniMessage
import org.bukkit.Bukkit
import org.bukkit.entity.ArmorStand
import java.util.UUID
import java.util.concurrent.TimeUnit

class Hologram(private val uuid: UUID) {

    companion object {
        private val EXPIRES_AFTER = TimeUnit.MINUTES.toMillis(10)
    }
    private var lastAccess = System.currentTimeMillis()
    private var label: String? = null

    fun getArmorStand(): ArmorStand? =
        Bukkit.getEntity(uuid)?.takeIf { it.isValid && it is ArmorStand }?.let { entity ->
            lastAccess = System.currentTimeMillis()
            entity as ArmorStand
        } ?: run {
            lastAccess = 0
            null
        }

    fun hasDespawned(): Boolean = getArmorStand() == null

    fun hasExpired(): Boolean = System.currentTimeMillis() - lastAccess > EXPIRES_AFTER

    fun setLabel(newLabel: String?) {
        if (label == newLabel){
            lastAccess = System.currentTimeMillis()
            return
        }

        label = newLabel
        getArmorStand()?.apply {
            if (newLabel != null) {
                isCustomNameVisible = true
                customName(MiniMessage.miniMessage().deserialize(newLabel))
            } else {
                isCustomNameVisible = false
                customName(null)
            }
        }
    }

    fun remove() {
        getArmorStand()?.remove()
        lastAccess = 0
    }
}