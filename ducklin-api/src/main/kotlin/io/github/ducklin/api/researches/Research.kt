package io.github.ducklin.api.researches

import io.github.ducklin.api.DucklinAPI
import io.github.ducklin.api.items.DucklinItem
import io.github.ducklin.api.items.ItemState
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.minimessage.MiniMessage
import org.bukkit.Keyed
import org.bukkit.NamespacedKey
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack

class Research(
    private val key: NamespacedKey,
    val id: Int,
    private val defaultName: String,
    cost: Int
) : Keyed {

    var isEnabled: Boolean = true; private set

    var cost: Int = cost
        set(value) {
            require(value > 0) {"Research cost must be zero or greater!"}
            field = value
        }

    private val items: MutableList<DucklinItem> = mutableListOf()

    override fun getKey(): NamespacedKey = key

    val isResearchingEnabled: Boolean
        get() = DucklinAPI.registry.enableResearches && isEnabled

    @Deprecated("Use key instead")
    fun getID(): Int = id

    fun getName(player: Player): Component =
        MiniMessage.miniMessage().deserialize(
            DucklinAPI.services.localizationService.getResearchName(player, key) ?: defaultName
        )

    fun getUnlocalizedName(): Component =MiniMessage.miniMessage().deserialize(defaultName)

    fun addItems(vararg items: DucklinItem?) {
        items.filterNotNull().forEach { it.research = this }
    }

    fun addItems(vararg stacks: ItemStack?): Research = apply {
        stacks.forEach { stack ->
            DucklinItem.getByItem(stack)?.let { it.research = this }
        }
    }

    val affectedItems: List<DucklinItem> get() = items.toList()

    fun hasEnabledItems(): Boolean = items.any { it.state == ItemState.ENABLED }

    fun unlockFromGuide(
        guide: SlimefunGuideImplementation,
        player: Player,
        profile: PlayerProfile,
        sfItem: DucklinItem,
        itemGroup: ItemGroup,
        page: Int
    ) {
        if (player.uniqueId in Slimefun.getRegistry().currentlyResearchingPlayers) return

        if (profile.hasUnlocked(this)) {
            guide.openItemGroup(profile, itemGroup, page)
            return
        }

        val event = PlayerPreResearchEvent(player, this, sfItem)
        Bukkit.getPluginManager().callEvent(event)
        if (event.isCancelled) return

        if (canUnlock(player)) {
            guide.unlockItem(player, sfItem) { guide.openItemGroup(profile, itemGroup, page) }
        } else {
            Slimefun.getLocalization().sendMessage(player, "messages.not-enough-xp", true)
        }
    }

    fun canUnlock(player: Player): Boolean {
        if (!isResearchingEnabled) return true
        val creativeFree = player.gameMode == GameMode.CREATIVE &&
                Slimefun.getRegistry().isFreeCreativeResearchingEnabled
        return creativeFree || player.level >= cost
    }

    fun unlock(player: Player, instant: Boolean, callback: Consumer<Player>? = null) {
        PlayerProfile.get(player, PlayerResearchTask(this, instant, callback))
    }

    fun register() {
        val cfg = Slimefun.getResearchCfg()
        cfg.setDefaultValue("enable-researching", true)
        val path = "${key.namespace}.${key.key}"

        if (cfg.getBoolean("$path.enabled") == false) {
            items.toList().forEach { it.research = null }
            isEnabled = false
            return
        }

        cfg.setDefaultValue("$path.cost", cost)
        cfg.setDefaultValue("$path.enabled", true)

        cost = cfg.getInt("$path.cost")
        isEnabled = true
        Slimefun.getRegistry().researches += this
    }

    companion object {
        @JvmStatic
        fun getResearch(key: NamespacedKey?): Research? =
            key?.let { k -> Slimefun.getRegistry().researches.firstOrNull { it.key == k } }
    }

    override fun toString(): String = "Research ($key)"
}

}