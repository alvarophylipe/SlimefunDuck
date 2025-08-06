package io.github.thebusybiscuit.slimefun4.api.items

import io.github.bakedlibs.dough.collections.OptionalMap
import io.github.bakedlibs.dough.items.ItemUtils
import io.github.thebusybiscuit.slimefun4.api.MinecraftVersion
import io.github.thebusybiscuit.slimefun4.api.SlimefunAddon
import io.github.thebusybiscuit.slimefun4.api.SlimefunBranch
import io.github.thebusybiscuit.slimefun4.api.exceptions.IdConflictException
import io.github.thebusybiscuit.slimefun4.api.exceptions.MissingDependencyException
import io.github.thebusybiscuit.slimefun4.api.exceptions.UnregisteredItemException
import io.github.thebusybiscuit.slimefun4.api.player.PlayerProfile
import io.github.thebusybiscuit.slimefun4.api.recipes.RecipeType
import io.github.thebusybiscuit.slimefun4.api.researches.Research
import io.github.thebusybiscuit.slimefun4.core.attributes.NotConfigurable
import io.github.thebusybiscuit.slimefun4.core.attributes.Placeable
import io.github.thebusybiscuit.slimefun4.core.attributes.Radioactive
import io.github.thebusybiscuit.slimefun4.core.handlers.GlobalItemHandler
import io.github.thebusybiscuit.slimefun4.implementation.Slimefun.Companion.cfg
import io.github.thebusybiscuit.slimefun4.implementation.Slimefun.Companion.getItemDataService
import io.github.thebusybiscuit.slimefun4.implementation.Slimefun.Companion.getMinecraftVersion
import io.github.thebusybiscuit.slimefun4.implementation.Slimefun.Companion.getPermissionsService
import io.github.thebusybiscuit.slimefun4.implementation.Slimefun.Companion.getRegistry
import io.github.thebusybiscuit.slimefun4.implementation.Slimefun.Companion.getWorldSettingsService
import io.github.thebusybiscuit.slimefun4.implementation.Slimefun.Companion.itemCfg
import io.github.thebusybiscuit.slimefun4.implementation.Slimefun.Companion.localization
import io.github.thebusybiscuit.slimefun4.implementation.Slimefun.Companion.updater
import io.github.thebusybiscuit.slimefun4.implementation.items.VanillaItem
import me.mrCookieSlime.Slimefun.Objects.handlers.BlockTicker
import org.apache.commons.lang.Validate
import org.bukkit.Material
import org.bukkit.World
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import java.lang.Deprecated
import java.util.*
import java.util.function.Consumer
import java.util.function.Function
import java.util.function.Supplier
import java.util.logging.Level
import javax.annotation.Nonnull
import javax.annotation.ParametersAreNonnullByDefault
import kotlin.Any
import kotlin.Array
import kotlin.Boolean
import kotlin.Exception
import kotlin.Int
import kotlin.RuntimeException
import kotlin.String
import kotlin.Throwable
import kotlin.UnsupportedOperationException
import kotlin.arrayOf
import kotlin.arrayOfNulls
import kotlin.require

