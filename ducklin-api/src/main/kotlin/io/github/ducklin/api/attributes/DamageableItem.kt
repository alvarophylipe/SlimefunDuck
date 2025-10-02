package io.github.ducklin.api.attributes

import io.github.ducklin.api.helpers.UnbreakingAlgorithm
import org.bukkit.Sound
import org.bukkit.enchantments.Enchantment
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.meta.Damageable

interface DamageableItem : ItemAttribute {

    val isDamageable: Boolean

    fun damageItem(p: Player, item: ItemStack?) {
        if (!isDamageable || item == null || item.type.isAir || item.amount <= 0) return

        val unbreaking = item.getEnchantmentLevel(Enchantment.UNBREAKING)

        if (evaluateUnbreakingEnchantment(unbreaking)) return

        item.itemMeta?.takeIf { !it.isUnbreakable }?.let { meta ->
            val damageable = meta as? Damageable ?: return
            if (damageable.damage >= item.type.maxDurability.toInt()) {
                p.playSound(p.eyeLocation, Sound.ENTITY_ITEM_BREAK, 1f, 1f)
                item.amount = 0
            } else {
                damageable.damage += 1
                item.itemMeta = meta
            }
        }
    }

    fun evaluateUnbreakingEnchantment(unbreakingLevel: Int): Boolean = UnbreakingAlgorithm.ITEM.evaluate(unbreakingLevel)
}