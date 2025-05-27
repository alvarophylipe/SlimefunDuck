package io.github.thebusybiscuit.slimefun4.implementation.setup;

import javax.annotation.ParametersAreNonnullByDefault;

import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItemStack;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;

import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItem;
import io.github.thebusybiscuit.slimefun4.api.researches.Research;
import io.github.thebusybiscuit.slimefun4.implementation.Slimefun;
import io.github.thebusybiscuit.slimefun4.implementation.SlimefunItems;

import java.util.Arrays;

/**
 * This static setup class is used to register all default implementations of
 * {@link Research} on startup.
 *
 * @see Research
 * @see SlimefunItems
 *
 */
public final class ResearchSetup {

    private static boolean alreadyRan = false;

    private ResearchSetup() {}

    public static void setupResearches() {
        if (alreadyRan) {
            throw new UnsupportedOperationException("Researches can only be registered once!");
        }

        alreadyRan = true;

        register("walking_sticks", 0, "Gravetos Estranhos", 1, SlimefunItems.GRANDMAS_WALKING_STICK, SlimefunItems.GRANDPAS_WALKING_STICK);
        register("portable_crafter", 1, "Criador Portátil", 1, SlimefunItems.PORTABLE_CRAFTER);
        register("fortune_cookie", 2, "Biscoito da Sorte", 1, SlimefunItems.FORTUNE_COOKIE);
        register("portable_dustbin", 4, "Lixeira Portátil", 2, SlimefunItems.PORTABLE_DUSTBIN);
        register("meat_jerky", 5, "Carnes Secas", 2, SlimefunItems.BEEF_JERKY, SlimefunItems.FISH_JERKY, SlimefunItems.RABBIT_JERKY, SlimefunItems.MUTTON_JERKY, SlimefunItems.CHICKEN_JERKY, SlimefunItems.PORK_JERKY);
        register("armor_forge", 6, "Criação de Armaduras", 2, SlimefunItems.ARMOR_FORGE);
        register("glowstone_armor", 7, "Armadura de Pedra Luminosa", 3, SlimefunItems.GLOWSTONE_HELMET, SlimefunItems.GLOWSTONE_CHESTPLATE, SlimefunItems.GLOWSTONE_LEGGINGS, SlimefunItems.GLOWSTONE_BOOTS);
        register("lumps", 8, "Pedaços e Magia", 3, SlimefunItems.MAGIC_LUMP_1, SlimefunItems.MAGIC_LUMP_2, SlimefunItems.MAGIC_LUMP_3, SlimefunItems.ENDER_LUMP_1, SlimefunItems.ENDER_LUMP_2, SlimefunItems.ENDER_LUMP_3);
        register("ender_backpack", 9, "Mochila do Fim", 4, SlimefunItems.ENDER_BACKPACK);
        register("ender_armor", 10, "Armadura do Fim", 4, SlimefunItems.ENDER_HELMET, SlimefunItems.ENDER_CHESTPLATE, SlimefunItems.ENDER_LEGGINGS, SlimefunItems.ENDER_BOOTS);
        register("magic_eye_of_ender", 11, "Olho Mágico do Fim", 4, SlimefunItems.MAGIC_EYE_OF_ENDER);
        register("magic_sugar", 12, "Açúcar Mágico", 4, SlimefunItems.MAGIC_SUGAR);
        register("monster_jerky", 13, "Carne Seca de Monstro", 5, SlimefunItems.MONSTER_JERKY);
        register("slime_armor", 14, "Armadura de Slime", 5, SlimefunItems.SLIME_HELMET, SlimefunItems.SLIME_CHESTPLATE, SlimefunItems.SLIME_LEGGINGS, SlimefunItems.SLIME_BOOTS);
        register("sword_of_beheading", 15, "Espada da Decapitação", 6, SlimefunItems.SWORD_OF_BEHEADING);
        register("basic_circuit_board", 16, "Trabalho Elétrico", 8, SlimefunItems.BASIC_CIRCUIT_BOARD);
        register("advanced_circuit_board", 17, "Eletricidade Avançada", 9, SlimefunItems.ADVANCED_CIRCUIT_BOARD);
        register("smeltery", 18, "Fundição Quente", 10, SlimefunItems.SMELTERY);
        register("steel", 19, "Era do Aço", 11, SlimefunItems.STEEL_INGOT);
        register("misc_power_items", 20, "Itens Importantes Relacionados à Energia", 12, SlimefunItems.SULFATE, SlimefunItems.POWER_CRYSTAL);
        register("battery", 21, "Sua Primeira Bateria", 10, SlimefunItems.BATTERY);
        register("steel_plate", 22, "Chapeamento de Aço", 14, SlimefunItems.STEEL_PLATE);
        register("steel_thruster", 23, "Propulsor de Aço", 14, SlimefunItems.STEEL_THRUSTER);
        register("parachute", 24, "Paraquedas", 15, SlimefunItems.PARACHUTE);
        register("grappling_hook", 25, "Gancho de Escalada", 15, SlimefunItems.GRAPPLING_HOOK, SlimefunItems.HOOK, SlimefunItems.CHAIN);
        register("jetpacks", 26, "Jetpacks", 22, SlimefunItems.DURALUMIN_JETPACK, SlimefunItems.BILLON_JETPACK, SlimefunItems.SOLDER_JETPACK, SlimefunItems.STEEL_JETPACK, SlimefunItems.DAMASCUS_STEEL_JETPACK, SlimefunItems.REINFORCED_ALLOY_JETPACK);
        register("multitools", 27, "Multiferramentas", 18, SlimefunItems.DURALUMIN_MULTI_TOOL, SlimefunItems.SOLDER_MULTI_TOOL, SlimefunItems.BILLON_MULTI_TOOL, SlimefunItems.STEEL_MULTI_TOOL, SlimefunItems.DAMASCUS_STEEL_MULTI_TOOL, SlimefunItems.REINFORCED_ALLOY_MULTI_TOOL);
        register("solar_panel_and_helmet", 28, "Energia Solar", 17, SlimefunItems.SOLAR_PANEL, SlimefunItems.SOLAR_HELMET);
        register("elemental_staff", 29, "Cajados Elementais", 17, SlimefunItems.STAFF_ELEMENTAL);
        register("grind_stone", 30, "Pedra de Moer", 4, SlimefunItems.GRIND_STONE);
        register("cactus_armor", 31, "Traje de Cacto", 5, SlimefunItems.CACTUS_BOOTS, SlimefunItems.CACTUS_CHESTPLATE, SlimefunItems.CACTUS_HELMET, SlimefunItems.CACTUS_LEGGINGS);
        register("gold_pan", 32, "Peneira de Ouro", 5, SlimefunItems.GOLD_PAN);
        register("magical_book_cover", 33, "Encadernação de Livros Mágicos", 5, SlimefunItems.MAGICAL_BOOK_COVER);
        register("slimefun_metals", 34, "Novos Metais", 6, SlimefunItems.COPPER_INGOT, SlimefunItems.TIN_INGOT, SlimefunItems.SILVER_INGOT, SlimefunItems.LEAD_INGOT, SlimefunItems.ALUMINUM_INGOT, SlimefunItems.ZINC_INGOT, SlimefunItems.MAGNESIUM_INGOT);
        register("ore_crusher", 35, "Duplicação de Minério", 6, SlimefunItems.ORE_CRUSHER);
        register("bronze", 36, "Criação de Bronze", 8, SlimefunItems.BRONZE_INGOT);
        register("alloys", 37, "Ligas Avançadas", 12, SlimefunItems.BILLON_INGOT, SlimefunItems.DURALUMIN_INGOT, SlimefunItems.ALUMINUM_BRASS_INGOT, SlimefunItems.ALUMINUM_BRONZE_INGOT, SlimefunItems.SOLDER_INGOT, SlimefunItems.CORINTHIAN_BRONZE_INGOT, SlimefunItems.BRASS_INGOT);
        register("compressor_and_carbon", 38, "Criação de Carbono", 9, SlimefunItems.COMPRESSOR, SlimefunItems.CARBON);
        register("gilded_iron_armor", 40, "Armadura de Ferro Dourado", 16, SlimefunItems.GILDED_IRON_HELMET, SlimefunItems.GILDED_IRON_CHESTPLATE, SlimefunItems.GILDED_IRON_LEGGINGS, SlimefunItems.GILDED_IRON_BOOTS);
        register("synthetic_diamond", 41, "Diamantes Sintéticos", 10, SlimefunItems.COMPRESSED_CARBON, SlimefunItems.CARBON_CHUNK, SlimefunItems.SYNTHETIC_DIAMOND);
        register("pressure_chamber", 42, "Câmara de Pressão", 14, SlimefunItems.PRESSURE_CHAMBER);
        register("synthetic_sapphire", 43, "Safiras Sintéticas", 16, SlimefunItems.SYNTHETIC_SAPPHIRE);
        register("damascus_steel", 45, "Aço Damasco", 17, SlimefunItems.DAMASCUS_STEEL_INGOT);
        register("damascus_steel_armor", 46, "Armadura de Aço Damasco", 18, SlimefunItems.DAMASCUS_STEEL_HELMET, SlimefunItems.DAMASCUS_STEEL_CHESTPLATE, SlimefunItems.DAMASCUS_STEEL_LEGGINGS, SlimefunItems.DAMASCUS_STEEL_BOOTS);
        register("reinforced_alloy", 47, "Liga Reforçada", 22, SlimefunItems.HARDENED_METAL_INGOT, SlimefunItems.REINFORCED_ALLOY_INGOT);
        register("carbonado", 48, "Diamantes Negros", 26, SlimefunItems.RAW_CARBONADO, SlimefunItems.CARBONADO);
        register("magic_workbench", 50, "Bancada Mágica", 12, SlimefunItems.MAGIC_WORKBENCH);
        register("wind_staff", 51, "Cajado do Vento", 17, SlimefunItems.STAFF_WIND);
        register("reinforced_armor", 52, "Armadura Reforçada", 26, SlimefunItems.REINFORCED_ALLOY_HELMET, SlimefunItems.REINFORCED_ALLOY_CHESTPLATE, SlimefunItems.REINFORCED_ALLOY_LEGGINGS, SlimefunItems.REINFORCED_ALLOY_BOOTS);
        register("ore_washer", 53, "Lavador de Minério", 5, SlimefunItems.ORE_WASHER, SlimefunItems.STONE_CHUNK, SlimefunItems.SIFTED_ORE);
        register("gold_carats", 54, "Ouro Puro", 7, SlimefunItems.GOLD_4K, SlimefunItems.GOLD_6K, SlimefunItems.GOLD_8K, SlimefunItems.GOLD_10K, SlimefunItems.GOLD_12K, SlimefunItems.GOLD_14K, SlimefunItems.GOLD_16K, SlimefunItems.GOLD_18K, SlimefunItems.GOLD_20K, SlimefunItems.GOLD_22K, SlimefunItems.GOLD_24K);
        register("silicon", 55, "Vale do Silício", 12, SlimefunItems.SILICON, SlimefunItems.FERROSILICON);
        register("fire_staff", 56, "Cajado de Fogo", 2, SlimefunItems.STAFF_FIRE);
//        register("smelters_pickaxe", 57, "Smelters Pickaxe", 17, SlimefunItems.SMELTERS_PICKAXE);
        register("common_talisman", 58, "Talismã Comum", 14, SlimefunItems.COMMON_TALISMAN);
        register("anvil_talisman", 59, "Talismã da Bigorna", 18, SlimefunItems.TALISMAN_ANVIL);
        register("miner_talisman", 60, "Talismã do Mineiro", 18, SlimefunItems.TALISMAN_MINER);
        register("hunter_talisman", 61, "Talismã do Caçador", 18, SlimefunItems.TALISMAN_HUNTER);
        register("lava_talisman", 62, "Talismã do Andarilho da Lava", 18, SlimefunItems.TALISMAN_LAVA);
        register("water_talisman", 63, "Talismã do Respirador Aquático", 18, SlimefunItems.TALISMAN_WATER);
        register("angel_talisman", 64, "Talismã do Anjo", 18, SlimefunItems.TALISMAN_ANGEL);
        register("fire_talisman", 65, "Talismã do Bombeiro", 18, SlimefunItems.TALISMAN_FIRE);
        register("lava_crystal", 67, "Situação Incendiária", 14, SlimefunItems.LAVA_CRYSTAL);
        register("magician_talisman", 68, "Talismã do Mago", 20, SlimefunItems.TALISMAN_MAGICIAN);
        register("traveller_talisman", 69, "Talismã do Viajante", 20, SlimefunItems.TALISMAN_TRAVELLER);
        register("warrior_talisman", 70, "Talismã do Guerreiro", 20, SlimefunItems.TALISMAN_WARRIOR);
        register("knight_talisman", 71, "Talismã do Cavaleiro", 20, SlimefunItems.TALISMAN_KNIGHT);
        register("gilded_iron", 72, "Ferro Brilhante", 11, SlimefunItems.GILDED_IRON);
        register("synthetic_emerald", 73, "Gema Falsa", 17, SlimefunItems.SYNTHETIC_EMERALD);
        register("chainmail_armor", 74, "Armadura de Malha", 8, "CHAIN_HELMET", "CHAIN_CHESTPLATE", "CHAIN_LEGGINGS", "CHAIN_BOOTS");
        register("whirlwind_talisman", 75, "Talismã do Redemoinho", 19, SlimefunItems.TALISMAN_WHIRLWIND);
        register("wizard_talisman", 76, "Talismã do Mago", 22, SlimefunItems.TALISMAN_WIZARD);
        register("lumber_axe", 77, "Machado de Lenhador", 21, SlimefunItems.LUMBER_AXE);
        register("hazmat_suit", 79, "Traje Hazmat", 21, SlimefunItems.SCUBA_HELMET, SlimefunItems.HAZMAT_CHESTPLATE, SlimefunItems.HAZMAT_LEGGINGS, SlimefunItems.HAZMAT_BOOTS);
        register("uranium", 80, "Radioativo", 30, SlimefunItems.TINY_URANIUM, SlimefunItems.SMALL_URANIUM, SlimefunItems.URANIUM);
        register("crushed_ore", 81, "Purificação de Minério", 25, SlimefunItems.CRUSHED_ORE, SlimefunItems.PULVERIZED_ORE, SlimefunItems.PURE_ORE_CLUSTER);
        register("redstone_alloy", 84, "Liga de Redstone", 16, SlimefunItems.REDSTONE_ALLOY);
        register("carbonado_tools", 85, "Máquinas de Nível Superior", 24, SlimefunItems.CARBONADO_MULTI_TOOL, SlimefunItems.CARBONADO_JETPACK, SlimefunItems.CARBONADO_JETBOOTS);
        register("first_aid", 86, "Primeiros Socorros", 2, SlimefunItems.CLOTH, SlimefunItems.RAG, SlimefunItems.BANDAGE, SlimefunItems.SPLINT, SlimefunItems.TIN_CAN, SlimefunItems.VITAMINS, SlimefunItems.MEDICINE);
        register("gold_armor", 87, "Armadura Brilhante", 13, SlimefunItems.GOLDEN_HELMET_12K, SlimefunItems.GOLDEN_CHESTPLATE_12K, SlimefunItems.GOLDEN_LEGGINGS_12K, SlimefunItems.GOLDEN_BOOTS_12K);
        register("night_vision_googles", 89, "Óculos de Visão Noturna", 10, SlimefunItems.NIGHT_VISION_GOGGLES);
//        register("pickaxe_of_containment", 90, "Pickaxe of Containment", 14, SlimefunItems.PICKAXE_OF_CONTAINMENT, SlimefunItems.BROKEN_SPAWNER);
        register("table_saw", 92, "Serra de Mesa", 4, SlimefunItems.TABLE_SAW);
        register("slime_steel_armor", 93, "Armadura de Aço Slime", 27, SlimefunItems.SLIME_HELMET_STEEL, SlimefunItems.SLIME_CHESTPLATE_STEEL, SlimefunItems.SLIME_LEGGINGS_STEEL, SlimefunItems.SLIME_BOOTS_STEEL);
        register("blade_of_vampires", 94, "Lâmina dos Vampiros", 26, SlimefunItems.BLADE_OF_VAMPIRES);
        register("water_staff", 96, "Cajado de Água", 8, SlimefunItems.STAFF_WATER);
        register("24k_gold_block", 97, "Cidade Dourada", 19, SlimefunItems.GOLD_24K_BLOCK);
        register("composter", 99, "Compostagem de Terra", 3, SlimefunItems.COMPOSTER);
        register("farmer_shoes", 100, "Sapatos de Fazendeiro", 4, SlimefunItems.FARMER_SHOES);
        register("explosive_tools", 101, "Ferramentas Explosivas", 30, SlimefunItems.EXPLOSIVE_PICKAXE, SlimefunItems.EXPLOSIVE_SHOVEL);
        register("automated_panning_machine", 102, "Peneira de Ouro Automatizada", 17, SlimefunItems.AUTOMATED_PANNING_MACHINE);
        register("boots_of_the_stomper", 103, "Botas do Pisotador", 19, SlimefunItems.BOOTS_OF_THE_STOMPER);
        register("pickaxe_of_the_seeker", 104, "Picareta do Explorador", 19, SlimefunItems.PICKAXE_OF_THE_SEEKER);
        register("backpacks", 105, "Mochilas", 15, SlimefunItems.BACKPACK_LARGE, SlimefunItems.BACKPACK_MEDIUM, SlimefunItems.BACKPACK_SMALL);
        register("woven_backpack", 106, "Mochila Tecida", 19, SlimefunItems.WOVEN_BACKPACK);
        register("crucible", 107, "Cadinho", 13, SlimefunItems.CRUCIBLE);
        register("gilded_backpack", 108, "Mochila Dourada", 22, SlimefunItems.GILDED_BACKPACK);
        register("armored_jetpack", 111, "Jetpack Blindado", 27, SlimefunItems.ARMORED_JETPACK);
        register("ender_talismans", 112, "Talismãs do Fim", 28, new String[0]);
        register("nickel_and_cobalt", 115, "Mais Minérios", 10, SlimefunItems.NICKEL_INGOT, SlimefunItems.COBALT_INGOT);
        register("magnet", 116, "Metais Magnéticos", 16, SlimefunItems.MAGNET);
        register("infused_magnet", 117, "Ímãs Infundidos", 18, SlimefunItems.INFUSED_MAGNET);
        register("cobalt_pickaxe", 118, "Picareta Veloz", 14, SlimefunItems.COBALT_PICKAXE);
        register("essence_of_afterlife", 119, "Necromancia", 19, SlimefunItems.NECROTIC_SKULL, SlimefunItems.ESSENCE_OF_AFTERLIFE);
        register("bound_backpack", 120, "Armazenamento Vinculado", 22, SlimefunItems.BOUND_BACKPACK);
        register("jetboots", 121, "Botas a Jato", 25, SlimefunItems.DURALUMIN_JETBOOTS, SlimefunItems.BILLON_JETBOOTS, SlimefunItems.SOLDER_JETBOOTS, SlimefunItems.STEEL_JETBOOTS, SlimefunItems.DAMASCUS_STEEL_JETBOOTS, SlimefunItems.REINFORCED_ALLOY_JETBOOTS);
        register("armored_jetboots", 122, "Botas a Jato Blindadas", 27, SlimefunItems.ARMORED_JETBOOTS);
        register("seismic_axe", 123, "Machado Sísmico", 29, SlimefunItems.SEISMIC_AXE);
        register("pickaxe_of_vein_mining", 124, "Picareta de Mineração de Veio", 29, SlimefunItems.PICKAXE_OF_VEIN_MINING);
        register("bound_weapons", 125, "Armas Vinculadas à Alma", 29, SlimefunItems.SOULBOUND_SWORD, SlimefunItems.SOULBOUND_BOW, SlimefunItems.SOULBOUND_TRIDENT);
        register("bound_tools", 126, "Ferramentas Vinculadas à Alma", 29, SlimefunItems.SOULBOUND_PICKAXE, SlimefunItems.SOULBOUND_AXE, SlimefunItems.SOULBOUND_SHOVEL, SlimefunItems.SOULBOUND_HOE);
        register("bound_armor", 127, "Armadura Vinculada à Alma", 29, SlimefunItems.SOULBOUND_HELMET, SlimefunItems.SOULBOUND_CHESTPLATE, SlimefunItems.SOULBOUND_LEGGINGS, SlimefunItems.SOULBOUND_BOOTS);
        register("juicer", 129, "Bebidas Deliciosas", 29, SlimefunItems.JUICER, SlimefunItems.APPLE_JUICE, SlimefunItems.MELON_JUICE, SlimefunItems.CARROT_JUICE, SlimefunItems.PUMPKIN_JUICE, SlimefunItems.SWEET_BERRY_JUICE, SlimefunItems.GLOW_BERRY_JUICE);
//        register("repaired_spawner", 130, "Repairing Spawners", 15, SlimefunItems.REPAIRED_SPAWNER);
        register("enhanced_furnace", 132, "Fornalha Aprimorada", 7, SlimefunItems.ENHANCED_FURNACE, SlimefunItems.ENHANCED_FURNACE_2);
        register("more_enhanced_furnaces", 133, "Melhores Fornalhas", 18, SlimefunItems.ENHANCED_FURNACE_3, SlimefunItems.ENHANCED_FURNACE_4, SlimefunItems.ENHANCED_FURNACE_5, SlimefunItems.ENHANCED_FURNACE_6, SlimefunItems.ENHANCED_FURNACE_7);
        register("high_tier_enhanced_furnaces", 134, "Fornalha de Alto Nível", 29, SlimefunItems.ENHANCED_FURNACE_8, SlimefunItems.ENHANCED_FURNACE_9, SlimefunItems.ENHANCED_FURNACE_10, SlimefunItems.ENHANCED_FURNACE_11);
        register("reinforced_furnace", 135, "Fornalha Reforçada", 32, SlimefunItems.REINFORCED_FURNACE);
        register("carbonado_furnace", 136, "Fornalha com Borda de Carbonado", 35, SlimefunItems.CARBONADO_EDGED_FURNACE);
        register("electric_motor", 137, "Aquecendo", 32, SlimefunItems.ELECTRO_MAGNET, SlimefunItems.ELECTRIC_MOTOR, SlimefunItems.HEATING_COIL);
        register("block_placer", 138, "Colocador de Blocos", 17, SlimefunItems.BLOCK_PLACER);
//        register("scroll_of_dimensional_teleposition", 142, "Virando as Coisas", 38, SlimefunItems.SCROLL_OF_DIMENSIONAL_TELEPOSITION);
        register("special_bows", 143, "Robin Hood", 22, SlimefunItems.EXPLOSIVE_BOW, SlimefunItems.ICY_BOW);
//        register("tome_of_knowledge_sharing", 144, "Compartilhando com Amigos", 30, SlimefunItems.TOME_OF_KNOWLEDGE_SHARING);
        register("flask_of_knowledge", 145, "Armazenamento de XP", 13, SlimefunItems.FLASK_OF_KNOWLEDGE);
        register("hardened_glass", 146, "Resistindo a Explosões", 15, SlimefunItems.REINFORCED_PLATE, SlimefunItems.HARDENED_GLASS);
        register("golden_apple_juice", 149, "Poção Dourada", 24, SlimefunItems.GOLDEN_APPLE_JUICE);
        register("cooler", 150, "Bebidas Portáteis", 24, SlimefunItems.COOLING_UNIT, SlimefunItems.COOLER);
        register("ancient_altar", 151, "Altar Antigo", 15, SlimefunItems.ANCIENT_PEDESTAL, SlimefunItems.ANCIENT_ALTAR);
        register("wither_proof_obsidian", 152, "Obsidiana à Prova de Wither", 21, SlimefunItems.WITHER_PROOF_OBSIDIAN);
        register("ancient_runes", 155, "Runas Elementais", 15, SlimefunItems.BLANK_RUNE, SlimefunItems.EARTH_RUNE, SlimefunItems.WATER_RUNE, SlimefunItems.AIR_RUNE, SlimefunItems.FIRE_RUNE);
        register("special_runes", 156, "Runas Roxas", 18, SlimefunItems.ENDER_RUNE, SlimefunItems.RAINBOW_RUNE);
        register("infernal_bonemeal", 157, "Farinha de Osso Infernal", 12, SlimefunItems.INFERNAL_BONEMEAL);
        register("rainbow_blocks", 158, "Blocos Arco-Íris", 24, SlimefunItems.RAINBOW_CLAY, SlimefunItems.RAINBOW_GLASS, SlimefunItems.RAINBOW_GLASS_PANE, SlimefunItems.RAINBOW_WOOL, SlimefunItems.RAINBOW_CONCRETE, SlimefunItems.RAINBOW_GLAZED_TERRACOTTA);
        register("infused_hopper", 159, "Funil Infundido", 22, SlimefunItems.INFUSED_HOPPER);
        register("wither_proof_glass", 160, "Vidro à Prova de Wither", 20, SlimefunItems.WITHER_PROOF_GLASS);
        register("duct_tape", 161, "Fita Adesiva", 14, SlimefunItems.DUCT_TAPE);
        register("plastic_sheet", 162, "Plástico", 25, SlimefunItems.PLASTIC_SHEET);
        register("android_memory_core", 163, "Núcleo de Memória", 28, SlimefunItems.ANDROID_MEMORY_CORE);
        register("oil", 164, "Petróleo", 30, SlimefunItems.OIL_BUCKET, SlimefunItems.OIL_PUMP);
        register("fuel", 165, "Combustível", 30, SlimefunItems.FUEL_BUCKET, SlimefunItems.REFINERY);
        register("hologram_projector", 166, "Hologramas", 36, SlimefunItems.HOLOGRAM_PROJECTOR);
        register("capacitors", 167, "Capacitores Nível 1", 25, SlimefunItems.SMALL_CAPACITOR, SlimefunItems.MEDIUM_CAPACITOR, SlimefunItems.BIG_CAPACITOR);
        register("high_tier_capacitors", 168, "Capacitores Nível 2", 32, SlimefunItems.LARGE_CAPACITOR, SlimefunItems.CARBONADO_EDGED_CAPACITOR);
        register("solar_generators", 169, "Usina Solar", 14, SlimefunItems.SOLAR_GENERATOR);
        register("electric_furnaces", 170, "Fornalha Elétrica", 15, SlimefunItems.ELECTRIC_FURNACE);
        register("electric_ore_grinding", 171, "Trituração e Moagem", 20, SlimefunItems.ELECTRIC_ORE_GRINDER, SlimefunItems.ELECTRIC_INGOT_PULVERIZER);
        register("heated_pressure_chamber", 172, "Câmara de Pressão Aquecida", 22, SlimefunItems.HEATED_PRESSURE_CHAMBER);
        register("coal_generator", 173, "Gerador de Carvão", 14, SlimefunItems.COAL_GENERATOR);
        register("bio_reactor", 173, "Biorreator", 18, SlimefunItems.BIO_REACTOR);
        register("auto_enchanting", 174, "Encantamento e Desencantamento Automáticos", 24, SlimefunItems.AUTO_ENCHANTER, SlimefunItems.AUTO_DISENCHANTER);
        register("auto_anvil", 175, "Bigorna Automática", 34, SlimefunItems.AUTO_ANVIL, SlimefunItems.AUTO_ANVIL_2);
        register("multimeter", 176, "Medição de Energia", 10, SlimefunItems.MULTIMETER);
        register("gps_setup", 177, "Configuração GPS Básica", 28, SlimefunItems.GPS_TRANSMITTER, SlimefunItems.GPS_CONTROL_PANEL, SlimefunItems.GPS_MARKER_TOOL);
        register("gps_emergency_transmitter", 178, "Ponto de Referência de Emergência GPS", 30, SlimefunItems.GPS_EMERGENCY_TRANSMITTER);
        register("programmable_androids", 179, "Androides Programáveis", 50, SlimefunItems.PROGRAMMABLE_ANDROID, SlimefunItems.PROGRAMMABLE_ANDROID_FARMER, SlimefunItems.PROGRAMMABLE_ANDROID_BUTCHER, SlimefunItems.PROGRAMMABLE_ANDROID_FISHERMAN, SlimefunItems.PROGRAMMABLE_ANDROID_MINER, SlimefunItems.PROGRAMMABLE_ANDROID_WOODCUTTER);
        register("android_interfaces", 180, "Interfaces de Androide", 26, SlimefunItems.ANDROID_INTERFACE_FUEL, SlimefunItems.ANDROID_INTERFACE_ITEMS);
        register("geo_scanner", 181, "Geo-Scanners", 30, SlimefunItems.GPS_GEO_SCANNER, SlimefunItems.PORTABLE_GEO_SCANNER);
        register("combustion_reactor", 182, "Reator de Combustão", 38, SlimefunItems.COMBUSTION_REACTOR);
        register("teleporter", 183, "Componentes Básicos do Teletransportador", 42, SlimefunItems.GPS_TELEPORTATION_MATRIX, SlimefunItems.GPS_TELEPORTER_PYLON);
        register("teleporter_activation_plates", 184, "Ativação do Teletransportador", 36, SlimefunItems.GPS_ACTIVATION_DEVICE_PERSONAL, SlimefunItems.GPS_ACTIVATION_DEVICE_SHARED);
        register("better_solar_generators", 185, "Geradores Solares Aprimorados", 28, SlimefunItems.SOLAR_GENERATOR_2, SlimefunItems.SOLAR_GENERATOR_3);
        register("better_gps_transmitters", 186, "Transmissores Aprimorados", 36, SlimefunItems.GPS_TRANSMITTER_2, SlimefunItems.GPS_TRANSMITTER_3);
        register("elevator", 187, "Elevadores", 28, SlimefunItems.ELEVATOR_PLATE);
        register("energized_solar_generator", 188, "Energia Solar Contínua", 44, SlimefunItems.SOLAR_GENERATOR_4);
        register("energized_gps_transmitter", 189, "Transmissor de Nível Superior", 44, SlimefunItems.GPS_TRANSMITTER_4);
        register("energy_regulator", 190, "Redes de Energia 101", 6, SlimefunItems.ENERGY_REGULATOR);
        register("butcher_androids", 191, "Androides Açougueiros", 32, SlimefunItems.PROGRAMMABLE_ANDROID_BUTCHER);
        register("organic_food", 192, "Alimentos Orgânicos", 25, SlimefunItems.FOOD_FABRICATOR, SlimefunItems.WHEAT_ORGANIC_FOOD, SlimefunItems.CARROT_ORGANIC_FOOD, SlimefunItems.POTATO_ORGANIC_FOOD, SlimefunItems.SEEDS_ORGANIC_FOOD, SlimefunItems.BEETROOT_ORGANIC_FOOD, SlimefunItems.MELON_ORGANIC_FOOD, SlimefunItems.APPLE_ORGANIC_FOOD, SlimefunItems.SWEET_BERRIES_ORGANIC_FOOD, SlimefunItems.KELP_ORGANIC_FOOD, SlimefunItems.COCOA_ORGANIC_FOOD, SlimefunItems.SEAGRASS_ORGANIC_FOOD);
        register("auto_breeder", 193, "Alimentação Automatizada", 25, SlimefunItems.AUTO_BREEDER);
        register("advanced_android", 194, "Androides Avançados", 60, SlimefunItems.PROGRAMMABLE_ANDROID_2);
        register("advanced_butcher_android", 195, "Androides Avançados - Açougueiro", 30, SlimefunItems.PROGRAMMABLE_ANDROID_2_BUTCHER);
        register("advanced_fisherman_android", 196, "Androides Avançados - Pescador", 30, SlimefunItems.PROGRAMMABLE_ANDROID_2_FISHERMAN);
        register("animal_growth_accelerator", 197, "Manipulação do Crescimento Animal", 32, SlimefunItems.ANIMAL_GROWTH_ACCELERATOR);
        register("xp_collector", 198, "Coletor de XP", 36, SlimefunItems.EXP_COLLECTOR);
        register("organic_fertilizer", 199, "Fertilizante Orgânico", 36, SlimefunItems.FOOD_COMPOSTER, SlimefunItems.WHEAT_FERTILIZER, SlimefunItems.CARROT_FERTILIZER, SlimefunItems.POTATO_FERTILIZER, SlimefunItems.SEEDS_FERTILIZER, SlimefunItems.BEETROOT_FERTILIZER, SlimefunItems.MELON_FERTILIZER, SlimefunItems.APPLE_FERTILIZER, SlimefunItems.SWEET_BERRIES_FERTILIZER, SlimefunItems.KELP_FERTILIZER, SlimefunItems.COCOA_FERTILIZER, SlimefunItems.SEAGRASS_FERTILIZER);
        register("crop_growth_accelerator", 200, "Aceleração do Crescimento de Culturas", 40, SlimefunItems.CROP_GROWTH_ACCELERATOR);
        register("better_crop_growth_accelerator", 201, "Acelerador de Crescimento de Culturas Aprimorado", 44, SlimefunItems.CROP_GROWTH_ACCELERATOR_2);
        register("reactor_essentials", 202, "Essenciais do Reator", 36, SlimefunItems.REACTOR_COOLANT_CELL, SlimefunItems.NEPTUNIUM, SlimefunItems.PLUTONIUM);
        register("nuclear_reactor", 203, "Usina Nuclear", 48, SlimefunItems.NUCLEAR_REACTOR);
        register("freezer", 204, "Sr. Congelador", 20, SlimefunItems.FREEZER);
        register("cargo_basics", 205, "Básico de Carga", 30, SlimefunItems.CARGO_MOTOR, SlimefunItems.CARGO_MANAGER, SlimefunItems.CARGO_CONNECTOR_NODE);
        register("cargo_nodes", 206, "Configuração de Carga", 30, SlimefunItems.CARGO_INPUT_NODE, SlimefunItems.CARGO_OUTPUT_NODE);
        register("electric_ingot_machines", 207, "Fabricação Elétrica de Lingotes", 18, SlimefunItems.ELECTRIC_GOLD_PAN, SlimefunItems.ELECTRIC_DUST_WASHER, SlimefunItems.ELECTRIC_INGOT_FACTORY);
        register("medium_tier_electric_ingot_machines", 208, "Fabricação Rápida de Lingotes", 25, SlimefunItems.ELECTRIC_GOLD_PAN_2, SlimefunItems.ELECTRIC_DUST_WASHER_2, SlimefunItems.ELECTRIC_INGOT_FACTORY_2, SlimefunItems.ELECTRIC_ORE_GRINDER_2);
        register("high_tier_electric_ingot_machines", 209, "Fabricação Super Rápida de Lingotes", 32, SlimefunItems.ELECTRIC_GOLD_PAN_3, SlimefunItems.ELECTRIC_DUST_WASHER_3, SlimefunItems.ELECTRIC_INGOT_FACTORY_3, SlimefunItems.ELECTRIC_ORE_GRINDER_3);
        register("better_food_fabricator", 211, "Fabricador de Alimentos Aprimorado", 28, SlimefunItems.FOOD_FABRICATOR_2, SlimefunItems.FOOD_COMPOSTER_2);
        register("reactor_access_port", 212, "Porta de Acesso ao Reator", 18, SlimefunItems.REACTOR_ACCESS_PORT);
        register("fluid_pump", 213, "Bomba de Fluido", 28, SlimefunItems.FLUID_PUMP);
        register("better_freezer", 214, "Congelador Aprimorado", 29, SlimefunItems.FREEZER_2, SlimefunItems.FREEZER_3);
        register("boosted_uranium", 215, "Círculo Sem Fim", 30, SlimefunItems.BOOSTED_URANIUM);
        register("trash_can", 216, "Lixeira", 8, SlimefunItems.TRASH_CAN);
        register("advanced_output_node", 217, "Nó de Saída Avançado", 24, SlimefunItems.CARGO_OUTPUT_NODE_2);
        register("carbon_press", 218, "Prensa de Carbono", 28, SlimefunItems.CARBON_PRESS);
        register("electric_smeltery", 219, "Fundição Elétrica", 28, SlimefunItems.ELECTRIC_SMELTERY);
        register("better_electric_furnace", 220, "Fornalha Elétrica Aprimorada", 20, SlimefunItems.ELECTRIC_FURNACE_2, SlimefunItems.ELECTRIC_FURNACE_3);
        register("better_carbon_press", 221, "Prensa de Carbono Aprimorada", 26, SlimefunItems.CARBON_PRESS_2);
        register("empowered_android", 222, "Androides Potencializados", 60, SlimefunItems.PROGRAMMABLE_ANDROID_3);
        register("empowered_butcher_android", 223, "Androides Potencializados - Açougueiro", 30, SlimefunItems.PROGRAMMABLE_ANDROID_3_BUTCHER);
        register("empowered_fisherman_android", 224, "Androides Potencializados - Pescador", 30, SlimefunItems.PROGRAMMABLE_ANDROID_3_FISHERMAN);
        register("high_tier_carbon_press", 225, "Prensa de Carbono Suprema", 32, SlimefunItems.CARBON_PRESS_3);
        register("wither_assembler", 226, "Assassino de Wither Automatizado", 35, SlimefunItems.WITHER_ASSEMBLER);
        register("better_heated_pressure_chamber", 227, "Câmara de Pressão Aquecida Aprimorada", 20, SlimefunItems.HEATED_PRESSURE_CHAMBER_2);
        register("elytra", 228, "Elytras", 20, "ELYTRA_SCALE", "ELYTRA");
        register("special_elytras", 229, "Elytras Especiais", 30, SlimefunItems.INFUSED_ELYTRA, SlimefunItems.SOULBOUND_ELYTRA);
        register("electric_crucible", 230, "Crisol Eletrificado", 26, SlimefunItems.ELECTRIFIED_CRUCIBLE);
        register("better_electric_crucibles", 231, "Crisóis Quentes", 30, SlimefunItems.ELECTRIFIED_CRUCIBLE_2, SlimefunItems.ELECTRIFIED_CRUCIBLE_3);
        register("advanced_electric_smeltery", 232, "Fundição Elétrica Avançada", 28, SlimefunItems.ELECTRIC_SMELTERY_2);
        register("advanced_farmer_android", 233, "Androides Avançados - Fazendeiro", 30, SlimefunItems.PROGRAMMABLE_ANDROID_2_FARMER);
        register("lava_generator", 234, "Gerador de Lava", 16, SlimefunItems.LAVA_GENERATOR);
        register("nether_ice", 235, "Refrigerante de Gelo do Nether", 45, SlimefunItems.NETHER_ICE, SlimefunItems.ENRICHED_NETHER_ICE, SlimefunItems.NETHER_ICE_COOLANT_CELL);
        register("nether_star_reactor", 236, "Reator de Estrela do Nether", 60, SlimefunItems.NETHER_STAR_REACTOR);
        register("blistering_ingots", 237, "Radioatividade Bolhosa", 38, SlimefunItems.BLISTERING_INGOT, SlimefunItems.BLISTERING_INGOT_2, SlimefunItems.BLISTERING_INGOT_3);
        register("automatic_ignition_chamber", 239, "Câmara de Ignição Automática", 12, SlimefunItems.IGNITION_CHAMBER);
        register("output_chest", 240, "Baú de Saída de Máquinas Básicas", 20, SlimefunItems.OUTPUT_CHEST);
        register("copper_wire", 241, "Condutividade Fina", 15, SlimefunItems.COPPER_WIRE);
        register("radiant_backpack", 242, "Mochila Radiante", 25, SlimefunItems.RADIANT_BACKPACK);
        register("auto_drier", 243, "Um Dia Seco", 15, SlimefunItems.AUTO_DRIER);
        register("diet_cookie", 244, "Biscoito Diet", 3, SlimefunItems.DIET_COOKIE);
        register("storm_staff", 245, "Cajado da Tempestade", 30, SlimefunItems.STAFF_STORM);
        register("soulbound_rune", 246, "Runa Vinculada à Alma", 60, SlimefunItems.SOULBOUND_RUNE);
        register("geo_miner", 247, "GEO-Minerador", 24, SlimefunItems.GEO_MINER);
        register("lightning_rune", 248, "Runa do Relâmpago", 24, SlimefunItems.LIGHTNING_RUNE);
        register("totem_of_undying", 249, "Totem da Imortalidade", 36, "TOTEM_OF_UNDYING");
        register("charging_bench", 250, "Bancada de Carregamento", 8, SlimefunItems.CHARGING_BENCH);
        register("nether_gold_pan", 251, "Peneira de Ouro do Nether", 8, SlimefunItems.NETHER_GOLD_PAN);
        register("electric_press", 252, "Prensa Elétrica", 16, SlimefunItems.ELECTRIC_PRESS, SlimefunItems.ELECTRIC_PRESS_2);
        register("magnesium_generator", 253, "Energia do Magnésio", 20, SlimefunItems.MAGNESIUM_SALT, SlimefunItems.MAGNESIUM_GENERATOR);
        register("kelp_cookie", 254, "Alga Saborosa", 4, SlimefunItems.KELP_COOKIE);
        register("makeshift_smeltery", 255, "Fundição Improvisada", 6, SlimefunItems.MAKESHIFT_SMELTERY);
        register("tree_growth_accelerator", 256, "Árvores Mais Rápidas", 18, SlimefunItems.TREE_GROWTH_ACCELERATOR);
        register("industrial_miner", 95, "Mineração Industrial", 28, SlimefunItems.INDUSTRIAL_MINER);
        register("advanced_industrial_miner", 98, "Melhor Mineração", 36, SlimefunItems.ADVANCED_INDUSTRIAL_MINER);
        register("magical_zombie_pills", 257, "Deszumbificação", 22, SlimefunItems.MAGICAL_ZOMBIE_PILLS);
        register("auto_brewer", 258, "Cervejaria Industrial", 30, SlimefunItems.AUTO_BREWER);
        register("enchantment_rune", 259, "Encantamento Antigo", 24, SlimefunItems.MAGICAL_GLASS, SlimefunItems.ENCHANTMENT_RUNE);
        register("lead_clothing", 260, "Roupa de Chumbo", 14, SlimefunItems.REINFORCED_CLOTH);
        register("tape_measure", 261, "Trena", 7, SlimefunItems.TAPE_MEASURE);
        register("iron_golem_assembler", 262, "Golems de Ferro Automatizados", 30, SlimefunItems.IRON_GOLEM_ASSEMBLER);
        register("shulker_shell", 263, "Shulkers Sintéticos", 30, SlimefunItems.SYNTHETIC_SHULKER_SHELL);
        register("villager_rune", 264, "Redefinir Trocas de Aldeões", 26, SlimefunItems.VILLAGER_RUNE, SlimefunItems.STRANGE_NETHER_GOO);
        register("climbing_pick", 265, "Saqueador de Blocos", 20, SlimefunItems.CLIMBING_PICK);
        register("even_higher_tier_capacitors", 266, "Capacitores Nível 3", 40, SlimefunItems.ENERGIZED_CAPACITOR);
        register("caveman_talisman", 267, "Talismã do Homem das Cavernas", 20, SlimefunItems.TALISMAN_CAVEMAN);
        register("elytra_cap", 268, "Equipamento de Colisão", 20, SlimefunItems.ELYTRA_CAP);
        register("energy_connectors", 269, "Conexões Com Fio", 12, SlimefunItems.ENERGY_CONNECTOR);
        register("bee_armor", 270, "Armadura de Abelha", 24, SlimefunItems.BEE_HELMET, SlimefunItems.BEE_WINGS, SlimefunItems.BEE_LEGGINGS, SlimefunItems.BEE_BOOTS);
        register("wise_talisman", 271, "Talismã do Sábio", 20, SlimefunItems.TALISMAN_WISE);
        register("book_binder", 272, "Encadernação de Livros de Encantamento", 26, SlimefunItems.BOOK_BINDER);
        register("auto_crafting", 273, "Criação Automática", 30, SlimefunItems.CRAFTING_MOTOR, SlimefunItems.VANILLA_AUTO_CRAFTER, SlimefunItems.ENHANCED_AUTO_CRAFTER, SlimefunItems.ARMOR_AUTO_CRAFTER);
        register("produce_collector", 274, "Ordenha Automática", 20, SlimefunItems.PRODUCE_COLLECTOR);
        register("improved_generators", 275, "Geradores Melhorados", 24, SlimefunItems.COAL_GENERATOR_2, SlimefunItems.LAVA_GENERATOR_2);
        register("ingredients_and_cheese", 276, "Culinária Slimefun", 5, SlimefunItems.SALT, SlimefunItems.WHEAT_FLOUR, SlimefunItems.HEAVY_CREAM, SlimefunItems.CHEESE, SlimefunItems.BUTTER);
        register("medium_tier_auto_enchanting", 277, "Encantamento e Desencantamento Automáticos Rápidos", 48, SlimefunItems.AUTO_ENCHANTER_2, SlimefunItems.AUTO_DISENCHANTER_2);
        register("portable_teleporter", 278, "Teletransporte de Qualquer Lugar", 42, SlimefunItems.PORTABLE_TELEPORTER);
        register("trident", 279, "Tridente", 20, "TRIDENT");
        register("farmer_talisman", 280, "Talismã do Fazendeiro", 18, SlimefunItems.TALISMAN_FARMER);
        register("rainbow_armor", 281, "Eu Quero Ver o Arco-Íris no Céu", 22, SlimefunItems.RAINBOW_HELMET, SlimefunItems.RAINBOW_CHESTPLATE, SlimefunItems.RAINBOW_LEGGINGS, SlimefunItems.RAINBOW_BOOTS);
    }

    private static void register(String key, int id, String name, int defaultCost, SlimefunItemStack... items) {
        ItemStack[] array = Arrays.stream(items).map(SlimefunItemStack::item).toArray(ItemStack[]::new);
        register(key, id, name, defaultCost, array);
    }

    @ParametersAreNonnullByDefault
    private static void register(String key, int id, String name, int defaultCost, ItemStack... items) {
        Research research = new Research(new NamespacedKey(Slimefun.instance(), key), id, name, defaultCost);

        for (ItemStack item : items) {
            SlimefunItem sfItem = SlimefunItem.getByItem(item);

            if (sfItem != null) {
                research.addItems(sfItem);
            }
        }

        research.register();
    }

    @ParametersAreNonnullByDefault
    private static void register(String key, int id, String name, int defaultCost, String... items) {
        Research research = new Research(new NamespacedKey(Slimefun.instance(), key), id, name, defaultCost);

        for (String itemId : items) {
            SlimefunItem sfItem = SlimefunItem.getById(itemId);

            if (sfItem != null) {
                research.addItems(sfItem);
            }
        }

        research.register();
    }
}
