package io.github.ducklin.api.attributes

import io.github.ducklin.api.attributes.interactions.InteractionResult
import org.bukkit.Location

interface ExternallyInteractable {
    fun onInteract(location: Location): InteractionResult
}