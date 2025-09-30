package io.github.ducklin.implementation

import io.github.bakedlibs.dough.config.Config
import io.github.bakedlibs.dough.protection.ProtectionManager
import io.github.thebusybiscuit.slimefun4.api.MinecraftVersion
import io.github.thebusybiscuit.slimefun4.api.SlimefunAddon
import io.github.thebusybiscuit.slimefun4.api.exceptions.TagMisconfigurationException
import io.github.thebusybiscuit.slimefun4.api.gps.GPSNetwork
import io.github.thebusybiscuit.slimefun4.api.player.PlayerProfile
import io.github.thebusybiscuit.slimefun4.core.SlimefunRegistry
import io.github.thebusybiscuit.slimefun4.core.commands.SlimefunCommand
import io.github.thebusybiscuit.slimefun4.core.networks.NetworkManager
import io.github.thebusybiscuit.slimefun4.core.services.*
import io.github.thebusybiscuit.slimefun4.core.services.github.GitHubService
import io.github.thebusybiscuit.slimefun4.core.services.holograms.HologramsService
import io.github.thebusybiscuit.slimefun4.core.services.profiler.SlimefunProfiler
import io.github.thebusybiscuit.slimefun4.core.services.sounds.SoundService
import io.github.thebusybiscuit.slimefun4.implementation.items.altar.AncientAltar
import io.github.thebusybiscuit.slimefun4.implementation.items.altar.AncientPedestal
import io.github.thebusybiscuit.slimefun4.implementation.items.backpacks.Cooler
import io.github.thebusybiscuit.slimefun4.implementation.items.magical.BeeWings
import io.github.thebusybiscuit.slimefun4.implementation.items.tools.GrapplingHook
import io.github.thebusybiscuit.slimefun4.implementation.items.weapons.SeismicAxe
import io.github.thebusybiscuit.slimefun4.implementation.listeners.*
import io.github.thebusybiscuit.slimefun4.implementation.listeners.crafting.*
import io.github.thebusybiscuit.slimefun4.implementation.listeners.entity.*
import io.github.thebusybiscuit.slimefun4.implementation.resources.GEOResourcesSetup
import io.github.thebusybiscuit.slimefun4.implementation.setup.PostSetup
import io.github.thebusybiscuit.slimefun4.implementation.setup.ResearchSetup
import io.github.thebusybiscuit.slimefun4.implementation.setup.SlimefunItemSetup
import io.github.thebusybiscuit.slimefun4.implementation.tasks.SlimefunStartupTask
import io.github.thebusybiscuit.slimefun4.implementation.tasks.TickerTask
import io.github.thebusybiscuit.slimefun4.implementation.tasks.armor.RadiationTask
import io.github.thebusybiscuit.slimefun4.implementation.tasks.armor.RainbowArmorTask
import io.github.thebusybiscuit.slimefun4.implementation.tasks.armor.SlimefunArmorTask
import io.github.thebusybiscuit.slimefun4.implementation.tasks.armor.SolarHelmetTask
import io.github.thebusybiscuit.slimefun4.integrations.IntegrationsManager
import io.github.thebusybiscuit.slimefun4.storage.Storage
import io.github.ducklin.storage.legacy.LegacyStorage
import io.github.thebusybiscuit.slimefun4.utils.NumberUtils
import io.github.ducklin.api.utils.tags.SlimefunTag
import io.papermc.lib.PaperLib
import me.mrCookieSlime.CSCoreLibPlugin.general.Inventory.MenuListener
import org.apache.commons.lang.Validate
import org.bukkit.Bukkit
import org.bukkit.plugin.Plugin
import org.bukkit.plugin.java.JavaPlugin
import org.bukkit.scheduler.BukkitTask
import java.io.File
import java.util.*
import java.util.function.Consumer
import java.util.logging.Level
import java.util.logging.Logger
import java.util.stream.Collectors
import javax.annotation.Nonnull

/**
 * This is the main class of Slimefun.
 * This is where all the magic starts, take a look around.
 *
 * @author TheBusyBiscuit
 */
@Suppress("DEPRECATION", "NULLABILITY_MISMATCH_BASED_ON_JAVA_ANNOTATIONS")
class Slimefun : SlimefunAddon() {
    /**
     * Keep track of which [io.github.ducklin.api.MinecraftVersion] we are on.
     */
    private var minecraftVersion = MinecraftVersion.UNKNOWN

    /**
     * Keep track of whether this is a fresh install or a regular boot up.
     */
    private var isNewlyInstalled = false

