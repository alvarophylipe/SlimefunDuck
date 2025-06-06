package io.github.thebusybiscuit.slimefun4.implementation.setup;

import java.time.Month;

import org.bukkit.Material;
import org.bukkit.NamespacedKey;

import io.github.bakedlibs.dough.items.CustomItemStack;
import io.github.thebusybiscuit.slimefun4.api.items.ItemGroup;
import io.github.thebusybiscuit.slimefun4.api.items.groups.FlexItemGroup;
import io.github.thebusybiscuit.slimefun4.api.items.groups.LockedItemGroup;
import io.github.thebusybiscuit.slimefun4.api.items.groups.SeasonalItemGroup;
import io.github.thebusybiscuit.slimefun4.implementation.Slimefun;
import io.github.thebusybiscuit.slimefun4.implementation.SlimefunItems;
import io.github.thebusybiscuit.slimefun4.utils.ChatUtils;
import io.github.thebusybiscuit.slimefun4.utils.HeadTexture;
import io.github.thebusybiscuit.slimefun4.utils.SlimefunUtils;

/**
 * This class holds a reference to every {@link ItemGroup}
 * found in Slimefun itself.
 * 
 * Addons should use their own {@link ItemGroup} hence why the visible of this class was now
 * changed to package-private. Only {@link SlimefunItemSetup} has access to this class.
 * 
 * @author TheBusyBiscuit
 * 
 * @see ItemGroup
 * @see LockedItemGroup
 * @see SeasonalItemGroup
 *
 */
class DefaultItemGroups {

    // Standard Item Groups
    protected final ItemGroup weapons = new ItemGroup(new NamespacedKey(Slimefun.instance(), "weapons"), CustomItemStack.create(SlimefunItems.BLADE_OF_VAMPIRES.item(), "&7Weapons"), 1);
    protected final ItemGroup tools = new ItemGroup(new NamespacedKey(Slimefun.instance(), "tools"), CustomItemStack.create(SlimefunItems.SMELTERS_PICKAXE.item(), "&7Tools"), 1);
    protected final ItemGroup usefulItems = new ItemGroup(new NamespacedKey(Slimefun.instance(), "items"), CustomItemStack.create(SlimefunItems.BACKPACK_MEDIUM.item(), "&7Useful Items"), 1);
    protected final ItemGroup basicMachines = new ItemGroup(new NamespacedKey(Slimefun.instance(), "basic_machines"), CustomItemStack.create(SlimefunItems.ENHANCED_CRAFTING_TABLE.item(), "&7Basic Machines"), 1);
    protected final ItemGroup food = new ItemGroup(new NamespacedKey(Slimefun.instance(), "food"), CustomItemStack.create(SlimefunItems.FORTUNE_COOKIE.item(), "&7Food"), 2);
    protected final ItemGroup armor = new ItemGroup(new NamespacedKey(Slimefun.instance(), "armor"), CustomItemStack.create(SlimefunItems.DAMASCUS_STEEL_CHESTPLATE.item(), "&7Armor"), 2);

    // Magical
    protected final ItemGroup magicalResources = new ItemGroup(new NamespacedKey(Slimefun.instance(), "magical_items"), CustomItemStack.create(SlimefunItems.ENDER_RUNE.item(), "&7Magical Items"), 2);
    protected final ItemGroup magicalGadgets = new ItemGroup(new NamespacedKey(Slimefun.instance(), "magical_gadgets"), CustomItemStack.create(SlimefunItems.INFUSED_ELYTRA.item(), "&7Magical Gadgets"), 3);
    protected final ItemGroup magicalArmor = new ItemGroup(new NamespacedKey(Slimefun.instance(), "magical_armor"), CustomItemStack.create(SlimefunItems.ENDER_HELMET.item(), "&7Magical Armor"), 2);

