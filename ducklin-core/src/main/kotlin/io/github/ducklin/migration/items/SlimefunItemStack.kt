@file:Suppress("DEPRECATION")

package io.github.ducklin.migration.items

import io.github.bakedlibs.dough.common.CommonPatterns
import io.github.bakedlibs.dough.items.ItemMetaSnapshot
import io.github.bakedlibs.dough.skins.PlayerHead
import io.github.bakedlibs.dough.skins.PlayerSkin
import io.github.ducklin.core.Slimefun
import io.github.ducklin.migration.MinecraftVersion
import io.github.ducklin.migration.utils.HeadTexture
import io.github.ducklin.migration.utils.compatibility.VersionedItemFlag
import org.apache.commons.lang.Validate
import org.bukkit.ChatColor
import org.bukkit.Color
import org.bukkit.Material
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.meta.ItemMeta
import org.bukkit.inventory.meta.LeatherArmorMeta
import org.bukkit.inventory.meta.PotionMeta
import org.bukkit.potion.PotionEffect
import org.bukkit.potion.PotionEffectType
import java.nio.charset.StandardCharsets
import java.util.Base64
import java.util.Locale
import java.util.function.Consumer
import javax.annotation.Nonnull
import kotlin.compareTo

/**
 * The [SlimefunItemStack] functions as the base for any
 * [SlimefunItem].
 *
 * @author TheBusyBiscuit
 * @author Walshy
 */
class SlimefunItemStack : ItemStack {
    /**
     * Returns the id that was given to this [SlimefunItemStack].
     *
     * @return The [SlimefunItem] id for this [SlimefunItemStack]
     */
    var itemId: String = ""
    private var itemMetaSnapshot: ItemMetaSnapshot? = null
    var texture: String? = null

    constructor(
        id: String,
        item: ItemStack
    ) : super(item) {
        this.itemId = id
        requireValid(id)
        tagMeta(id)
    }

    constructor(
        id: String,
        type: Material,
        amount: Int = 1
    ) : super(type, amount) {
        this.itemId = id
        requireValid(id)
        tagMeta(id)
    }

    constructor(
        id: String,
        item: ItemStack,
        consumer: Consumer<ItemMeta>
    ) : this(id, item) {
        editMeta { meta ->
            consumer.accept(meta)
        }
    }

    constructor(
        id: String,
        type: Material,
        consumer: Consumer<ItemMeta>
    ) : this(id, ItemStack(type), consumer)

    constructor(
        id: String,
        type: Material,
        name: String? = null,
        consumer: Consumer<ItemMeta> = Consumer { }
    ) : this(id, type) {
        editMeta { meta ->
            name?.let { meta.setDisplayName(it.colored()) }
            consumer.accept(meta)
        }
    }

    constructor(
        id: String,
        item: ItemStack,
        name: String? = null,
        vararg lore: String
    ) : this(id, item) {
        editMeta { meta ->
            name?.let { meta.setDisplayName(it.colored()) }
            if (lore.isNotEmpty()) meta.lore = lore.map { it.colored() }
        }
    }

    constructor(
        id: String,
        type: Material,
        name: String? = null,
        vararg lore: String
    ) : this(id, ItemStack(type), name, *lore)

    constructor(
        id: String,
        type: Material,
        color: Color,
        name: String? = null,
        vararg lore: String,
    ) : this(id, type, name, *lore) {
        editMeta { meta ->
            when (meta) {
                is LeatherArmorMeta -> meta.setColor(color)
                is PotionMeta -> meta.color = color
                else -> {}
            }
        }
    }

    constructor(
        id: String,
        color: Color,
        effect: PotionEffect,
        name: String? = null,
        vararg lore: String
    ) : this(id, Material.POTION, color, name, *lore) {
        editMeta { raw ->
            val meta = raw as PotionMeta
            meta.addCustomEffect(effect, true)
            if (effect.type == PotionEffectType.SATURATION) {
                meta.addItemFlags(VersionedItemFlag.HIDE_ADDITIONAL_TOOLTIP)
            }
        }
    }

    constructor(
        id: String,
        texture: String,
        name: String? = null,
        vararg lore: String
    ) : this(id, getSkull(id, texture), name, *lore) {
        this.texture = getTexture(id, texture)
    }

    constructor(
        id: String,
        texture: HeadTexture,
        name: String? = null,
        vararg lore: String
    ) : this(id, getSkull(id, texture.texture), name, *lore) {
        this.texture = getTexture(id, texture.texture)
    }

    constructor(
        id: String,
        texture: String,
        name: String? = null,
        consumer: Consumer<ItemMeta>
    ) : this(id, getSkull(id, texture)) {
        this.texture = getTexture(id, texture)
        editMeta {
            name?.let { n -> it.setDisplayName(n.colored()) }
            consumer.accept(it)
        }
    }