@Suppress("NULLABILITY_MISMATCH_BASED_ON_JAVA_ANNOTATIONS")
open class SlimefunItem @JvmOverloads constructor(
    var itemGroup: ItemGroup,
    val itemStackTemplate: ItemStack,
    val id: String,
    var recipeType: RecipeType,
    var recipe: Array<ItemStack?> = arrayOfNulls(9),
    var recipeOutput: ItemStack? = null
) : Placeable {

    @JvmField
    var addon: SlimefunAddon? = null

    var state: ItemState = ItemState.UNREGISTERED
        internal set

    var research: Research? = null
        set(value) {
            field?.affectedItems?.remove(this)
            value?.affectedItems?.add(this)
            field = value
        }

    var isEnchantable: Boolean = true
        protected set

    var isDisenchantable: Boolean = true
        protected set

    @JvmField
    var isUseableInWorkbench: Boolean = false

    fun setUsableInWorkbench(value: Boolean) {
        this.isUseableInWorkbench = value
    }

    var wikiURL: Optional<String> = Optional.empty()
        private set

    private val itemHandlers = OptionalMap<Class<out ItemHandler?>?,
            ItemHandler>(Supplier { HashMap() })

    val itemSettings: MutableSet<ItemSetting<*>> = HashSet()

    var isTicking: Boolean = false
        private set

    var blockTicker: BlockTicker? = null
        private set

    val item: ItemStack
        get() = itemStackTemplate.clone()

    val itemName: String
        get() = ItemUtils.getItemName(itemStackTemplate)

    val handlers: MutableCollection<ItemHandler?>
        get() = itemHandlers.values()

    val isDisabled: Boolean
        get() {
            if (state == ItemState.UNREGISTERED) {
                error(
                    "isDisabled() cannot be called before registering the item",
                    UnregisteredItemException(this)
                )
            }
            return state != ItemState.ENABLED
        }

    constructor(
        itemGroup: ItemGroup,
        item: SlimefunItemStack,
        recipeType: RecipeType,
        recipe: Array<ItemStack?> = arrayOfNulls(9)
    ) : this(
        itemGroup,
        item,
        item.itemId,
        recipeType,
        recipe,
        null
    )

    constructor(
        itemGroup: ItemGroup,
        item: SlimefunItemStack,
        recipeType: RecipeType,
        recipe: Array<ItemStack?> = arrayOfNulls(9),
        recipeOutput: ItemStack? = null
    ) : this(
        itemGroup,
        item,
        item.itemId,
        recipeType,
        recipe,
        recipeOutput
    )

    constructor(
        itemGroup: ItemGroup,
        item: SlimefunItemStack
    ) : this(
        itemGroup,
        item,
        RecipeType.NULL,
        emptyArray()
    )

    fun hasResearch(): Boolean = research != null

    private var hidden: Boolean = false

    fun isHidden(): Boolean = hidden

    fun setHidden(hidden: Boolean) {
        if (this.hidden == hidden) return
        this.hidden = hidden
        if (state == ItemState.ENABLED) {
            if (hidden) itemGroup.remove(this) else itemGroup.add(this)
        }
    }

    fun isDisabledIn(world: World): Boolean {
        if (state == ItemState.UNREGISTERED) {
            error("isDisabled(World) cannot be called before registering the item", UnregisteredItemException(this))
            return false
        }

        return isDisabled || !getWorldSettingsService().isEnabled(world, this)
    }

    /*
    @Suppress("UNCHECKED_CAST")
    fun <T> getItemSetting(key: String, c: Class<T>)
    : ItemSetting<T>? = itemSettings.firstOrNull { it.key == key && it.isType(c) } as? ItemSetting<T>
    */

    open fun register(@Nonnull addon: SlimefunAddon) {
        Validate.notNull(addon, "A SlimefunAddon cannot be null!")
        Validate.notNull(addon.javaPlugin, "SlimefunAddon#getJavaPlugin() is not allowed to return null!")

        this.addon = addon

        try {
            checkDependencies(addon)
            checkForConflicts()
            preRegister()

            if (recipe.size < 9) {
                recipe = recipe.copyOf(9)
            }

            getRegistry().allSlimefunItems.add(this)
            getRegistry().slimefunItemIds[id] = this

            // Items that are "not-configurable" cannot be configured.
            if (this !is NotConfigurable) {
                itemCfg.setDefaultValue("$id.enabled", true)
                itemCfg.setDefaultValue("$id.can-be-used-in-workbenches", this.isUseableInWorkbench)
                itemCfg.setDefaultValue("$id.hide-in-guide", hidden)
                itemCfg.setDefaultValue("$id.allow-enchanting", this.isEnchantable)
                itemCfg.setDefaultValue("$id.allow-disenchanting", this.isDisenchantable)

                // Load all item settings
                itemSettings.forEach { it.reload() }
            }

            if (this.isTicking && !cfg.getBoolean("URID.enable-tickers")) {
                state = ItemState.DISABLED
                return
            }

            state = when {
                this is NotConfigurable -> ItemState.ENABLED
                itemCfg.getBoolean("$id.enabled") -> {
                    isUseableInWorkbench = itemCfg.getBoolean("$id.can-be-used-in-workbenches")
                    hidden = itemCfg.getBoolean("$id.hide-in-guide")
                    isEnchantable = itemCfg.getBoolean("$id.allow-enchanting")
                    isDisenchantable = itemCfg.getBoolean("$id.allow-disenchanting")
                    ItemState.ENABLED
                }
                this is VanillaItem -> ItemState.VANILLA_FALLBACK
                else -> ItemState.DISABLED
            }

            // Now we can be certain this item should be enabled
            if (state == ItemState.ENABLED) {
                onEnable()
            } else {
                itemHandlers.values().forEach {
                    if (it is BlockTicker) {
                        getRegistry().tickerBlocks.remove(id)
                    }
                }
                this.itemHandlers.clear()
            }

            postRegister()

            // handle runtime-registrations / auto-loading
            if (getRegistry().isAutoLoadingEnabled && state == ItemState.ENABLED) {
                info("Item was registered during runtime.")
                load()
            }
        } catch (x: Exception) {
            error("Registering " + toString() + " has failed!", x)
        }
    }

    private fun onEnable() {
        // Register the ItemGroup too if it hasn't been registered yet
        if (!itemGroup.isRegistered) {
            itemGroup.register(addon!!)
        }

        // Send out deprecation warnings for any classes or interfaces
        checkForDeprecations(javaClass)

        // Check for an illegal stack size
        if (itemStackTemplate.amount != 1) {
            // @formatter:off
            warn(("This item has an illegal stack size: " + itemStackTemplate.amount
            + ". An Item size of 1 is recommended. Please inform the author(s) of " + addon!!.javaPlugin.name
            + " to fix this. Crafting Results with amounts of higher should be handled"
            + " via the recipeOutput parameter!"))
                        // @formatter:on
        }

        // Add it to the list of enabled items
        getRegistry().enabledSlimefunItems.add(this)

        // Load our Item Handlers
        loadItemHandlers()

        // Properly mark this Item as radioactive
        if (this is Radioactive) {
            getRegistry().radioactiveItems.add(this)
        }
    }

    private fun loadItemHandlers() {

        itemHandlers.values().forEach { handler ->
            val exception = handler.validate(this)
            if (exception.isPresent) {
                throw exception.get()
            } else {
                checkForDeprecations(handler.javaClass)
            }

            if (handler is GlobalItemHandler) {
                getRegistry().getGlobalItemHandlers(handler.identifier).add(handler)
            }
        }
    }

    private fun checkDependencies(@Nonnull addon: SlimefunAddon) {
        if (!addon.hasDependency("Slimefun")) {
            throw MissingDependencyException(addon, "Slimefun")
        }
    }

    private fun checkForConflicts() {
        val conflictingItem: SlimefunItem? = getById(id)

        if (conflictingItem != null) {
            throw IdConflictException(this, conflictingItem)
        }
    }

    private fun checkForDeprecations(c: Class<*>?) {
        if (updater.branch == SlimefunBranch.DEVELOPMENT) return

        if (c == null) return


        if (c.isAnnotationPresent(Deprecated::class.java)) {
            warn("The inherited Class \"" + c.getName() + "\" has been deprecated. Check the documentation for more details!")
        }

        c.interfaces.forEach {
            if (it.isAnnotationPresent(Deprecated::class.java)) {
                warn("The implemented Interface \"${it.name}\" has been deprecated. Check the documentation for more details!")
            }
        }

        checkForDeprecations(c.getSuperclass())
    }

    fun setUseableInWorkbench(useable: Boolean): SlimefunItem {
        this.isUseableInWorkbench = useable

        return this
    }

    fun isItem(item: ItemStack?): Boolean {
        if (item == null) {
            return false
        }

        if (item.hasItemMeta()) {
            val itemId = getItemDataService().getItemData(item)

            if (itemId.isPresent) {
                return id == itemId.get()
            }
        }

        return false
    }

    open fun load() {
        if (!hidden) itemGroup.add(this)
        recipeType.register(recipe, recipeOutput?.clone() ?: itemStackTemplate.clone())
    }

    fun addItemHandler(vararg handlers: ItemHandler?) {
        Validate.notEmpty(handlers, "You cannot add zero handlers...")
        Validate.noNullElements(handlers, "You cannot add any 'null' ItemHandler!")

        // Make sure they are added before the item was registered.
        if (state != ItemState.UNREGISTERED) {
            throw UnsupportedOperationException("You cannot add an ItemHandler after the SlimefunItem was registered.")
        }

        for (handler in handlers) {
            itemHandlers.put(handler!!.getIdentifier(), handler)

            // Tickers are a special case (at the moment at least)
            if (handler is BlockTicker) {
                this.isTicking = true
                getRegistry().tickerBlocks.add(id)
                blockTicker = handler
            }
        }
    }

    fun addItemSetting(vararg settings: ItemSetting<*>?) {
        Validate.notEmpty(settings, "You cannot add zero settings...")
        Validate.noNullElements(settings, "You cannot add any 'null' ItemSettings!")

        if (state != ItemState.UNREGISTERED) {
            throw UnsupportedOperationException("You cannot add an ItemSetting after the SlimefunItem was registered.")
        }

        if (this is NotConfigurable) {
            throw UnsupportedOperationException("This Item has been marked as NotConfigurable and cannot accept Item Settings!")
        }

        for (setting in settings) {
            if (setting != null) {
                // Prevent two Item Settings with the same key
                for (existingSetting in itemSettings) {
                    require(existingSetting.key != setting.key) { "This Item has already an ItemSetting with this key: " + setting.key }
                }

                itemSettings.add(setting)
            }
        }
    }

    open fun preRegister() {
        // Override this method to execute code before the Item has been registered
        // Useful for calls to addItemHandler(...)
    }

    /**
     * This method is called after [.register].
     * Override this method to add any additional setup that needs to happen after
     * the original registration of this [SlimefunItem].
     */
    open fun postRegister() {
        // Override this method to execute code after the Item has been registered
        // Useful for calls to Slimefun.getItemValue(...)
    }

    /**
     * This method will assign the given wiki page to this Item.
     * Note that you only need to provide the page name itself,
     * the URL to our wiki is prepended automatically.
     *
     * @param page
     * The associated wiki page
     */
    fun addOfficialWikipage(@Nonnull page: String) {
        Validate.notNull(page, "Wiki page cannot be null.")
        this.wikiURL = Optional.of<String?>("https://github.com/Slimefun/Slimefun4/wiki/$page")
    }

    /**
     * This method calls every [ItemHandler] of the given [Class]
     * and performs the action as specified via the [Consumer].
     *
     * @param c
     * The [Class] of the [ItemHandler] to call.
     * @param callable
     * A [Consumer] that is called for any found [ItemHandler].
     * @param <T>
     * The type of [ItemHandler] to call.
     *
     * @return Whether or not an [ItemHandler] was found.
    </T> */
    @ParametersAreNonnullByDefault
    fun <T : ItemHandler?> callItemHandler(c: Class<T?>, callable: Consumer<T?>): Boolean {
        val handler = itemHandlers.get(c)

        if (handler.isPresent) {
            try {
                callable.accept(c.cast(handler.get()))
            } catch (x: Exception) {
                error("Could not pass \"" + c.getSimpleName() + "\" for " + toString(), x)
            } catch (x: LinkageError) {
                error("Could not pass \"" + c.getSimpleName() + "\" for " + toString(), x)
            }

            return true
        }

        return false
    }

    override fun toString(): String {
        return if (addon == null) {
            javaClass.getSimpleName() + " - '" + id + "'"
        } else {
            javaClass.getSimpleName() + " - '" + id + "' (" + addon!!.javaPlugin.name + " v" + addon!!.pluginVersion + ')'
        }
    }

    @Nonnull
    override fun getDrops(): MutableCollection<ItemStack?> {
        return mutableListOf(itemStackTemplate.clone())
    }

    @Nonnull
    override fun getDrops(p: Player): MutableCollection<ItemStack?> {
        return drops
    }

    @ParametersAreNonnullByDefault
    fun info(message: String) {
        Validate.notNull(addon, "Cannot log a message for an unregistered item!")

        val msg = toString() + ": " + message
        addon!!.javaPlugin.logger.log(Level.INFO, msg)
    }

    @ParametersAreNonnullByDefault
    fun warn(message: String) {
        Validate.notNull(addon, "Cannot send a warning for an unregistered item!")

        val msg = toString() + ": " + message
        addon!!.javaPlugin.logger.log(Level.WARNING, msg)

        if (addon!!.bugTrackerURL != null) {
            // We can prompt the server operator to report it to the addon's bug tracker
            addon!!.javaPlugin.logger.log(Level.WARNING, "You can report this warning here: {0}", addon!!.bugTrackerURL)
        }
    }

    /**
     * This will throw a [Throwable] to the console and signal that
     * this was caused by this [SlimefunItem].
     *
     * @param message
     * The message to display alongside this Stacktrace
     * @param throwable
     * The [Throwable] to throw as a stacktrace.
     */
    @ParametersAreNonnullByDefault
    fun error(message: String, throwable: Throwable) {
        Validate.notNull(addon, "Cannot send an error for an unregistered item!")
        addon!!.javaPlugin.logger.log(
            Level.SEVERE,
            "Item \"{0}\" from {1} v{2} has caused an Error!",
            arrayOf<Any?>(id, addon!!.javaPlugin.name, addon!!.pluginVersion)
        )

        if (addon!!.bugTrackerURL != null) {
            // We can prompt the server operator to report it to the addon's bug tracker
            addon!!.javaPlugin.logger.log(Level.SEVERE, "You can report it here: {0}", addon!!.bugTrackerURL)
        }

        addon!!.javaPlugin.logger.log(Level.SEVERE, message, throwable)

        // We definitely want to re-throw them during Unit Tests
        if (throwable is RuntimeException && getMinecraftVersion() == MinecraftVersion.UNIT_TEST) {
            throw throwable
        }
    }

//    @ParametersAreNonnullByDefault
//    fun sendDeprecationWarning(player: Player) {
//        Validate.notNull(player, "The Player must not be null.")
//        localization.sendMessage(player, "messages.deprecated-item")
//    }

    fun canUse(@Nonnull p: Player, sendMessage: Boolean): Boolean {
        Validate.notNull(p, "The Player cannot be null!")

        if (this.state == ItemState.VANILLA_FALLBACK) {
            // Vanilla items (which fell back) can always be used.
            return true
        } else if (this.isDisabled) {
            // The Item has been disabled in the config
            if (sendMessage) {
                localization.sendMessage(p, "messages.disabled-item", true)
            }

            return false
        } else if (!getWorldSettingsService().isEnabled(p.world, this)) {
            // The Item was disabled in the current World
            if (sendMessage) {
                localization.sendMessage(p, "messages.disabled-in-world", true)
            }

            return false
        } else if (!getPermissionsService().hasPermission(p, this)) {
            // The Player does not have the required permission node
            if (sendMessage) {
                localization.sendMessage(p, "messages.no-permission", true)
            }

            return false
        } else if (hasResearch()) {
            val profile = PlayerProfile.find(p)

            if (!profile.isPresent) {
                /*
                     * We will return false since we cannot know the answer yet.
                     * But we will schedule the Profile for loading and not send
                     * any message.
                     */
                PlayerProfile.request(p)
                return false
            } else if (!profile.get().hasUnlocked(research)) {
                /*
                     * The Profile is loaded but Player has not unlocked the
                     * required Research to use this SlimefunItem.
                     */
                if (sendMessage && this !is VanillaItem) {
                    localization.sendMessage(p, "messages.not-researched", true) { s: String? ->
                        s!!.replace(
                            "%item%",
                            this.itemName
                        )
                    }
                }

                return false
            } else {
                /*
                     * The PlayerProfile is loaded and the Player has unlocked
                     * the required Research.
                     */
                return true
            }
        } else {
            // All checks have passed, the Player can use this item.
            return true
        }
    }

    override fun equals(other: Any?): Boolean {
        return if (other is SlimefunItem) {
            other.id == this.id
        } else {
            false
        }
    }

    override fun hashCode(): Int {
        return id.hashCode()
    }

    companion object {
        /**
         * Retrieve a [SlimefunItem] by its id.
         *
         * @param id
         * The id of the [SlimefunItem]
         * @return The [SlimefunItem] associated with that id. Null if non-existent
         */
        @JvmStatic
        fun getById(@Nonnull id: String): SlimefunItem? {
            return getRegistry().slimefunItemIds[id]
        }

        /**
         * Retrieve a [Optional] [SlimefunItem] by its id.
         *
         * @param id
         * The id of the [SlimefunItem]
         * @return The [Optional] [SlimefunItem] associated with that id. Empty if non-existent
         */
//        @Nonnull
//        fun getOptionalById(@Nonnull id: String): Optional<SlimefunItem> {
//            return Optional.ofNullable<SlimefunItem?>(getById(id))
//        }

        @JvmStatic
        fun getByItem(slimefunItemStack: SlimefunItemStack?): SlimefunItem? {
            if (slimefunItemStack == null) {
                return null
            }

            val delegate = slimefunItemStack.item()
            if (delegate.type == Material.AIR) {
                return null
            }

            return getById(slimefunItemStack.itemId)
        }

        /**
         * Retrieve a [SlimefunItem] from an [ItemStack].
         *
         * @param item
         * The [ItemStack] to check
         * @return The [SlimefunItem] associated with this [ItemStack] if present, otherwise null
         */
        @JvmStatic
        fun getByItem(item: ItemStack?): SlimefunItem? {
            if (item == null || item.type == Material.AIR) {
                return null
            }

            val itemID = getItemDataService().getItemData(item)

            return itemID.map<SlimefunItem?>(Function { id: String? -> getById(id!!) }).orElse(null)
        }

        /**
         * Retrieve a [Optional] [SlimefunItem] from an [ItemStack].
         *
         * @param item
         * The [ItemStack] to check
         * @return The [Optional] [SlimefunItem] associated with this [ItemStack] if present, otherwise empty
         */
//        @Nonnull
//        fun getOptionalByItem(item: ItemStack?): Optional<SlimefunItem> {
//            return Optional.ofNullable<SlimefunItem?>(getByItem(item))
//        }
    }
}
