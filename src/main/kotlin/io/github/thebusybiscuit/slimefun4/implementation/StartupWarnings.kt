package io.github.thebusybiscuit.slimefun4.implementation

import io.github.thebusybiscuit.slimefun4.implementation.Slimefun.Companion.supportedVersions
import io.github.thebusybiscuit.slimefun4.utils.NumberUtils
import java.lang.String
import java.util.logging.Level
import java.util.logging.Logger
import javax.annotation.ParametersAreNonnullByDefault
import kotlin.Int

/**
 * This class stores some startup warnings we occasionally need to print.
 * If you set up your server the recommended way, you are never going to see
 * any of these messages.
 *
 * @author TheBusyBiscuit
 */
internal object StartupWarnings {
    private const val BORDER = "****************************************************"
    private const val PREFIX = "* "

    @ParametersAreNonnullByDefault
    fun discourageCSCoreLib(logger: Logger?) {
        logger?.log(Level.SEVERE, BORDER)
        logger?.log(Level.SEVERE, PREFIX + "It looks like you are still using CS-CoreLib.")
        logger?.log(Level.SEVERE, PREFIX)
        logger?.log(Level.SEVERE, PREFIX + "Slimefun no longer requires CS-CoreLib to be")
        logger?.log(Level.SEVERE, PREFIX + "installed as of January 30th 2021. You need to")
        logger?.log(Level.SEVERE, PREFIX + "remove CS-CoreLib for Slimefun to run.")
        logger?.log(Level.SEVERE, BORDER)
    }

    @ParametersAreNonnullByDefault
    fun invalidMinecraftVersion(logger: Logger?, majorVersion: Int, slimefunVersion: kotlin.String) {
        logger?.log(Level.SEVERE, BORDER)
        logger?.log(Level.SEVERE, PREFIX + "Slimefun was not installed correctly!")
        logger?.log(Level.SEVERE, PREFIX + "You are using the wrong version of Minecraft!")
        logger?.log(Level.SEVERE, PREFIX)
        logger?.log(Level.SEVERE, PREFIX + "You are using Minecraft 1.{0}.x", majorVersion)
        logger?.log(Level.SEVERE, PREFIX + "but Slimefun {0} requires you to be using", slimefunVersion)
        logger?.log(Level.SEVERE, PREFIX + "Minecraft {0}", String.join(" / ", supportedVersions))
        logger?.log(Level.SEVERE, BORDER)
    }

    @ParametersAreNonnullByDefault
    fun invalidServerSoftware(logger: Logger?) {
        logger?.log(Level.SEVERE, BORDER)
        logger?.log(Level.SEVERE, PREFIX + "Slimefun was not installed correctly!")
        logger?.log(Level.SEVERE, PREFIX + "CraftBukkit is no longer supported!")
        logger?.log(Level.SEVERE, PREFIX)
        logger?.log(Level.SEVERE, PREFIX + "Slimefun requires you to use Spigot, Paper or")
        logger?.log(Level.SEVERE, PREFIX + "any supported fork of Spigot or Paper.")
        logger?.log(Level.SEVERE, "$PREFIX(We recommend Paper)")
        logger?.log(Level.SEVERE, BORDER)
    }

    @ParametersAreNonnullByDefault
    fun oldJavaVersion(logger: Logger?, recommendedJavaVersion: Int) {
        val javaVersion = NumberUtils.getJavaVersion()

        logger?.log(Level.WARNING, BORDER)
        logger?.log(Level.WARNING, PREFIX + "Your Java version (Java {0}) is out of date.", javaVersion)
        logger?.log(Level.WARNING, PREFIX)
        logger?.log(Level.WARNING, PREFIX + "We recommend you to update to Java {0}.", recommendedJavaVersion)
        logger?.log(
            Level.WARNING,
            PREFIX + "Java {0} is required for newer versions of Minecraft",
            recommendedJavaVersion
        )
        logger?.log(Level.WARNING, PREFIX + "and we would like to utilise all the new features")
        logger?.log(Level.WARNING, PREFIX + "that come with it as soon as possible.")
        logger?.log(Level.WARNING, PREFIX + "Slimefun will also require Java {0} in", recommendedJavaVersion)
        logger?.log(Level.WARNING, PREFIX + "the foreseeable future, so please update!")
        logger?.log(Level.WARNING, BORDER)
    }
}
