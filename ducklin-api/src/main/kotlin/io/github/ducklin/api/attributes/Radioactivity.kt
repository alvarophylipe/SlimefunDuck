package io.github.ducklin.api.attributes

import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor

enum class Radioactivity(
    val color: NamedTextColor,
    val exposureModifier: Int
) {

    LOW(NamedTextColor.YELLOW, 1),
    MODERATE(NamedTextColor.YELLOW, 2),
    HIGH(NamedTextColor.GOLD, 3),
    VERY_HIGH(NamedTextColor.RED, 5),
    VERY_DEADLY(NamedTextColor.DARK_RED, 10);

    val radiationLevel = ordinal + 1

    fun getLore(): Component =
        Component.text("☢", NamedTextColor.GOLD)
            .append(Component.text("Radiation Level: ", NamedTextColor.GRAY))
            .append(Component.text(toString().replace("_", " "), color))

}