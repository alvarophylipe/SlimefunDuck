package io.github.ducklin.api.items

import io.github.bakedlibs.dough.items.CustomItemStack
import io.github.ducklin.api.AbstractExtension
import io.github.ducklin.api.DucklinAPI
import org.apache.commons.lang3.Validate
import org.bukkit.ChatColor
import org.bukkit.Keyed
import org.bukkit.Material
import org.bukkit.NamespacedKey
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemFlag
import org.bukkit.inventory.ItemStack
import java.util.*
import java.util.function.ToIntFunction
import javax.annotation.Nonnull


open class ItemGroup(
    val key: NamespacedKey,
    val item: ItemStack,
    var tier: Int = 3
) : Keyed {

    var extension: AbstractExtension? = null; private set

    private val _items = mutableListOf<DucklinItem>()
    val items: List<DucklinItem> get() = _items.toList()
    var isCrossExtensionItemGroup: Boolean = false

    init {
        require(item.type != Material.AIR) {
            "ItemStack must not be AIR"
        }

        item.editMeta { meta ->
            meta.addItemFlags(
                ItemFlag.HIDE_ATTRIBUTES,
                ItemFlag.HIDE_ENCHANTS,
                ItemFlag.HIDE_ADDITIONAL_TOOLTIP
            )
        }
    }

    override fun getKey(): NamespacedKey = key

    open fun register(extension: AbstractExtension) {
        require(!isRegistered) {
            "This ItemGroup has already been registered!"
        }

        this.extension = extension

        DucklinAPI.
            A.getAllItemGroups().add(this)
        sortCategoriesByTier()
    }

    val isRegistered: Boolean
        /**
         * This method returns whether this [ItemGroup] has been registered yet.
         * More specifically: Whether [.register] was called or not.
         *
         * @return Whether this [ItemGroup] has been registered
         */
        get() = this.extension != null && Slimefun.getRegistry().getAllItemGroups().contains(this)



    fun setTier(tier: Int) {
        this.tier = tier

        // Refresh ItemGroup order if already registered.
        if (this.isRegistered) {
            sortCategoriesByTier()
        }
    }

    /**
     * This refreshes the [ItemGroup] order.
     */
    private fun sortCategoriesByTier() {
        val categories: MutableList<ItemGroup?> = Slimefun.getRegistry().getAllItemGroups()
        Collections.sort<ItemGroup?>(
            categories,
            Comparator.comparingInt<ItemGroup?>(ToIntFunction { obj: ItemGroup? -> obj!!.getTier() })
        )
    }

    fun getExtension(): AbstractExtension? {
        return extension
    }


    open fun add(item: DucklinItem) {
        Validate.notNull(item, "Cannot add null Items to an ItemGroup!")

        if (items.contains(item)) {
            return
        }

        if (this.isRegistered && !this.isCrossExtensionItemGroup && !item.extension?.name
                .equals(this.extension?.name)
        ) {
            item.warn(
                "This item does not belong into ItemGroup " + this + " as that group belongs to " + this.extension?.name
            )
        }

        _items.add(item)
    }


    open fun remove(item: DucklinItem) {
        Validate.notNull(item, "Cannot remove null from an ItemGroup!")
        _items.remove(item)
    }


    open fun getItem(p: Player): ItemStack {
        return CustomItemStack(item, { meta ->
            var name: String? = DucklinAPI.services.localizationService.getItemGroupName(p, key)
            if (name == null) {
                name = item.itemMeta.itemName().toString()
            }

            if (this is SeasonalItemGroup) {
                meta.setDisplayName(ChatColor.GOLD.toString() + name)
            } else {
                meta.setDisplayName(ChatColor.YELLOW.toString() + name)
            }
            meta.setLore(
                Arrays.asList<T?>(
                    "",
                    ChatColor.GRAY.toString() + "\u21E8 " + ChatColor.GREEN + Slimefun.getLocalization()
                        .getMessage(p, "guide.tooltips.open-itemgroup")
                )
            )
        })
    }

    @get:Nonnull
    val unlocalizedName: String
        /**
         * This method makes Walshy happy.
         * It adds a way to get the name of a [ItemGroup] without localization nor coloring.
         *
         * @return The unlocalized name of this [ItemGroup]
         */
        get() = ChatColor.stripColor(item.getItemMeta().getDisplayName())!!

    /**
     * This returns the localized display name of this [ItemGroup] for the given [Player].
     * The method will fall back to [.getUnlocalizedName] if no translation was found.
     *
     * @param p
     * The [Player] who to translate the name for
     *
     * @return The localized name of this [ItemGroup]
     */
    @Nonnull
    fun getDisplayName(@Nonnull p: Player): String {
        val localized: String? = Slimefun.getLocalization().getItemGroupName(p, getKey())

        if (localized != null) {
            return localized
        } else {
            return this.unlocalizedName
        }
    }

    /**
     * This method returns whether a given [DucklinItem] exists in this [ItemGroup].
     *
     * @param item
     * The [DucklinItem] to find
     *
     * @return Whether the given [DucklinItem] was found in this [ItemGroup]
     */
    open fun contains(item: DucklinItem?): Boolean {
        return item != null && items.contains(item)
    }

    fun isAccessible(@Nonnull p: Player): Boolean {
        return true
    }

    /**
     * This method returns whether this [ItemGroup] can be viewed
     * by the given [Player]. Empty [ItemGroups][ItemGroup] will not
     * be visible. This includes [ItemGroups][ItemGroup] where every [DucklinItem]
     * is disabled. If an [ItemGroup] is not accessible by the [Player],
     * see [.isAccessible], this method will also return false.
     *
     * @param p
     * The [Player] to check for
     *
     * @return Whether this [ItemGroup] is visible to the given [Player]
     */
    open fun isVisible(@Nonnull p: Player): Boolean {
        if (items.isEmpty() || !isAccessible(p)) {
            return false
        }

        for (slimefunItem in this.items) {
            /*
             * If any item for this item group is visible,
             * the item group itself is also visible.
             * Empty item groups are not displayed.
             */
            if (!slimefunItem.isHidden() && !slimefunItem.isDisabledIn(p.getWorld())) {
                return true
            }
        }

        return false
    }

    override fun equals(obj: Any?): Boolean {
        if (obj is ItemGroup) {
            return obj.getKey() == this.getKey()
        } else {
            return false
        }
    }

    override fun hashCode(): Int {
        return key.hashCode()
    }

    override fun toString(): String {
        return javaClass.getSimpleName() + " {" + key + ",tier=" + tier + "}"
    }

    /**
     * This method checks whether this [ItemGroup] will be hidden for the specified
     * [Player].
     *
     * Categories are hidden if all of their items have been disabled.
     *
     * @param p
     * The [Player] to check for
     *
     * @return Whether this [ItemGroup] will be hidden to the given [Player]
     */
    @Deprecated("")
    fun isHidden(@Nonnull p: Player): Boolean {
        return !isVisible(p)
    }
}
