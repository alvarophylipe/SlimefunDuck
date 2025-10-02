package io.github.ducklin.api.services.localization

import io.github.bakedlibs.dough.common.ChatColors
import io.github.bakedlibs.dough.config.Config
import io.github.bakedlibs.dough.items.CustomItemStack
import io.github.ducklin.api.AbstractExtension
import io.github.ducklin.api.DucklinAPI
import io.github.ducklin.api.MinecraftVersion
import io.github.ducklin.api.recipes.RecipeType
import net.kyori.adventure.text.minimessage.MiniMessage
import org.bukkit.Keyed
import org.bukkit.Material
import org.bukkit.NamespacedKey
import org.bukkit.command.CommandSender
import org.bukkit.configuration.file.FileConfiguration
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemFlag
import org.bukkit.inventory.ItemStack

private val miniMessage = MiniMessage.miniMessage()

abstract class Localization(protected val plugin: AbstractExtension) : Keyed {

    protected val defaultConfig = Config(plugin, "messages.yml")

    protected fun save() = defaultConfig.save()

    open val chatPrefix: String; get() = getMessage("prefix")

    abstract fun getLanguage(id: String): Language?
    abstract fun getLanguage(p: Player): Language?
    abstract val defaultLanguage: Language?
    protected abstract fun hasLanguage(id: String): Boolean
    abstract fun getLanguages(): Collection<Language>
    protected abstract fun addLanguage(id: String, texture: String)

    protected fun loadEmbeddedLanguages() =
        LanguagePreset.entries
            .filter { it.releaseReady }
            .forEach { addLanguage(LanguagePreset.ENGLISH.code, it.textureHash) }

    private fun defaultFile(file: LanguageFile): FileConfiguration {
        val lang = getLanguage(LanguagePreset.ENGLISH.code) ?: error("Fallback language \"en\" is missing!")

        return lang.getFile(file) ?: error("Fallback file: \"${file.path("en")}\" is missing!")
    }

    private fun getStringOrNull(language: Language?, file: LanguageFile, path: String): String? {
        require(path.isNotBlank())

        val cfg = language?.getFile(file) ?: defaultFile(file)
        return cfg.getString(path)
    }

    private fun getString(language: Language?, file: LanguageFile, path: String): String =
        getStringOrNull(language, file, path) ?: "! Missing string \"$path\""

    private fun getStringListOrNull(language: Language?, file: LanguageFile, path: String): List<String>? {
        require(path.isNotBlank())

        val cfg = language?.getFile(file) ?: defaultFile(file)

        return cfg.getStringList(path).takeIf { it.isNotEmpty() }
    }

    private fun getStringList(language: Language?, file: LanguageFile, path: String): List<String> =
        getStringListOrNull(language, file, path) ?: listOf("! Missing string list \"$path\"")

    fun getMessage(key: String): String =
        defaultLanguage?.getFile(LanguageFile.MESSAGES)?.getString(key)
            ?: defaultFile(LanguageFile.MESSAGES).getString(key)
            ?: "! Missing key \"$key\""

    fun getMessage(player: Player, key: String): String =
        getString(getLanguage(player), LanguageFile.MESSAGES, key)

    fun getDefaultMessages(key: String): List<String> = defaultConfig.getStringList(key)

    fun getMessages(player: Player, key: String): List<String> =
        getStringList(getLanguage(player), LanguageFile.MESSAGES, key)

    fun getMessages(player: Player, key: String, function: (String) -> String): List<String> =
        getMessages(player, key).map(function)

    fun getResearchName(player: Player, key: NamespacedKey): String? =
        getStringOrNull(getLanguage(player), LanguageFile.RESEARCHES, "${key.namespace}.${key.key}")

    fun getItemGroupName(player: Player, key: NamespacedKey): String? =
        getStringOrNull(getLanguage(player), LanguageFile.CATEGORIES, "${key.namespace}.${key.key}")

    fun getResourceString(player: Player, key: String): String? =
        getStringOrNull(getLanguage(player), LanguageFile.RESOURCES, key)

    fun getRecipeTypeItem(player: Player, recipeType: RecipeType): ItemStack {
        require(recipeType.key != null)
        val item = recipeType.toItem() ?: return ItemStack(Material.AIR)

        val lang = getLanguage(player)
        val key = recipeType.key

        return CustomItemStack(item) { meta ->
            val name = getStringOrNull(lang, LanguageFile.RECIPES, "${key.namespace}.${key.key}.name")
            val lore = getStringListOrNull(lang, LanguageFile.RECIPES, "${key.namespace}.${key.key}.lore")

            name?.let { meta.displayName(miniMessage.deserialize("<aqua>$it")) }
            lore?.let {
                meta.lore(it.map { line -> miniMessage.deserialize("<gray>$line") })
            }
            meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES, ItemFlag.HIDE_ENCHANTS)
        }
    }

    fun sendMessage(recipient: CommandSender, key: String, addPrefix: Boolean = true) {
        val prefix = if (addPrefix) chatPrefix else ""
        val msg = prefix + getMessage(recipient as Player, key)
        recipient.sendMessage(miniMessage.deserialize(msg))
    }

    fun sendActionBarMessage(player: Player, key: String, addPrefix: Boolean = true) {
        val prefix = if (addPrefix) chatPrefix else ""
        val msg = prefix + getMessage(player, key)
        player.sendActionBar(miniMessage.deserialize(ChatColors.color(msg)))
    }

    fun sendMessage(recipient: CommandSender, key: String, function: (String) -> String) =
        sendMessage(recipient, key, true, function)

    fun sendMessage(
        recipient: CommandSender,
        key: String,
        addPrefix: Boolean = true,
        function: (String) -> String
    ) {
        if (DucklinAPI.minecraftVersion == MinecraftVersion.UNIT_TEST) return
        val prefix = if (addPrefix) chatPrefix else ""
        val raw = function(getMessage(recipient as Player, key))
        val colored = ChatColors.color(prefix + raw)
        recipient.sendMessage(miniMessage.deserialize(colored))
    }

    fun sendMessages(recipient: CommandSender, key: String) {
        val prefix = chatPrefix
        val msgs = if (recipient is Player) getMessages(recipient, key) else getDefaultMessages(key)
        msgs.forEach { line ->
            val msg = ChatColors.color(prefix + line)
            recipient.sendMessage(miniMessage.deserialize(msg))
        }
    }

    fun sendMessages(
        recipient: CommandSender,
        key: String,
        addPrefix: Boolean = true,
        function: (String) -> String
    ) {
        val prefix = if (addPrefix) chatPrefix else ""
        val msgs = if (recipient is Player) getMessages(recipient, key) else getDefaultMessages(key)
        msgs.forEach { line ->
            val msg = ChatColors.color(prefix + function(line))
            recipient.sendMessage(miniMessage.deserialize(msg))
        }
    }

    fun sendMessages(recipient: CommandSender, key: String, function: (String) -> String) =
        sendMessages(recipient, key, true, function)

    protected fun getTotalKeys(lang: Language): Set<String> = getKeys(*lang.getFiles())

    protected fun getKeys(vararg files: FileConfiguration): Set<String> =
        files.flatMapTo(mutableSetOf()) { it.getKeys(true) }

    override fun getKey(): NamespacedKey = NamespacedKey(DucklinAPI.plugin, "localization")

}