    // Various things we need
    private val registry = SlimefunRegistry()
    private val command = SlimefunCommand(this)
    private val ticker = TickerTask()
    override val javaPlugin: JavaPlugin
        get() = this
    override val bugTrackerURL: String
        get() = "https://github.com/Slimefun/Slimefun4/issues Slimefun/Slimefun4 Slimefun/Slimefun4  "

    // Services - Systems that fulfill certain tasks, treat them as a black box
    private val itemDataService = CustomItemDataService(this, "slimefun_item")
    private val blockDataService = BlockDataService(this, "slimefun_block")
    private val textureService = CustomTextureService(Config(this, "item-models.yml"))
    private val gitHubService = GitHubService("Slimefun/Slimefun4")
    private val updaterService = UpdaterService(this, description.version, file)
    private val metricsService = MetricsService(this)
    private val autoSavingService = AutoSavingService()
    private val backupService = BackupService()
    private val permissionsService = PermissionsService(this)
    private val worldSettingsService = PerWorldSettingsService(this)
    private val recipeService = MinecraftRecipeService(this)
    private val hologramsService = HologramsService(this)
    private val soundService = SoundService(this)
    private val threadService = ThreadService(this)
    private val analyticsService = AnalyticsService(this)

    // Some other things we need
    private val integrations = IntegrationsManager(this)
    private val profiler = SlimefunProfiler()
    private val gpsNetwork = GPSNetwork(this)

    // Even more things we need
    private var networkManager: NetworkManager? = null
    private var local: LocalizationService? = null

    // Important config files for Slimefun
    private val config = Config(this)
    private val items = Config(this, "Items.yml")
    private val researches = Config(this, "Researches.yml")

    // Data storage
    private var playerStorage: Storage? = null

    // Listeners that need to be accessed elsewhere
    private val grapplingHookListener = GrapplingHookListener()
    private val backpackListener = BackpackListener()
    private val bowListener = SlimefunBowListener()

    /**
     * This constructor is invoked by Bukkit and within unit tests.
     * Therefore, we need to figure out if we're within unit tests or not.
     */
    init {
        // Check that we got loaded by MockBukkit rather than Bukkit's loader
        // TODO: This is very much a hack and we can hopefully move to a more native way in the future
        if (classLoader.javaClass.getPackageName().startsWith("be.seeseemelk.mockbukkit")) {
            minecraftVersion = MinecraftVersion.UNIT_TEST
        }
    }

    /**
     * This is called when the [org.bukkit.plugin.Plugin] has been loaded and enabled on a [Server].
     */
    override fun onEnable() {
        setInstance(this)

        if (this.isUnitTest) {
            // We handle Unit Tests separately.
            onUnitTestStart()
        } else if (this.isVersionUnsupported) {
            // We wanna ensure that the Server uses a compatible version of Minecraft.
            server.pluginManager.disablePlugin(this)
        } else {
            // The Environment has been validated.
            onPluginStart()
        }
    }

    /**
     * This is our start method for a Unit Test environment.
     */
    private fun onUnitTestStart() {
        local = LocalizationService(this, "", null)
        networkManager = NetworkManager(200)
        command.register()
        registry.load(this, config)
        loadTags()
        soundService.reload(false)
        // TODO: What do we do if tests want to use another storage backend (e.g. testing new feature on legacy + sql)?
        // Do we have a way to override this?
        playerStorage = LegacyStorage()
    }

