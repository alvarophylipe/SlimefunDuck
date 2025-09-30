@file:Suppress("DEPRECATION")

package io.github.thebusybiscuit.slimefun4.api

import com.github.shynixn.mccoroutine.bukkit.SuspendingJavaPlugin
import org.apache.commons.lang.Validate
import org.bukkit.plugin.java.JavaPlugin
import javax.annotation.Nonnull
import io.github.thebusybiscuit.slimefun4.implementation.Slimefun
import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItem

/**
 * This is a very basic interface that will be used to identify
 * the [Slimefun] that registered a [SlimefunItem].
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
abstract class SlimefunAddon : JavaPlugin() {
    abstract val javaPlugin: JavaPlugin

    /**
     * This method returns a link to the Bug Tracker of this [SlimefunAddon]
     *
     * @return The URL for this Plugin's Bug Tracker, or null
     */
    abstract val bugTrackerURL: String?

    val pluginVersion: String?
        /**
         * This method returns the version of this addon, it defaults to the version
         * of the [JavaPlugin] provided by [SlimefunAddon.javaPlugin]
         *
         * @return The version of this [SlimefunAddon]
         */
        get() = javaPlugin.description.version

    /**
     * This method checks whether the given String is the name of a dependency of this
     * [SlimefunAddon].
     * It specifically checks whether the given String can be found in [org.bukkit.plugin.PluginDescriptionFile.getDepend]
     * or [org.bukkit.plugin.PluginDescriptionFile.getSoftDepend]
     *
     * @param dependency
     * The dependency to check for
     *
     * @return Whether this [SlimefunAddon] depends on the given [Slimefun]
     */
    fun hasDependency(@Nonnull dependency: String): Boolean {
        Validate.notNull(dependency, "The dependency cannot be null")

        // Well... it cannot depend on itself, but you get the idea.
        if (javaPlugin.name.equals(dependency, ignoreCase = true)) {
            return true
        }

        val description = javaPlugin.description
        return dependency in description.depend || dependency in description.softDepend
    }
}