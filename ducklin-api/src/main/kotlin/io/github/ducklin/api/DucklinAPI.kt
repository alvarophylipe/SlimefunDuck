package io.github.ducklin.api

import io.github.ducklin.api.providers.RegistryProvider
import io.github.ducklin.api.providers.ServiceProvider
import org.bukkit.configuration.file.FileConfiguration
import org.bukkit.plugin.java.JavaPlugin
import java.util.logging.Logger

object DucklinAPI {

    lateinit var services: ServiceProvider; private set

    lateinit var registry: RegistryProvider; private set

    @JvmStatic
    val plugin: JavaPlugin; get() = services.plugin

    @JvmStatic
    val logger: Logger; get() = services.plugin.logger

    val config: FileConfiguration; get() = plugin.config

    val minecraftVersion: MinecraftVersion = MinecraftVersion.UNKNOWN

    fun setServiceProvider(provider: ServiceProvider) {
        if (::services.isInitialized) {
            throw IllegalStateException("Service Proviced can not be initialized twice times")
        }

        services = provider
    }

    fun setRegistryProvider(provider: RegistryProvider) {
        if (::registry.isInitialized) {
            throw IllegalStateException("Registry Proviced can not be initialized twice times")
        }

        registry = provider
    }

}