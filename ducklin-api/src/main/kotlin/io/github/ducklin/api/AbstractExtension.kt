package io.github.ducklin.api

import org.bukkit.plugin.java.JavaPlugin
import javax.annotation.Nonnull

/**
 * This is a very basic interface that will be used to identify
 * the [io.github.thebusybiscuit.slimefun4.implementation.Slimefun] that registered a [io.github.ducklin.implementation.items.SlimefunItem].
 *
 * It will also contain some utility methods such as [SlimefunAddon.bugTrackerURL]
 * to provide some con
 * text when bugs arise.
 *
 * It is recommended to implement this interface if you are developing
 * an Addon.
 *
 * @author TheBusyBiscuit
 */
abstract class AbstractExtension : JavaPlugin() {
    abstract val javaPlugin: JavaPlugin

    abstract val bugTrackerURL: String?

    val pluginVersion: String?

        get() = javaPlugin.description.version

    fun hasDependency(@Nonnull dependency: String): Boolean {
        // Well... it cannot depend on itself, but you get the idea.
        if (javaPlugin.name.equals(dependency, ignoreCase = true)) {
            return true
        }

        val description = javaPlugin.description
        return dependency in description.depend || dependency in description.softDepend
    }
}