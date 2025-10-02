package io.github.ducklin.api.helpers

import net.kyori.adventure.text.format.NamedTextColor
import java.util.regex.Pattern


object Patterns {
    val SLASH_SEPARATOR: Pattern = Pattern.compile(" / ")

    val YAML_ENTRY: Pattern = Pattern.compile("[a-z0-9_-]+:.*")

    val MINECRAFT_NAMESPACEDKEY: Pattern = Pattern.compile("minecraft:[a-z0-9/._-]+")

    val MINECRAFT_TAG: Pattern = Pattern.compile("#minecraft:[a-z_]+")
    val DUCKLIN_TAG: Pattern = Pattern.compile("#ducklin:[a-z_]+")

    val USES_LEFT_LORE: Pattern =
        Pattern.compile("${NamedTextColor.YELLOW}[0-9]+ Uses? ${NamedTextColor.GRAY}left")
}