    constructor(
        id: String,
        texture: HeadTexture,
        name: String? = null,
        consumer: Consumer<ItemMeta>
    ) : this(id, getSkull(id, texture.texture)) {
        this.texture = getTexture(id, texture.texture)
        editMeta {
            name?.let { n -> it.setDisplayName(n.colored()) }
            consumer.accept(it)
        }
    }

    constructor(
        base: SlimefunItemStack,
        amount: Int
    ) : super(base.type, amount) {
        this.itemId = base.itemId
        this.texture = base.texture
        this.itemMeta = base.itemMeta?.clone()
        tagMeta(itemId)
    }

    val item: SlimefunItem?
        /**
         * Gets the [SlimefunItem] associated for this [SlimefunItemStack].
         * Null if no item is found.
         *
         * @return The [SlimefunItem] for this [SlimefunItemStack], null if not found.
         */
        get() = SlimefunItem.getById(this.itemId)

    /**
     * This method returns the associated [SlimefunItem] and casts it to the provided
     * [Class].
     *
     * If no item was found or the found [SlimefunItem] is not of the requested type,
     * the method will return null.
     *
     * @param <T>
     * The type of [SlimefunItem] to cast this to
     * @param type
     * The [Class] of the target [SlimefunItem]
     *
     * @return The [SlimefunItem] this [SlimefunItem] represents, cast to the given type
    </T> */
    fun <T : SlimefunItem?> getItem(@Nonnull type: Class<T?>): T? {
        val item = this.item
        return if (type.isInstance(item)) type.cast(item) else null
    }

    val displayName: String?
        get() {
            if (itemMetaSnapshot == null) {
                // Just to be extra safe
                return null
            }

            return itemMetaSnapshot!!.displayName.orElse(null)
        }

    fun getDisplayNameOrElse(fallback: String): String = displayName ?: fallback

    @Nonnull
    override fun clone(): ItemStack {
        return super.clone()
    }

    override fun toString(): String {
        return "SlimefunItemStack (" + this.itemId + (if (this.amount.compareTo(1) > 1) (" x " + this.amount) else "") + ')'
    }

    /**
     * {@inheritDoc}
     */
    override fun equals(obj: Any?): Boolean {
        // We don't want people to override this, it should use the super method
        return super.equals(obj)
    }

    /**
     * {@inheritDoc}
     */
    override fun hashCode(): Int {
        // We don't want people to override this, it should use the super method
        return super.hashCode()
    }

    /**
     * @return underlying ItemStack used
     */
    fun item(): ItemStack {
        return this.clone()
    }

//    fun editMeta(consumer: Consumer<ItemMeta?>): Boolean {
//        return super.editMeta(consumer)
//    }

    override fun <M : ItemMeta?> editMeta(metaClass: Class<M?>, consumer: Consumer<in M?>): Boolean {
        return super.editMeta<M?>(metaClass, consumer)
    }

    private fun requireValid(id: String) {
        require(id == id.uppercase(Locale.ROOT)) {
            "Slimefun Item Ids must be uppercase! (e. g. 'MY_ITEM_ID')"
        }
        checkNotNull(Slimefun.instance()) {
            "A SlimefunItemStack must never be created before your Plugin was enabled."
        }
    }

    private fun tagMeta(id: String) {
        itemMeta = itemMeta.also { meta ->
            Slimefun.getItemDataService().setItemData(meta, id)
            Slimefun.itemTextureService.setTexture(meta, id)
        }
    }

    companion object {

        private fun String.colored(): String =
            ChatColor.translateAlternateColorCodes('&', this)

        @Nonnull
        private fun getSkull(@Nonnull id: String, @Nonnull texture: String): ItemStack {
            if (Slimefun.Companion.getMinecraftVersion() == MinecraftVersion.UNIT_TEST) {
                return ItemStack(Material.PLAYER_HEAD)
            }

            val skin = PlayerSkin.fromBase64(getTexture(id, texture))
            return PlayerHead.getItemStack(skin)
        }

        @Nonnull
        private fun getTexture(@Nonnull id: String, @Nonnull texture: String): String {
            Validate.notNull(id, "The id cannot be null")
            Validate.notNull(texture, "The texture cannot be null")

            if (texture.startsWith("ey")) {
                return texture
            } else if (CommonPatterns.HEXADECIMAL.matcher(texture).matches()) {
                val value =
                    "{\"textures\":{\"SKIN\":{\"url\":\"http://textures.minecraft.net/texture/$texture\"}}}"
                return Base64.getEncoder().encodeToString(value.toByteArray(StandardCharsets.UTF_8))
            } else {
                throw IllegalArgumentException("The provided texture for Item \"$id\" does not seem to be a valid texture String!")
            }
        }
    }
}