    /**
     * This is our start method for a correct Slimefun installation.
     */
    private fun onPluginStart() {
        val timestamp = System.nanoTime()
        val logger = getLogger()
        val plManager = server.pluginManager

        // Check if Paper (<3) is installed
        if (PaperLib.isPaper()) {
            logger.log(Level.INFO, "Paper was detected! Performance optimizations have been applied.")
        } else {
            PaperLib.suggestPaper(this)
        }

        // Check if CS-CoreLib is installed (it is no longer needed)
        if (plManager.getPlugin("CS-CoreLib") != null) {
            StartupWarnings.discourageCSCoreLib(logger)
            plManager.disablePlugin(this)
            return
        }

        // Encourage newer Java version
        if (NumberUtils.getJavaVersion() < RECOMMENDED_JAVA_VERSION) {
            StartupWarnings.oldJavaVersion(logger, RECOMMENDED_JAVA_VERSION)
        }

        // If the server has no "data-storage" folder, it's _probably_ a new install. So mark it for metrics.
        isNewlyInstalled = !File("data-storage/Slimefun").exists()

        // Creating all necessary Folders
        logger.log(Level.INFO, "Creating directories...")
        createDirectories()

        // Load various config settings into our cache
        registry.load(this, config)

        // Set up localization
        logger.log(Level.INFO, "Loading language files...")
        val chatPrefix = config.getString("options.chat-prefix")
        val serverDefaultLanguage = config.getString("options.language")
        local = LocalizationService(this, chatPrefix, serverDefaultLanguage)

        var networkSize = config.getInt("networks.max-size")

        // Make sure that the network size is a valid input
        if (networkSize < 1) {
            logger.log(
                Level.WARNING,
                "Your 'networks.max-size' setting is misconfigured! It must be at least 1, it was set to: {0}",
                networkSize
            )
            networkSize = 1
        }

        networkManager = NetworkManager(
            networkSize,
            config.getBoolean("networks.enable-visualizer"),
            config.getBoolean("networks.delete-excess-items")
        )

        // Data storage
        playerStorage = LegacyStorage()
        logger.log(Level.INFO, "Using legacy storage for player data")

        // Setting up bStats and analytics
        Thread({ metricsService.start() }, "Slimefun Metrics").start()
        analyticsService.start()

        // Starting the Auto-Updater
        if (config.getBoolean("options.auto-update")) {
            logger.log(Level.INFO, "Starting Auto-Updater...")
            updaterService.start()
        } else {
            updaterService.disable()
        }

        // Registering all GEO Resources
        logger.log(Level.INFO, "Loading GEO-Resources...")
        GEOResourcesSetup.setup()

        logger.log(Level.INFO, "Loading Tags...")
        loadTags()

        logger.log(Level.INFO, "Loading items...")
        loadItems()

        logger.log(Level.INFO, "Loading researches...")
        loadResearches()

        registry.isResearchingEnabled = researchCfg.getBoolean("enable-researching")
        PostSetup.setupWiki()

        logger.log(Level.INFO, "Registering listeners...")
        registerListeners()

        // Initiating various Stuff and all items with a slight delay (0ms after the Server finished loading)
        runSync(SlimefunStartupTask(this) {
            textureService.register(registry.allSlimefunItems, true)
            permissionsService.register(registry.allSlimefunItems, true)
            soundService.reload(true)

            // This try/catch should prevent buggy Spigot builds from blocking item loading
            try {
                recipeService.refresh()
            } catch (x: Exception) {
                logger.log(
                    Level.SEVERE,
                    x
                ) { "An Exception occurred while iterating through the Recipe list on Minecraft Version " + minecraftVersion.name + " (Slimefun v" + version + ")" }
            } catch (x: LinkageError) {
                logger.log(
                    Level.SEVERE,
                    x
                ) { "An Exception occurred while iterating through the Recipe list on Minecraft Version " + minecraftVersion.name + " (Slimefun v" + version + ")" }
            }
        }, 0)

        // Setting up our commands
        try {
            command.register()
        } catch (x: Exception) {
            logger.log(Level.SEVERE, "An Exception occurred while registering the /slimefun command", x)
        } catch (x: LinkageError) {
            logger.log(Level.SEVERE, "An Exception occurred while registering the /slimefun command", x)
        }

        // Armor Update Task
        if (config.getBoolean("options.enable-armor-effects")) {
            SlimefunArmorTask().schedule(this, config.getInt("options.armor-update-interval") * 20L)
            if (config.getBoolean("options.enable-radiation")) {
                RadiationTask().schedule(this, config.getInt("options.radiation-update-interval") * 20L)
            }
            RainbowArmorTask().schedule(this, config.getInt("options.rainbow-armor-update-interval") * 20L)
            SolarHelmetTask().schedule(this, config.getInt("options.armor-update-interval").toLong())
        } else if (config.getBoolean("options.enable-radiation")) {
            logger.log(Level.WARNING, "Cannot enable radiation while armor effects are disabled.")
        }

        // Starting our tasks
        autoSavingService.start(this, config.getInt("options.auto-save-delay-in-minutes"))
        hologramsService.start()
        ticker.start(this)

        // Loading integrations
        logger.log(Level.INFO, "Loading Third-Party plugin integrations...")
        integrations.start()
        gitHubService.start(this)

        // Hooray!
        logger.log(Level.INFO, "Slimefun has finished loading in {0}", getStartupTime(timestamp))
    }

