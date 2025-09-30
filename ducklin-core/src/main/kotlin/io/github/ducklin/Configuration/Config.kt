package io.github.ducklin.Configuration

import org.bukkit.configuration.file.FileConfiguration
import org.bukkit.configuration.file.YamlConfiguration
import java.io.File
import kotlin.collections.set
import kotlin.text.contains
import kotlin.text.get


open class Config @JvmOverloads constructor(
    private val file: File?,
    private var config: FileConfiguration? = null
) {

    init {
        this.config = config ?: if (file != null)
            YamlConfiguration.loadConfiguration(file)
        else
            YamlConfiguration()
    }

    constructor(path: String) : this(File(path))

    open fun getFile(): File? = file

    open fun getConfiguration(): FileConfiguration? = config

    open fun setValue(path: String, value: Any?) {
        config?.set(path, value)
    }

    open fun save() {
        file?.let {
            f ->
            runCatching { config?.save(f) }
                .onFailure {  }
        }
    }

    open fun save(target: File? = file) {
        target?.let {
                f ->
            runCatching { config?.save(f) }
                .onFailure {  }
        }
    }

    open fun setDefaultValue(path: String, value: Any?) {
        contains(path)?.let { if (!it) setValue(path, value) }
    }

    open fun contains(path: String): Boolean = config?.contains(path) ?: false

    open fun getValue(path: String): Any? = config?.get(path)

    open fun getString(path: String): String? = config?.getString(path)

    open fun createFile() {
        file?.let { f ->
            runCatching { f.createNewFile() }
                .onFailure { /* silencioso */ }
        }
    }

    open fun getKeys(deep: Boolean = false): Set<String>? = config?.getKeys(deep)

    open fun getKeys(path: String): Set<String> = config?.getConfigurationSection(path)?.getKeys(false) ?: emptySet()

    open fun getKeys(): Set<String>? = getKeys(false)

    open fun reload() {
        config = file?.let { YamlConfiguration.loadConfiguration(it) }
    }
}
