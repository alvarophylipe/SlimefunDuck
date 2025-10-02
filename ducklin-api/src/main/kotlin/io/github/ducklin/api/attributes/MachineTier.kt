package io.github.ducklin.api.attributes

import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor

enum class MachineTier(val prefix: String, val color: NamedTextColor) {

    BASIC("Basic", NamedTextColor.YELLOW),
    AVERAGE("Average", NamedTextColor.GOLD),
    MEDIUM("Medium", NamedTextColor.GREEN),
    GOOD("Good", NamedTextColor.DARK_GREEN),
    ADVANCED("Advanced", NamedTextColor.GOLD),
    END_GAME("End-Game", NamedTextColor.DARK_RED);

    fun getTier(): Component = Component.text(prefix, color)
    override fun toString(): String = prefix

}