    /**
     * This method gets called when the [org.bukkit.plugin.Plugin] gets disabled.
     * Most often it is called when the [Server] is shutting down or reloading.
     */
    override fun onDisable() {
        // Slimefun never loaded successfully, so we don't even bother doing stuff here
        if (instance() == null || minecraftVersion == MinecraftVersion.UNIT_TEST) {
            return
        }

        // Cancel all tasks from this plugin immediately
        Bukkit.getScheduler().cancelTasks(this)

        // Finishes all started movements/removals of block data
        try {
            ticker.halt()
            ticker.run()
        } catch (x: Exception) {
            logger.log(
                Level.SEVERE,
                x
            ) { "Something went wrong while disabling the ticker task for Slimefun v" + description.version }
        }

        // Kill our Profiler Threads
        profiler.kill()

        // Save all Player Profiles that are still in memory
        PlayerProfile.iterator().forEachRemaining(Consumer { profile: PlayerProfile? ->
            if (profile!!.isDirty) {
                profile.save()
            }
        })

        // Save all registered Worlds
        for (entry in getRegistry().worlds.entries) {
            try {
                entry.value.saveAndRemove()
            } catch (x: Exception) {
                logger.log(
                    Level.SEVERE,
                    x
                ) { "An Error occurred while saving Slimefun-Blocks in World '" + entry.key + "' for Slimefun " + version }
            }
        }

        // Save all "universal" inventories (ender chests for example)
        for (menu in registry.universalInventories.values) {
            menu.save()
        }

        // Create a new backup zip
        if (config.getBoolean("options.backup-data")) {
            backupService.run()
        }

        // Close and unload any resources from our Metrics Service
        metricsService.cleanUp()

        // Terminate our Plugin instance
        setInstance(null)

        /**
         * Close all inventories on the server to prevent item dupes
         * (In case some idiot uses /reload)
         */
        for (p in Bukkit.getOnlinePlayers()) {
            p.closeInventory()
        }
    }

    /**
     * This returns the time it took to load Slimefun (given a starting point).
     *
     * @param timestamp
     * The time at which we started to load Slimefun.
     *
     * @return The total time it took to load Slimefun (in ms or s)
     */
    @Nonnull
    private fun getStartupTime(timestamp: Long): String {
        val ms = (System.nanoTime() - timestamp) / 1000000

        return if (ms > 1000) {
            NumberUtils.roundDecimalNumber(ms / 1000.0) + 's'
        } else {
            NumberUtils.roundDecimalNumber(ms.toDouble()) + "ms"
        }
    }

    val isUnitTest: Boolean
        /**
         * This method checks if this is currently running in a unit test
         * environment.
         *
         * @return Whether we are inside a unit test
         */
        get() = minecraftVersion == MinecraftVersion.UNIT_TEST

    private val isVersionUnsupported: Boolean
        /**
         * This method checks for the [io.github.ducklin.api.MinecraftVersion] of the [Server].
         * If the version is unsupported, a warning will be printed to the console.
         *
         * @return Whether the [io.github.ducklin.api.MinecraftVersion] is unsupported
         */
        get() {
            try {
                // First check if they still use the unsupported CraftBukkit software.
                if (!PaperLib.isSpigot() && Bukkit.getName() == "CraftBukkit") {
                    StartupWarnings.invalidServerSoftware(logger)
                    return true
                }

                // Now check the actual Version of Minecraft
                val version = PaperLib.getMinecraftVersion()
                val patchVersion = PaperLib.getMinecraftPatchVersion()

                if (version > 0) {
                    // Check all supported versions of Minecraft
                    for (supportedVersion in MinecraftVersion.entries) {
                        if (supportedVersion.isMinecraftVersion(version, patchVersion)) {
                            minecraftVersion = supportedVersion
                            return false
                        }
                    }

                    // Looks like you are using an unsupported Minecraft Version
                    StartupWarnings.invalidMinecraftVersion(logger, version, description.version)
                    return true
                } else {
                    logger.log(
                        Level.WARNING,
                        "We could not determine the version of Minecraft you were using? ({0})",
                        Bukkit.getVersion()
                    )

                    /*
      * If we are unsure about it, we will assume "supported".
      * They could be using a non-Bukkit based Software which still
      * might support Bukkit-based plugins.
      * Use at your own risk in this case.
      */
                    return false
                }
            } catch (x: Exception) {
                logger.log(
                    Level.SEVERE,
                    x
                ) { "Error: Could not determine Environment or version of Minecraft for Slimefun v" + description.version }

                // We assume "unsupported" if something went wrong.
                return true
            } catch (x: LinkageError) {
                logger.log(
                    Level.SEVERE,
                    x
                ) { "Error: Could not determine Environment or version of Minecraft for Slimefun v" + description.version }

                return true
            }
        }

    /**
     * This method creates all necessary directories (and sub directories) for Slimefun.
     */
    private fun createDirectories() {
        val storageFolders = arrayOf<String?>(
            "Players",
            "blocks",
            "stored-blocks",
            "stored-inventories",
            "stored-chunks",
            "universal-inventories",
            "waypoints",
            "block-backups"
        )
        val pluginFolders = arrayOf<String?>("scripts", "error-reports", "cache/github", "world-settings")

        for (folder in storageFolders) {
            val file = File("data-storage/Slimefun", folder)

            if (!file.exists()) {
                file.mkdirs()
            }
        }

        for (folder in pluginFolders) {
            val file = File("plugins/Slimefun", folder)

            if (!file.exists()) {
                file.mkdirs()
            }
        }
    }

