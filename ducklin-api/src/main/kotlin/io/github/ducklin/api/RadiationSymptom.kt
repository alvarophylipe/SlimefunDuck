package io.github.ducklin.api

import org.bukkit.entity.Player
import org.bukkit.potion.PotionEffect
import org.bukkit.potion.PotionEffectType

enum class RadiationSymptom(
    val minExposure: Int,
    val effectType: PotionEffectType,
    val level: Int
) {

    SLOW(10, PotionEffectType.SLOWNESS, 3),
    WITHER_LOW(25, PotionEffectType.WITHER, 0),
    BLINDNESS(50, PotionEffectType.BLINDNESS, 4),
    WITHER_HIGH(75, PotionEffectType.WITHER, 3),
    IMMINENT_DEATH(100, PotionEffectType.INSTANT_DAMAGE, 49);

    private val potionEffect: PotionEffect
    private val updateInterval: Int

    init {
        require(minExposure > 0) { "The minimum exposure must be greater than 0." }
        require(level > 0) { "The status effect level must be non-negative." }

        updateInterval = DucklinAPI.config.getInt("options.radiation-update-interval", 1).also {
            DucklinAPI.config.set("options.radiation-update-interval", 1)
        }.let { it * 20 + 20 }

        potionEffect = PotionEffect(effectType, updateInterval, level)
    }

    fun apply(p: Player) = p.addPotionEffect(potionEffect)

    fun shouldApply(exposure: Int): Boolean = exposure > minExposure

}