    // Resources and tech stuff
    protected final ItemGroup misc = new ItemGroup(new NamespacedKey(Slimefun.instance(), "misc"), CustomItemStack.create(SlimefunItems.TIN_CAN.item(), "&7Miscellaneous"), 2);
    protected final ItemGroup technicalComponents = new ItemGroup(new NamespacedKey(Slimefun.instance(), "tech_misc"), CustomItemStack.create(SlimefunItems.HEATING_COIL.item(), "&7Technical Components"), 2);
    protected final ItemGroup technicalGadgets = new ItemGroup(new NamespacedKey(Slimefun.instance(), "technical_gadgets"), CustomItemStack.create(SlimefunItems.STEEL_JETPACK.item(), "&7Technical Gadgets"), 3);
    protected final ItemGroup resources = new ItemGroup(new NamespacedKey(Slimefun.instance(), "resources"), CustomItemStack.create(SlimefunItems.SYNTHETIC_SAPPHIRE.item(), "&7Resources"), 1);

    // Locked Item Groups
    protected final LockedItemGroup electricity = new LockedItemGroup(new NamespacedKey(Slimefun.instance(), "electricity"), CustomItemStack.create(SlimefunItems.NUCLEAR_REACTOR.item(), "&bEnergy and Electricity"), 4, basicMachines.getKey());
    protected final LockedItemGroup androids = new LockedItemGroup(new NamespacedKey(Slimefun.instance(), "androids"), CustomItemStack.create(SlimefunItems.PROGRAMMABLE_ANDROID.item(), "&cProgrammable Androids"), 4, basicMachines.getKey());
    protected final ItemGroup cargo = new LockedItemGroup(new NamespacedKey(Slimefun.instance(), "cargo"), CustomItemStack.create(SlimefunItems.CARGO_MANAGER.item(), "&cCargo Management"), 4, basicMachines.getKey());
    protected final LockedItemGroup gps = new LockedItemGroup(new NamespacedKey(Slimefun.instance(), "gps"), CustomItemStack.create(SlimefunItems.GPS_TRANSMITTER.item(), "&bGPS-based Machines"), 4, basicMachines.getKey());

    // Seasonal Item Groups
    protected final SeasonalItemGroup christmas = new SeasonalItemGroup(new NamespacedKey(Slimefun.instance(), "christmas"), Month.DECEMBER, 1, CustomItemStack.create(SlimefunUtils.getCustomHead("215ba31cde2671b8f176de6a9ffd008035f0590d63ee240be6e8921cd2037a45"), ChatUtils.christmas("Christmas") + " &7(December only)"));
    protected final SeasonalItemGroup valentinesDay = new SeasonalItemGroup(new NamespacedKey(Slimefun.instance(), "valentines_day"), Month.FEBRUARY, 2, CustomItemStack.create(SlimefunUtils.getCustomHead("55d89431d14bfef2060461b4a3565614dc51115c001fae2508e8684bc0ae6a80"), "&dValentine's Day" + " &7(14th February)"));
    protected final SeasonalItemGroup easter = new SeasonalItemGroup(new NamespacedKey(Slimefun.instance(), "easter"), Month.APRIL, 2, CustomItemStack.create(HeadTexture.EASTER_EGG.getAsItemStack(), "&6Easter" + " &7(April)"));
    protected final SeasonalItemGroup birthday = new SeasonalItemGroup(new NamespacedKey(Slimefun.instance(), "birthday"), Month.OCTOBER, 1, CustomItemStack.create(Material.FIREWORK_ROCKET, "&a&lTheBusyBiscuit's Birthday &7(26th October)"));
    protected final SeasonalItemGroup halloween = new SeasonalItemGroup(new NamespacedKey(Slimefun.instance(), "halloween"), Month.OCTOBER, 1, CustomItemStack.create(Material.JACK_O_LANTERN, "&6&lHalloween &7(31st October)"));

    // Flex Item Groups
    protected final FlexItemGroup rickFlexGroup = new RickFlexGroup(new NamespacedKey(Slimefun.instance(), "rick"));
}