    /**
     * This method registers all of our [Listeners][Listener].
     */
    private fun registerListeners() {
        // Old deprecated CS-CoreLib Listener
        MenuListener(this)

        SlimefunBootsListener(this)
        SlimefunItemInteractListener(this)
        SlimefunItemConsumeListener(this)
        BlockPhysicsListener(this)
        CargoNodeListener(this)
        MultiBlockListener(this)
        GadgetsListener(this)
        DispenserListener(this)
        BlockListener(this)
        EnhancedFurnaceListener(this)
        ItemPickupListener(this)
        ItemDropListener(this)
        DeathpointListener(this)
        ExplosionsListener(this)
        DebugFishListener(this)
        FireworksListener(this)
        WitherListener(this)
        IronGolemListener(this)
        EntityInteractionListener(this)
        MobDropListener(this)
        VillagerTradingListener(this)
        ElytraImpactListener(this)
        CraftingTableListener(this)
        AnvilListener(this)
        BrewingStandListener(this)
        CauldronListener(this)
        GrindstoneListener(this)
        CartographyTableListener(this)
        ButcherAndroidListener(this)
        MiningAndroidListener(this)
        NetworkListener(this, networkManager!!)
        HopperListener(this)
        TalismanListener(this)
        SoulboundListener(this)
        AutoCrafterListener(this)
        SlimefunItemHitListener(this)
        MiddleClickListener(this)
        BeeListener(this)
        BeeWingsListener(this, (SlimefunItems.BEE_WINGS.item as BeeWings?)!!)
        PiglinListener(this)
        SmithingTableListener(this)
        JoinListener(this)

        // Item-specific Listeners
        CoolerListener(this, SlimefunItems.COOLER.item as Cooler?)
        SeismicAxeListener(this, (SlimefunItems.SEISMIC_AXE.item as SeismicAxe?)!!)
        RadioactivityListener(this)
        AncientAltarListener(
            this,
            SlimefunItems.ANCIENT_ALTAR.item as AncientAltar?,
            SlimefunItems.ANCIENT_PEDESTAL.item as AncientPedestal?
        )
        grapplingHookListener.register(this, (SlimefunItems.GRAPPLING_HOOK.item as GrapplingHook?)!!)
        bowListener.register(this)
        backpackListener.register(this)

        // Handle Slimefun Guide being given on Join
        SlimefunGuideListener(this, config.getBoolean("guide.receive-on-first-join"))

        // Clear the Slimefun Guide History upon Player Leaving
        PlayerProfileListener(this)
    }

    /**
     * This (re)loads every [io.github.ducklin.api.utils.tags.SlimefunTag].
     */
    private fun loadTags() {
        for (tag in SlimefunTag.entries) {
            try {
                // Only reload "empty" (or unloaded) Tags
                if (tag.isEmpty) {
                    tag.reload()
                }
            } catch (e: TagMisconfigurationException) {
                logger.log(Level.SEVERE, e) { "Failed to load Tag: " + tag.name }
            }
        }
    }

    /**
     * This loads all of our items.
     */
    private fun loadItems() {
        try {
            SlimefunItemSetup.setup(this)
        } catch (x: Exception) {
            logger.log(
                Level.SEVERE,
                x
            ) { "An Error occurred while initializing SlimefunItems for Slimefun $version" }
        } catch (x: LinkageError) {
            logger.log(
                Level.SEVERE,
                x
            ) { "An Error occurred while initializing SlimefunItems for Slimefun $version" }
        }
    }

    /**
     * This loads our researches.
     */
    private fun loadResearches() {
        try {
            ResearchSetup.setupResearches()
        } catch (x: Exception) {
            logger.log(
                Level.SEVERE,
                x
            ) { "An Error occurred while initializing Slimefun Researches for Slimefun $version" }
        } catch (x: LinkageError) {
            logger.log(
                Level.SEVERE,
                x
            ) { "An Error occurred while initializing Slimefun Researches for Slimefun $version" }
        }
    }

