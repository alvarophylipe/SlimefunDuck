package io.github.ducklin.api.recipes

import io.github.bakedlibs.dough.items.CustomItemStack
import io.github.bakedlibs.dough.recipes.MinecraftRecipe
import io.github.ducklin.api.DucklinAPI
import io.github.ducklin.api.items.DucklinItem
import io.github.ducklin.api.items.DucklinItemStack
import net.kyori.adventure.text.Component
import org.bukkit.Keyed
import org.bukkit.Material
import org.bukkit.NamespacedKey
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack

typealias RecipeCallback = (Array<ItemStack?>, ItemStack) -> Unit

sealed class RecipeType(
    val key: NamespacedKey,
    val item: ItemStack? = null,
    val machine: String = "",
    val callback: RecipeCallback? = null
) {

    object NULL : RecipeType(
        NamespacedKey(DucklinAPI.plugin, "null")
    )

    object MULTIBLOCK : RecipeType(
        NamespacedKey(DucklinAPI.plugin, "multiblock"),
        CustomItemStack(Material.BRICKS, "MultiBlock", "Build it in the World")
    )

    object ARMOR_FORGE : RecipeType(
        NamespacedKey(DucklinAPI.plugin, "armor_forge"),
//        lore = listOf("Craft it in an Armor Forge")
    )

    object GRIND_STONE : RecipeType(
        NamespacedKey(DucklinAPI.plugin, "grind_stone"),
//        lore = listOf("Grind it using the Grind Stone")
    )

    object SMELTERY : RecipeType(
        NamespacedKey(DucklinAPI.plugin, "smeltery"),
//        lore = listOf("Smelt it using a Smeltery")
    )

    object ORE_CRUSHER : RecipeType(
        NamespacedKey(DucklinAPI.plugin, "ore_crusher"),
//        lore = listOf("Crush it using the Ore Crusher")
    )

    object GOLD_PAN : RecipeType(
        NamespacedKey(DucklinAPI.plugin, "gold_pan"),
//        lore = listOf("Use a Gold Pan on Gravel to obtain this Item")
    )

    object COMPRESSOR : RecipeType(
        NamespacedKey(DucklinAPI.plugin, "compressor"),
//        lore = listOf("Compress it using the Compressor")
    )

    object PRESSURE_CHAMBER : RecipeType(
        NamespacedKey(DucklinAPI.plugin, "pressure_chamber"),
//        lore = listOf("Compress it using the Pressure Chamber")
    )

    object MAGIC_WORKBENCH : RecipeType(
        NamespacedKey(DucklinAPI.plugin, "magic_workbench"),
//        lore = listOf("Craft it in a Magic Workbench")
    )

    object ORE_WASHER : RecipeType(
        NamespacedKey(DucklinAPI.plugin, "ore_washer"),
//        lore = listOf("Wash it in an Ore Washer")
    )

    object ENHANCED_CRAFTING_TABLE : RecipeType(
        NamespacedKey(DucklinAPI.plugin, "enhanced_crafting_table"),
//        lore = listOf(
//            "A regular Crafting Table cannot",
//            "hold this massive Amount of Power..."
//        )
    )

    object JUICER : RecipeType(
        NamespacedKey(DucklinAPI.plugin, "juicer"),
//        lore = listOf("Used for Juice Creation")
    )

    object ANCIENT_ALTAR : RecipeType(
        NamespacedKey(DucklinAPI.plugin, "ancient_altar"),
//        callback = { recipe, output ->
//            val altarRecipe = AltarRecipe(recipe.toList(), output)
//            val altar =  as AncientAltar
//            altar.recipes += altarRecipe
//        }
    )

    object MOB_DROP : RecipeType(
        NamespacedKey(DucklinAPI.plugin, "mob_drop"),
        CustomItemStack(Material.IRON_SWORD, "Mob Drop"),
//        callback = { recipe, output ->
//            val name = recipe.getOrNull(4)?.itemMeta?.displayName?.let {
//                net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer.legacyAmpersand().serialize(it)
//            } ?: return@RecipeType
//            val entity = EntityType.valueOf(name.uppercase().replace(' ', '_'))
//            val drops = Slimefun.registry.mobDrops.getOrPut(entity) { mutableSetOf() }
//            drops += output
//        },
//        lore = listOf("Kill the specified Mob to obtain this Item")
    )

    object BARTER_DROP : RecipeType(
        NamespacedKey(DucklinAPI.plugin, "barter_drop"),
        CustomItemStack(Material.GOLD_INGOT, "Barter Drop"),
//        callback = { _, output ->
//            Slimefun.registry.barteringDrops += output
//        },
//        lore = listOf("Barter with piglins for a chance", "to obtain this item")
    )

    object INTERACT : RecipeType(
        NamespacedKey(DucklinAPI.plugin, "interact"),
//        CustomItemStack(Material.PLAYER_HEAD, "Interact"),
//        lore = listOf("Right click with this item")
    )

    object HEATED_PRESSURE_CHAMBER : RecipeType(
        NamespacedKey(DucklinAPI.plugin, "heated_pressure_chamber"),
    )

    object FOOD_FABRICATOR : RecipeType(
        NamespacedKey(DucklinAPI.plugin, "food_fabricator"),
    )

    object FOOD_COMPOSTER : RecipeType(
        NamespacedKey(DucklinAPI.plugin, "food_composter"),
    )

    object FREEZER : RecipeType(
        NamespacedKey(DucklinAPI.plugin, "freezer"),
    )

    object REFINERY : RecipeType(
        NamespacedKey(DucklinAPI.plugin, "refinery"),
    )

    object GEO_MINER : RecipeType(
        NamespacedKey(DucklinAPI.plugin, "geo_miner"),
    )

    object NUCLEAR_REACTOR : RecipeType(
        NamespacedKey(DucklinAPI.plugin, "nuclear_reactor"),
    )

    constructor(
        key: NamespacedKey,
        ducklinItem: DucklinItemStack,
        vararg lore: String
    ) : this(key, ducklinItem.item, null, *lore)

    constructor(
        key: NamespacedKey,
        item: ItemStack,
        vararg lore: String
    ) : this(key, item, null, *lore)

    constructor(
        key: NamespacedKey,
        item: ItemStack,
        callback: RecipeCallback? = null,
        vararg lore: String
    ) : this (
        key,
        CustomItemStack(item, null, *lore),
        DucklinAPI.services.itemDataService.getItemData(item) ?: "",
        callback
    )

    constructor(
        recipe: MinecraftRecipe<*>
    ) : this (
        NamespacedKey.minecraft(recipe.recipeClass.simpleName.lowercase().replace("recipe", "")),
        ItemStack(recipe.machine),
        ""
    )

    open fun register(recipe: Array<ItemStack?>, result: ItemStack) {
        callback?.invoke(recipe, result) ?: run {
            val ducklinItem = DucklinItem.getById(machine)

            if (ducklinItem is MultiBlockMachine) {
                ducklinItem.addRecipe(recipe, result)
            }
        }
    }

    fun toItem(): ItemStack? = item?.clone()

}