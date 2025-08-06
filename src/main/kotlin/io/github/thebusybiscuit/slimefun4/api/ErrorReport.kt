package io.github.thebusybiscuit.slimefun4.api

import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItem
import io.github.thebusybiscuit.slimefun4.core.attributes.EnergyNetProvider
import io.github.thebusybiscuit.slimefun4.implementation.Slimefun.Companion.runSync
import io.github.thebusybiscuit.slimefun4.implementation.Slimefun.Companion.version
import io.papermc.lib.PaperLib
import me.mrCookieSlime.Slimefun.api.BlockStorage
import org.bukkit.Bukkit
import org.bukkit.Location
import java.io.File
import java.io.PrintStream
import java.nio.charset.StandardCharsets
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.*
import java.util.concurrent.atomic.AtomicInteger
import java.util.function.*
import java.util.function.Function
import java.util.logging.Level
import java.util.stream.IntStream
import javax.annotation.Nonnull
import javax.annotation.ParametersAreNonnullByDefault

/**
 * This class represents an [ErrorReport].
 * Error reports are thrown when a [BlockTicker] is causing problems.
 * To ensure that the console doesn't get too spammy, we destroy the block and generate
 * an [ErrorReport] instead.
 * Error reports get saved in the plugin folder.
 *
 * @param <T>
 * The type of [Throwable] which has spawned this [ErrorReport]
 *
 * @author TheBusyBiscuit
</T> */
@Suppress("DEPRECATION")
class ErrorReport<T : Throwable?> @JvmOverloads constructor(
    private val thrown: T,
    private val addon: SlimefunAddon,
    printer: Consumer<PrintStream> = Consumer { }
) {

    private var file: File? = null

    /**
     * This is the base constructor for an [ErrorReport]. It will only
     * print the necessary info and provides a [Consumer] for any more detailed
     * needs.
     *
     * @param thrown
     * The [Throwable] which caused this [ErrorReport].
     * @param addon
     * The [SlimefunAddon] responsible.
     * @param printer
     * A custom [Consumer] to add more details.
     */
    init {
        runSync { print(printer) }
    }

    /**
     * This constructs a new [ErrorReport] for the given [Location] and
     * [SlimefunItem].
     *
     * @param throwable
     * The [Throwable] which caused this [ErrorReport].
     * @param l
     * The [Location] at which the error was thrown.
     * @param item
     * The [SlimefunItem] responsible.
     */
    @ParametersAreNonnullByDefault
    constructor(throwable: T, l: Location, item: SlimefunItem) : this(
        throwable,
        item.addon!!,
        Consumer { stream: PrintStream? ->
            stream!!.println("Block Info:")
            stream.println("  World: " + l.world.name)
            stream.println("  X: " + l.blockX)
            stream.println("  Y: " + l.blockY)
            stream.println("  Z: " + l.blockZ)
            stream.println("  Material: " + l.block.type)
            stream.println("  Block Data: " + l.block.blockData.javaClass.name)
            stream.println("  State: " + l.block.state.javaClass.name)
            stream.println()

            if (item.blockTicker != null) {
                stream.println("Ticker-Info:")
                stream.println(
                    "  Type: " + (if (item.blockTicker!!.isSynchronized()) "Synchronized" else "Asynchronous")
                )
                stream.println()
            }

            if (item is EnergyNetProvider) {
                stream.println("Ticker-Info:")
                stream.println("  Type: Indirect (Energy Network)")
                stream.println()
            }

            stream.println("Slimefun Data:")
            stream.println("  ID: " + item.id)
            stream.println("  Inventory: " + BlockStorage.getStorage(l.world)!!.hasInventory(l))
            stream.println("  Data: " + BlockStorage.getBlockInfoAsJson(l))
            stream.println()
        })

    /**
     * This constructs a new [ErrorReport] for the given [SlimefunItem].
     *
     * @param throwable
     * The [Throwable] which caused this [ErrorReport].
     * @param item
     * The [SlimefunItem] responsible.
     */
    @ParametersAreNonnullByDefault
    constructor(
        throwable: T,
        item: SlimefunItem
    ) : this(throwable, item.addon!!, Consumer { stream: PrintStream? ->
        stream!!.println("SlimefunItem:")
        stream.println("  ID: " + item.id)
        stream.println("  Plugin: " + (item.addon?.javaPlugin!!.name))
        stream.println()
    })

    /**
     * This method returns the [File] this [ErrorReport] has been written to.
     *
     * @return The [File] for this [ErrorReport]
     */
    @Nonnull
    fun getFile(): File {
        return file!!
    }

    private fun print(@Nonnull printer: Consumer<PrintStream?>) {
        this.file = newFile
        count.incrementAndGet()

        try {
            PrintStream(file, StandardCharsets.UTF_8.name()).use { stream ->
                stream.println()
                stream.println("Error Generated: " + dateFormat.format(LocalDateTime.now()))
                stream.println()

                stream.println("Java Environment:")
                stream.println("  Operating System: " + System.getProperty("os.name"))
                stream.println("  Java Version: " + System.getProperty("java.version"))
                stream.println()

                val serverSoftware = if (PaperLib.isSpigot() && !PaperLib.isPaper()) "Spigot" else Bukkit.getName()
                stream.println("Server Software: $serverSoftware")
                stream.println("  Build: " + Bukkit.getVersion())
                stream.println("  Minecraft v" + Bukkit.getBukkitVersion())
                stream.println()

                stream.println("Slimefun Environment:")
                stream.println("  Slimefun v$version")
                stream.println("  Caused by: " + addon.javaPlugin.name + " v" + addon.pluginVersion)
                stream.println()

                val plugins: MutableList<String?> = ArrayList<String?>()
                val addons: MutableList<String?> = ArrayList<String?>()

                scanPlugins(plugins, addons)

                stream.println("Installed Addons (" + addons.size + ")")
                addons.forEach(Consumer { x: String? -> stream.println(x) })

                stream.println()

                stream.println("Installed Plugins (" + plugins.size + "):")
                plugins.forEach(Consumer { x: String? -> stream.println(x) })

                stream.println()

                printer.accept(stream)

                stream.println("Stacktrace:")
                stream.println()
                thrown!!.printStackTrace(stream)

                addon.javaPlugin.logger.log(Level.WARNING, "")
                addon.javaPlugin.logger.log(Level.WARNING, "An Error occurred! It has been saved as: ")
                addon.javaPlugin.logger.log(Level.WARNING, "/plugins/Slimefun/error-reports/{0}", file!!.getName())
                addon.javaPlugin.logger.log(
                    Level.WARNING,
                    "Please put this file on https://pastebin.com/ and report this to the developer(s)."
                )

                if (addon.bugTrackerURL != null) {
                    addon.javaPlugin.logger.log(Level.WARNING, "Bug Tracker: {0}", addon.bugTrackerURL)
                }
                addon.javaPlugin.logger.log(Level.WARNING, "")
            }
        } catch (x: Exception) {
            addon.javaPlugin.logger.log(
                Level.SEVERE,
                x
            ) { "An Error occurred while saving an Error-Report for Slimefun $version" }
        }
    }

    companion object {
        private val dateFormat: DateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd-HH-mm", Locale.ROOT)
        private val count = AtomicInteger(0)

        /**
         * This method returns the amount of [ErrorReports][ErrorReport] created in this session.
         *
         * @return The amount of [ErrorReports][ErrorReport] created.
         */
        fun count(): Int {
            return count.get()
        }

        private fun scanPlugins(@Nonnull plugins: MutableList<String?>, @Nonnull addons: MutableList<String?>) {
            val dependency = "Slimefun"

            for (plugin in Bukkit.getPluginManager().plugins) {
                if (Bukkit.getPluginManager().isPluginEnabled(plugin)) {
                    plugins.add("  + " + plugin.name + ' ' + plugin.description.version)

                    if (dependency in plugin.description.depend || dependency in plugin.description.softDepend) {
                        addons.add("  + " + plugin.name + ' ' + plugin.description.version)
                    }
                } else {
                    plugins.add("  - " + plugin.name + ' ' + plugin.description.version)

                    if (dependency in plugin.description.depend || dependency in plugin.description.softDepend) {
                        addons.add("  - " + plugin.name + ' ' + plugin.description.version)
                    }
                }
            }
        }

        private val newFile: File
            get() {
                val path =
                    "plugins/Slimefun/error-reports/" + dateFormat.format(
                        LocalDateTime.now()
                    )
                var newFile = File("$path.err")

                if (newFile.exists()) {
                    val stream = IntStream.iterate(
                        1
                    ) { i: Int -> i + 1 }
                        .filter { i: Int ->
                            !File("$path ($i).err").exists()
                        }
                    val id = stream.findFirst().getAsInt()

                    newFile = File("$path ($id).err")
                }

                return newFile
            }

        /**
         * This helper method wraps the given [Runnable] into a try-catch block.
         * When an [Exception] occurs, a new [ErrorReport] will be generated using
         * the provided [Function].
         *
         * @param function
         * The [Function] to generate a new [ErrorReport]
         * @param runnable
         * The code to execute
         */
        fun tryCatch(
            function: Function<Exception?, ErrorReport<Exception?>?>,
            runnable: Runnable
        ) {
            try {
                runnable.run()
            } catch (x: Exception) {
                function.apply(x)
            }
        }
    }
}
