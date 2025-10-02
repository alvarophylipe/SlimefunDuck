@file:Suppress("USELESS_IS_CHECK")

package io.github.ducklin.api.services.localization

import io.github.ducklin.api.DucklinAPI
import io.github.ducklin.api.helpers.DucklinHelper
import org.bukkit.configuration.file.FileConfiguration
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import java.util.EnumMap

class Language (
    val id: String,
    hash: String
) {
    private val files: EnumMap<LanguageFile, FileConfiguration> = EnumMap(LanguageFile::class.java)

    val item: ItemStack? = null

    private val process: Double by lazy {
        when (id) {
            "en" -> 100.0
            else -> 0.0
        }
    }

    fun translationProgress(): Double = process

    internal fun getFile(file: LanguageFile): FileConfiguration? = files[file]

    fun setFile(file: LanguageFile, configuration: FileConfiguration) {
        require(file !is LanguageFile ) { "File must not be null" }
        require(configuration != FileConfiguration::class) { "Config must not be null" }
        files[file] = configuration
    }

    fun getName(p: Player): String = DucklinAPI.services.localizationService.getMessage(p, "languages.$id")

    fun isDefault(): Boolean = this == DucklinAPI.services.localizationService.defaultLanguage

    override fun toString(): String = "Language(id='$id', default='${isDefault()}')"

    internal fun getFiles(): Array<FileConfiguration> = LanguageFile.valuesCached.map { files[it]!! }.toTypedArray()

}