    companion object {
        /**
         * This is the Java version we recommend server owners to use.
         * This does not necessarily mean that it's the minimum version
         * required to run Slimefun.
         */
        private const val RECOMMENDED_JAVA_VERSION = 17

        /**
         * Our static instance of [Slimefun].
         * Make sure to clean this up in [.onDisable]!
         */
        private var instance: Slimefun? = null

        /**
         * This is a private internal method to set the de-facto instance of [Slimefun].
         * Having this as a separate method ensures the separation between static and non-static fields.
         * It also makes sonarcloud happy :)
         * Only ever use it during [.onEnable] or [.onDisable].
         *
         * @param pluginInstance
         * Our instance of [Slimefun] or null
         */
        private fun setInstance(pluginInstance: Slimefun?) {
            instance = pluginInstance
        }

        @JvmStatic
        @get:Nonnull
        val supportedVersions: MutableCollection<String?>
            /**
             * This private method gives us a [Collection] of every [io.github.ducklin.api.MinecraftVersion]
             * that Slimefun is compatible with (as a [String] representation).
             *
             *
             * Example:
             *
             * <pre>
             * { 1.14.x, 1.15.x, 1.16.x }
            </pre> *
             *
             * @return A [Collection] of all compatible minecraft versions as strings
             */
            get() {
                val list: MutableList<String?> = ArrayList<String?>()

                for (version in MinecraftVersion.entries) {
                    if (!version.virtual) {
                        list.add(version.displayName)
                    }
                }

                return list
            }

        /**
         * This returns the global instance of [Slimefun].
         * This may return null if the [org.bukkit.plugin.Plugin] was disabled.
         *
         * @return The [Slimefun] instance
         */
        @JvmStatic
        fun instance(): Slimefun? {
            return instance
        }

        /**
         * This private static method allows us to throw a proper [Exception]
         * whenever someone tries to access a static method while the instance is null.
         * This happens when the method is invoked before [.onEnable] or after [.onDisable].
         *
         *
         * Use it whenever a null check is needed to avoid a non-descriptive [NullPointerException].
         */
        private fun validateInstance() {
            checkNotNull(instance) { "Cannot invoke static method, Slimefun instance is null." }
        }

        /**
         * This returns the [Logger] instance that Slimefun uses.
         *
         *
         * **Any [io.github.ducklin.api.SlimefunAddon] should use their own [Logger] instance!**
         *
         * @return Our [Logger] instance
         */
        @JvmStatic
        @Nonnull
        fun logger(): Logger? {
            validateInstance()
            return instance!!.logger
        }

        @JvmStatic
        @get:Nonnull
        val version: String
            /**
             * This returns the version of Slimefun that is currently installed.
             *
             * @return The currently installed version of Slimefun
             */
            get() {
                validateInstance()
                return instance!!.description.version
            }

        @JvmStatic
        @get:Nonnull
        val cfg: Config
            get() {
                validateInstance()
                return instance!!.config
            }

        @JvmStatic
        @get:Nonnull
        val researchCfg: Config
            get() {
                validateInstance()
                return instance!!.researches
            }

        @JvmStatic
        @get:Nonnull
        val itemCfg: Config
            get() {
                validateInstance()
                return instance!!.items
            }

        @JvmStatic
        @get:Nonnull
        val gPSNetwork: GPSNetwork
            /**
             * This returns our [io.github.ducklin.core.api.gps.GPSNetwork] instance.
             * The [io.github.ducklin.core.api.gps.GPSNetwork] is responsible for handling any GPS-related
             * operations and for managing any [io.github.ducklin.core.api.geo.GEOResource].
             *
             * @return Our [io.github.ducklin.core.api.gps.GPSNetwork] instance
             */
            get() {
                validateInstance()
                return instance!!.gpsNetwork
            }

        @JvmStatic
        @get:Nonnull
        val tickerTask: TickerTask
            get() {
                validateInstance()
                return instance!!.ticker
            }

        @JvmStatic
        @get:Nonnull
        val localization: LocalizationService
            /**
             * This returns the [io.github.ducklin.core.services.LocalizationService] of Slimefun.
             *
             * @return The [io.github.ducklin.core.services.LocalizationService] of Slimefun
             */
            get() {
                validateInstance()
                return instance!!.local!!
            }

        @JvmStatic
        @get:Nonnull
        val minecraftRecipeService: MinecraftRecipeService
            /**
             * This method returns out [io.github.ducklin.core.services.MinecraftRecipeService] for Slimefun.
             * This service is responsible for finding/identifying [Recipes][org.bukkit.inventory.Recipe]
             * from vanilla Minecraft.
             *
             * @return Slimefun's [io.github.ducklin.core.services.MinecraftRecipeService] instance
             */
            get() {
                validateInstance()
                return instance!!.recipeService
            }

        @JvmStatic
        @Nonnull
        fun getItemDataService(): CustomItemDataService {
            validateInstance()
            return instance!!.itemDataService
        }

        @JvmStatic
        @get:Nonnull
        val itemTextureService: CustomTextureService
            get() {
                validateInstance()
                return instance!!.textureService
            }

        @JvmStatic
        @Nonnull
        fun getPermissionsService(): PermissionsService {
            validateInstance()
            return instance!!.permissionsService
        }

        @JvmStatic
        @Nonnull
        fun getBlockDataService(): BlockDataService {
            validateInstance()
            return instance!!.blockDataService
        }

        /**
         * This method returns out world settings service.
         * That service is responsible for managing item settings per
         * [org.bukkit.World], such as disabling a [io.github.ducklin.api.items.SlimefunItem] in a
         * specific [org.bukkit.World].
         *
         * @return Our instance of [io.github.ducklin.core.services.PerWorldSettingsService]
         */
        @JvmStatic
        @Nonnull
        fun getWorldSettingsService(): PerWorldSettingsService {
            validateInstance()
            return instance!!.worldSettingsService
        }

        /**
         * This returns our [io.github.ducklin.core.services.holograms.HologramsService] which handles the creation and
         * cleanup of any holograms.
         *
         * @return Our instance of [io.github.ducklin.core.services.holograms.HologramsService]
         */
        @JvmStatic
        @Nonnull
        fun getHologramsService(): HologramsService {
            validateInstance()
            return instance!!.hologramsService
        }

        /**
         * This returns our [io.github.ducklin.core.services.sounds.SoundService] which handles the configuration of all sounds used in Slimefun
         *
         * @return Our instance of [io.github.ducklin.core.services.sounds.SoundService]
         */
        @JvmStatic
        @Nonnull
        fun getSoundService(): SoundService {
            validateInstance()
            return instance!!.soundService
        }

        /**
         * This returns our instance of [io.github.ducklin.integrations.IntegrationsManager].
         * This is responsible for managing any integrations with third party [plugins][org.bukkit.plugin.Plugin].
         *
         * @return Our instance of [io.github.ducklin.integrations.IntegrationsManager]
         */
        @JvmStatic
        @Nonnull
        fun getIntegrations(): IntegrationsManager {
            validateInstance()
            return instance!!.integrations
        }

        @JvmStatic
        @get:Nonnull
        val protectionManager: ProtectionManager
            /**
             * This returns out instance of the [ProtectionManager].
             * This bridge is used to hook into any third-party protection [org.bukkit.plugin.Plugin].
             *
             * @return Our instanceof of the [ProtectionManager]
             */
            get() = getIntegrations()
                .protectionManager

        @JvmStatic
        @get:Nonnull
        val updater: UpdaterService
            /**
             * This method returns the [io.github.ducklin.core.services.UpdaterService] of Slimefun.
             * It is used to handle automatic updates.
             *
             * @return The [io.github.ducklin.core.services.UpdaterService] for Slimefun
             */
            get() {
                validateInstance()
                return instance!!.updaterService
            }

        /**
         * This method returns the [io.github.ducklin.core.services.MetricsService] of Slimefun.
         * It is used to handle sending metric information to bStats.
         *
         * @return The [io.github.ducklin.core.services.MetricsService] for Slimefun
         */
        @JvmStatic
        @Nonnull
        fun getMetricsService(): MetricsService {
            validateInstance()
            return instance!!.metricsService
        }

        /**
         * This method returns the [io.github.ducklin.core.services.AnalyticsService] of Slimefun.
         * It is used to handle sending analytic information.
         *
         * @return The [io.github.ducklin.core.services.AnalyticsService] for Slimefun
         */
        @JvmStatic
        @Nonnull
        fun getAnalyticsService(): AnalyticsService {
            validateInstance()
            return instance!!.analyticsService
        }

        /**
         * This method returns the [io.github.ducklin.core.services.github.GitHubService] of Slimefun.
         * It is used to retrieve data from GitHub repositories.
         *
         * @return The [io.github.ducklin.core.services.github.GitHubService] for Slimefun
         */
        @JvmStatic
        @Nonnull
        fun getGitHubService(): GitHubService {
            validateInstance()
            return instance!!.gitHubService
        }

        /**
         * This returns our [io.github.ducklin.core.networks.NetworkManager] which is responsible
         * for handling the Cargo and Energy networks.
         *
         * @return Our [io.github.ducklin.core.networks.NetworkManager] instance
         */
        @JvmStatic
        @Nonnull
        fun getNetworkManager(): NetworkManager {
            validateInstance()
            return instance!!.networkManager!!
        }

        @JvmStatic
        @Nonnull
        fun getRegistry(): SlimefunRegistry {
            validateInstance()
            return instance!!.registry
        }

        @JvmStatic
        @Nonnull
        fun getGrapplingHookListener(): GrapplingHookListener {
            validateInstance()
            return instance!!.grapplingHookListener
        }

        @JvmStatic
        @Nonnull
        fun getBackpackListener(): BackpackListener {
            validateInstance()
            return instance!!.backpackListener
        }

        @JvmStatic
        @Nonnull
        fun getBowListener(): SlimefunBowListener {
            validateInstance()
            return instance!!.bowListener
        }

        /**
         * The [org.bukkit.command.Command] that was added by Slimefun.
         *
         * @return Slimefun's command
         */
        @JvmStatic
        @Nonnull
        fun getCommand(): SlimefunCommand {
            validateInstance()
            return instance!!.command
        }

        /**
         * This returns our instance of the [io.github.ducklin.core.services.profiler.SlimefunProfiler], a tool that is used
         * to analyse performance and lag.
         *
         * @return The [io.github.ducklin.core.services.profiler.SlimefunProfiler]
         */
        @JvmStatic
        @Nonnull
        fun getProfiler(): SlimefunProfiler {
            validateInstance()
            return instance!!.profiler
        }

        /**
         * This returns the currently installed version of Minecraft.
         *
         * @return The current version of Minecraft
         */
        @JvmStatic
        @Nonnull
        fun getMinecraftVersion(): MinecraftVersion {
            validateInstance()
            return instance!!.minecraftVersion
        }

        /**
         * This method returns whether this version of Slimefun was newly installed.
         * It will return true if this [Server] uses Slimefun for the very first time.
         *
         * @return Whether this is a new installation of Slimefun
         */
        fun isNewlyInstalled(): Boolean {
            validateInstance()
            return instance!!.isNewlyInstalled
        }

        @JvmStatic
        @get:Nonnull
        val installedAddons: MutableSet<Plugin?>
            /**
             * This method returns a [Set] of every [Plugin] that lists Slimefun
             * as a required or optional dependency.
             *
             *
             * We will just assume this to be a list of our addons.
             *
             * @return A [Set] of every [Plugin] that is dependent on Slimefun
             */
            get() {
                validateInstance()
                val pluginName: String? =
                    instance!!.name

                // @formatter:off - Collect any Plugin that (soft)-depends on Slimefun
                return Arrays.stream<Plugin>(instance!!.server.pluginManager.plugins).filter {
                        plugin: Plugin? -> val description = plugin!!.description
                    pluginName in description.depend || pluginName in description.softDepend
                } .collect(Collectors.toSet())
                // @formatter:on
            }

        /**
         * This method schedules a delayed synchronous task for Slimefun.
         * **For Slimefun only, not for addons.**
         *
         * This method should only be invoked by Slimefun itself.
         * Addons must schedule their own tasks using their own [Plugin] instance.
         *
         * @param runnable
         * The [Runnable] to run
         * @param delay
         * The delay for this task
         *
         * @return The resulting [org.bukkit.scheduler.BukkitTask] or null if Slimefun was disabled
         */
        @JvmStatic
        fun runSync(@Nonnull runnable: Runnable, delay: Long): BukkitTask? {
            Validate.notNull(runnable, "Cannot run null")
            Validate.isTrue(delay >= 0, "The delay cannot be negative")

            // Run the task instantly within a Unit Test
            if (getMinecraftVersion() == MinecraftVersion.UNIT_TEST) {
                runnable.run()
                return null
            }

            if (instance == null || !instance!!.isEnabled) {
                return null
            }

            return instance!!.server.scheduler.runTaskLater(instance!!, runnable, delay)
        }

        /**
         * This method schedules a synchronous task for Slimefun.
         * **For Slimefun only, not for addons.**
         *
         * This method should only be invoked by Slimefun itself.
         * Addons must schedule their own tasks using their own [Plugin] instance.
         *
         * @param runnable
         * The [Runnable] to run
         *
         * @return The resulting [BukkitTask] or null if Slimefun was disabled
         */
        @JvmStatic
        fun runSync(@Nonnull runnable: Runnable): BukkitTask? {
            Validate.notNull(runnable, "Cannot run null")

            // Run the task instantly within a Unit Test
            if (getMinecraftVersion() == MinecraftVersion.UNIT_TEST) {
                runnable.run()
                return null
            }

            if (instance == null || !instance!!.isEnabled) {
                return null
            }

            return instance!!.server.scheduler.runTask(instance!!, runnable)
        }

        @JvmStatic
        @Nonnull
        fun getPlayerStorage(): Storage {
            return instance()!!.playerStorage!!
        }

        /**
         * This method returns the [io.github.ducklin.core.services.ThreadService] of Slimefun.
         * **Do not use this if you're an addon. Please make your own [io.github.ducklin.core.services.ThreadService].**
         *
         * @return The [io.github.ducklin.core.services.ThreadService] for Slimefun
         */
        @JvmStatic
        @Nonnull
        fun getThreadService(): ThreadService {
            return instance()!!.threadService
        }
    }
}