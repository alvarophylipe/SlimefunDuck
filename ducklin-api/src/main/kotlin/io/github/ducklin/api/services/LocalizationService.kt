package io.github.ducklin.api.services

import io.github.ducklin.api.AbstractExtension
import io.github.ducklin.api.DucklinAPI
import io.github.ducklin.api.helpers.Patterns
import io.github.ducklin.api.services.localization.Language
import io.github.ducklin.api.services.localization.LanguageFile
import io.github.ducklin.api.services.localization.Localization
import org.bukkit.NamespacedKey
import org.bukkit.configuration.file.YamlConfiguration
import org.bukkit.entity.Player
import org.bukkit.persistence.PersistentDataType
import java.io.BufferedReader
import java.io.InputStreamReader
import java.nio.charset.StandardCharsets

class LocalizationService(
    plugin: AbstractExtension,
    prefix: String?,
    serverDefaultLanguage: String?
) : Localization(plugin) {

    companion object {
        private const val LANGUAGE_PATH = "language"
    }

    val languages = linkedMapOf<String, Language>()
    private val languageKey = NamespacedKey(plugin, LANGUAGE_PATH)
    private val translationsEnabled = serverDefaultLanguage != null && DucklinAPI.config.getBoolean("options.enabled-translations")

    override val defaultLanguage: Language? = serverDefaultLanguage?.let {
        Language(it,
            "11b3188fd44902f72602bd7c2141f5a70673a411adb3d81862c69e536166b").apply {
            setFile(LanguageFile.MESSAGES, defaultConfig.configuration)
            loadEmbeddedLanguages()

        }
    }

    override val chatPrefix: String = prefix ?: ""

    override fun getKey(): NamespacedKey = languageKey

    override fun getLanguage(id: String): Language? = languages[id]

    override fun getLanguages(): Collection<Language> = languages.values

    override fun hasLanguage(id: String): Boolean =
        getConfigurationFromStream(LanguageFile.MESSAGES.path(id))
            .getKeys(false)
            .isNotEmpty()

    fun isLanguageLoaded(id: String): Boolean = languages.containsKey(id)

    fun getDefaultLanguage(): Language? = defaultLanguage

    override fun getLanguage(p: Player): Language =
        p.persistentDataContainer
            .get(languageKey, PersistentDataType.STRING)
            ?.let { languages[it] }
            ?: defaultLanguage!!


    private fun loadServerLanguage(code: String) {
        val reset = code != defaultConfig.getString(LANGUAGE_PATH)
        if (reset) defaultConfig.clear()

        LanguageFile.entries
            .filter { it != LanguageFile.MESSAGES }
            .forEach { copyToDefaultLanguage(code, it) }

        defaultConfig.setValue(LANGUAGE_PATH, code)

        val path = "/language/$code/messages.yml"
        plugin.javaClass.getResourceAsStream(path)?.use { stream ->
            BufferedReader(InputStreamReader(stream, StandardCharsets.UTF_8)).use { reader ->
                val yaml = YamlConfiguration.loadConfiguration(reader)
                defaultConfig.configuration.setDefaults(yaml)
            }
        } ?: DucklinAPI.logger.severe("Failed to load language file: \"$path\"")

        DucklinAPI.logger.info("Loaded language \"$code\"")
        DucklinAPI.logger.info("Available languages: ${languages.keys.joinToString()}")
        save()
    }

    private fun copyToDefaultLanguage(code: String, file: LanguageFile) {
        defaultLanguage?.setFile(file, getConfigurationFromStream(file.path(code)))
    }

    override fun addLanguage(id: String, texture: String) {
        if (!hasLanguage(id)) return
        val lang = Language(id, texture)
        LanguageFile.entries.forEach { file ->
            val defaults = if (file == LanguageFile.MESSAGES) defaultConfig else return
            lang.setFile(file, getConfigurationFromStream(file.path(lang), YamlConfiguration.loadConfiguration(defaults.file)))
        }
        languages[id] = lang
    }

    private fun getConfigurationFromStream(
        file: String,
        defaults: YamlConfiguration? = null
    ): YamlConfiguration {
        val stream = plugin.javaClass.getResourceAsStream(file) ?: return YamlConfiguration()
        return stream.use {
            val content = BufferedReader(InputStreamReader(it, StandardCharsets.UTF_8)).readText()
            if (!Patterns.YAML_ENTRY.matcher(content).matches()) return YamlConfiguration()
            YamlConfiguration.loadConfiguration(content.reader()).apply {
                if (defaults != null) setDefaults(defaults)
            }
        }
    }
}