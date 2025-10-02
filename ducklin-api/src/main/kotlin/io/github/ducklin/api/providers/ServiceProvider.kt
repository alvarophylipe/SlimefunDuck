package io.github.ducklin.api.providers

import io.github.ducklin.api.AbstractExtension
import io.github.ducklin.api.services.BlockStorageService
import io.github.ducklin.api.services.CapacitorService
import io.github.ducklin.api.services.ItemDataService
import io.github.ducklin.api.services.ItemTextureService
import io.github.ducklin.api.services.LocalizationService
import io.github.ducklin.api.services.hologram.HologramService
import org.bukkit.World
import java.util.logging.Logger

interface ServiceProvider {

    val plugin: AbstractExtension

    val logger: Logger

    val capacitorService: CapacitorService
    val itemDataService: ItemDataService
    val itemTextureService: ItemTextureService
    val localizationService: LocalizationService
    val hologramService: HologramService

    fun getStorage(w: World): BlockStorageService

}