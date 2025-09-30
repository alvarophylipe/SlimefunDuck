package io.github.ducklin.migration

import com.google.common.collect.ImmutableMap
import com.google.gson.JsonParser
import com.google.gson.stream.JsonWriter
import io.github.bakedlibs.dough.blocks.BlockPosition
import io.github.bakedlibs.dough.common.CommonPatterns
import io.github.ducklin.core.Configuration.Config
import io.github.ducklin.core.Slimefun
import io.github.ducklin.core.Slimefun.Companion.cfg
import io.github.ducklin.menu.BlockMenu
import io.github.ducklin.menu.BlockMenuPreset
import io.github.ducklin.menu.UniversalBlockMenu
import io.github.ducklin.migration.items.SlimefunItem
import io.github.ducklin.migration.utils.NumberUtils
import org.bukkit.Bukkit
import org.bukkit.Chunk
import org.bukkit.Location
import org.bukkit.World
import org.bukkit.block.Block
import org.bukkit.configuration.file.YamlConfiguration
import org.bukkit.inventory.ItemStack
import java.io.File
import java.io.IOException
import java.io.StringWriter
import java.nio.file.Files
import java.nio.file.StandardCopyOption
import java.util.concurrent.ConcurrentHashMap
import java.util.logging.Level
import kotlin.text.contains
import kotlin.text.get

class BlockStorage(private val world: World) {

    private val storage = ConcurrentHashMap<Location, Config>()
    private val inventories = ConcurrentHashMap<Location, BlockMenu>()
    private val blocksCache = ConcurrentHashMap<String, Config>()

    var changes: Int = 0
        private set
    var isMarkedForRemoval = false

    init {
        require("." !in world.name) { "Slimefun cannot deal with World names that contain a dot: " + world.name }

        if (Slimefun.getRegistry().worlds.putIfAbsent(world.name, this) != null) {
            error("Attempted to create duplicate BlockStorage for world '${world.name}'")
        }

        Slimefun.logger()!!.log(Level.INFO, "Loading Blocks for World \"{0}\"", world.name)
        Slimefun.logger()!!.log(Level.INFO, "This may take a long time...")

        val dir = File(PATH_BLOCKS + world.name)

        if (dir.exists()) loadBlocks(dir) else dir.mkdirs()
        loadChunks()
        if (Slimefun.instance()!!.isUnitTest) loadInventories()
    }

    private fun loadBlocks(dir: File) {
        val files = dir.listFiles() ?: return
        val total = files.size.toLong()
        var done = 0L
        var totalBlocks = 0L
        val delay = cfg.getInt("URID.info-delay")
        val start = System.currentTimeMillis()
        var timestamp = System.currentTimeMillis()

        files.forEach { file ->
            when {
                file.name == "null.sfb" -> logCorruptedFile(file)
                file.name.endsWith(".sfb") -> {
                    if (System.currentTimeMillis() > timestamp + delay) {
                        val progress = ((done * 100.0) / total).toInt()
                        Slimefun.logger()!!.info("Loading Blocks... $progress% done (\"${world.name}\")")
                        timestamp = System.currentTimeMillis()
                    }
                    YamlConfiguration.loadConfiguration(file).also { cfg ->
                        cfg.getKeys(false).forEach { key ->
                            loadBlock(file, cfg, key)
                            totalBlocks++
                        }
                    }
                    done++
                }
            }
        }

        val time = System.currentTimeMillis() - start
        Slimefun.logger()!!.info("Loading Blocks... 100% (FINISHED - ${time}ms)")
        Slimefun.logger()!!.info("Loaded a total of $totalBlocks Blocks for World \"${world.name}\"")
        if (totalBlocks > 0) {
            Slimefun.logger()!!.info("Avg: ${NumberUtils.roundDecimalNumber(time.toDouble() / totalBlocks)}ms/Block")
        }
    }

    private fun logCorruptedFile(file: File) {
        Slimefun.logger()!!.warning("File with corrupted blocks detected!")
        Slimefun.logger()!!.warning("Slimefun will simply skip this File, you should look inside though!")
        Slimefun.logger()!!.warning(file.path)
    }

    private fun loadBlock(file: File, cfg: YamlConfiguration, key: String) {
        deserializeLocation(key)?.let { loc ->
            try {
                parseBlockInfo(loc, cfg.getString(key))?.takeIf { it.contains("id") }?.let { blockInfo ->
                    if (storage.putIfAbsent(loc, blockInfo) != null) {
                        if (Slimefun.getRegistry().logDuplicateBlockEntries()) {
                            Slimefun.logger()!!.info(
                                "Ignoring duplicate block @ ${loc.blockX}, ${loc.blockY}, ${loc.blockZ} " +
                                        "(${blockInfo.getString("id")} -> ${storage[loc]?.getString("id")})"
                            )
                        }
                        return
                    }

                    val fileName = file.nameWithoutExtension
                    if (Slimefun.getRegistry().tickerBlocks.contains(fileName)) {
                        Slimefun. tickerTask.enableTicker(loc)
                    }
                }
            } catch (x: Exception) {
                Slimefun.logger()!!.log(Level.WARNING, x) {
                    "Failed to load ${file.name}($key) for Slimefun ${Slimefun.version}"
                }
            }
        }
    }

    private fun loadChunks() {
        val chunks = File(PATH_CHUNKS + "chunks.sfc")
        if (!chunks.exists()) return

        try {
            val cfg = YamlConfiguration.loadConfiguration(chunks)
            cfg.getKeys(false)
                .filter { CommonPatterns.SEMICOLON.split(it).getOrNull(0) == world.name }
                .forEach { key ->
                    cfg.getString(key)?.let { json ->
                        Slimefun.getRegistry().chunks[key] = BlockInfoConfig(parseJSON(json))
                    }
                }

        } catch (x: Exception) {
            Slimefun.logger()!!.log(Level.WARNING, x) {
                "Failed to load ${chunks.name} in World ${world.name} for Slimefun ${Slimefun.version}"
            }
        }
    }

    private fun loadInventories() {
        File(PATH_INVENTORIES).listFiles()?.forEach { file ->
            if (!file.name.startsWith(world.name) || !file.name.endsWith(".sfi")) return@forEach
            deserializeLocation(file.nameWithoutExtension)?.takeIf { it.world == world }?.let { loc ->
                try {
                    val cfg = io.github.bakedlibs.dough.config.Config(file)
                    val preset = BlockMenuPreset.getPreset(cfg.getString("preset"))
                        ?: BlockMenuPreset.getPreset(checkID(loc)) ?: return@forEach
                    inventories[loc] = BlockMenu(preset, loc, cfg)
                } catch (x: Exception) {
                    Slimefun.logger()!!.log(Level.SEVERE, x) { "Error loading inventory: ${file.name}" }
                }
            }
        }

        if (universalInventoriesLoaded) return
        universalInventoriesLoaded = true

        File("data-storage/Slimefun/universal-inventories").listFiles()?.forEach { file ->
            if (!file.name.endsWith(".sfi")) return@forEach
            try {
                val cfg = Config(file)
                BlockMenuPreset.getPreset(cfg.getString("preset"))?.let { preset ->
                    Slimefun.getRegistry().universalInventories[preset.id] = UniversalBlockMenu(preset, cfg)
                }
            } catch (x: Exception) {
                Slimefun.logger()!!.log(Level.SEVERE, x) { "Error loading universal inventory: ${file.name}" }
            }
        }
    }

    fun computeChanges() {
        changes = blocksCache.size + inventories.values.sumOf { it.unsavedChanges } +
                Slimefun.getRegistry().universalInventories.values.sumOf { it.unsavedChanges }
    }

    fun save() {
        computeChanges()
        if (changes == 0) return

        Slimefun.logger()!!.info("Saving block data for world \"${world.name}\" ($changes change(s) queued)")

        val cache = blocksCache.toMutableMap()

        cache.values.forEach { cfg ->
            when {
                cfg.getKeys()!!.isEmpty() -> cfg.getFile()?.takeIf { it.exists() }?.delete()
                else -> {
                    val tmp = File(cfg.getFile()?.parentFile, "${cfg.getFile()?.name}.tmp")
                    cfg.save(tmp)

                    runCatching { Files.move(tmp.toPath(), cfg.getFile()?.toPath()!!, StandardCopyOption.ATOMIC_MOVE) }
                        .onFailure {
                            Slimefun.logger()!!.severe("Error copying temp file for Slimefun ${Slimefun.version}")
                        }
                }
            }
        }

        inventories.forEach { (loc, menu) -> menu.save(loc) }
        Slimefun.getRegistry().universalInventories.values.forEach { it.save() }
        changes = 0
    }

    fun saveAndRemove() {
        save()
        saveChunks()
        this.isMarkedForRemoval = true
    }

    var rawStorage: MutableMap<Location, Config?> = ImmutableMap.copyOf(storage)

    fun loadInventory(l: Location, preset: BlockMenuPreset): BlockMenu = BlockMenu(preset, l).also { inventories[l] = it }

    fun reloadInventory(l: Location) = inventories[l]?.reload()

    fun clearInventory(l: Location) {
        val menu = inventories[l] ?: return
        menu.toInventory()?.viewers?.toList()?.forEach { Slimefun.runSync(it::closeInventory) }
        inventories.remove(l)?.delete(l)
    }

    fun hasInventory(l: Location): Boolean = inventories.contains(l)

    operator fun get(l: Location): Config? = storage[l]
    operator fun set(l: Location, cfg: Config) {
        storage[l] = cfg
    }

    companion object {
        private const val PATH_BLOCKS = "data-storage/Slimefun/stored-blocks/"
        private const val PATH_CHUNKS = "data-storage/Slimefun/stored-chunks/"
        private const val PATH_INVENTORIES = "data-storage/Slimefun/stored-inventories/"

        private val emptyBlockData = EmptyBlockData()
        private var chunkChanges = 0
        private var universalInventoriesLoaded = false

        fun getStorage(world: World): BlockStorage? = Slimefun.getRegistry().worlds[world.name]
        fun getOrCreate(world: World): BlockStorage = getStorage(world) ?: BlockStorage(world)

        private fun serializeLocation(l: Location): String =
            "${l.world.name};${l.blockX};${l.blockY};${l.blockZ}"

        private fun serializeChunk(world: World, x: Int, z: Int): String = "${world.name};Chunk;$x;$z"

        private fun deserializeLocation(l: String?): Location? =
            CommonPatterns.SEMICOLON.split(l).takeIf { it.size == 4 }?.let { parts ->
                Bukkit.getWorld(parts[0])?.let { w ->
                    runCatching { Location(w,
                        parts[1].toInt().toDouble(),
                        parts[2].toInt().toDouble(),
                        parts[3].toInt().toDouble()) }.getOrNull()
                }
            } ?: run {
                Slimefun.logger()!!.warning("Could not parse Number from: $l")
                null
            }

        @JvmStatic
        fun saveChunks() {

            if (chunkChanges == 0) return
            val chunksFile = File("${PATH_CHUNKS}chunks.sfc")
            val cfg = Config("${PATH_CHUNKS}chunks.temp")
            Slimefun.getRegistry().chunks
                .filter { it.value.getKeys()!!.isNotEmpty() }
                .forEach { (k, v) -> cfg.setValue(k, v.toJSON()) }
            cfg.save(chunksFile)
            chunkChanges = 0
        }

        @JvmStatic
        fun getRawStorage(world: World): MutableMap<Location, Config?>? = getStorage(world)?.rawStorage

        @JvmStatic
        fun store(block: Block, item: ItemStack) = SlimefunItem.getByItem(item)?.let { addBlockInfo(block, "id", it.id, true) }

        @JvmStatic
        fun store(block: Block, item: String) = addBlockInfo(block, "id", item, true)

        @JvmStatic
        fun retrieve(block: Block): ItemStack? = check(block)?.also { clearBlockInfo(block) }?.item

        @JvmStatic
        fun getLocationInfo(l: Location): Config = getStorage(l.world)?.storage?.get(l) ?: emptyBlockData

        @JvmStatic
        fun getLocationInfo(l: Location, key: String): String? = getLocationInfo(l).getString(key)

        @JvmStatic
        fun getLocationInfo(l: BlockPosition, key: String): String? = getLocationInfo(l.toLocation()).getString(key)

        @JvmStatic
        fun addBlockInfo(l: Location, key: String, value: String, updateTicker: Boolean = false) {
            val cfg = getLocationInfo(l).takeUnless { it === emptyBlockData } ?: BlockInfoConfig()
            cfg.setValue(key, value)
            setBlockInfo(l, cfg, updateTicker)
        }

         @JvmStatic
        fun addBlockInfo(block: Block, key: String, value: String, updateTicker: Boolean = false) =
            addBlockInfo(block.location, key, value, updateTicker)

        @JvmStatic
        fun addBlockInfo(l: BlockPosition, key: String, value: String, updateTicker: Boolean = false) =
            addBlockInfo(l.toLocation(), key, value, updateTicker)

        @JvmStatic
        fun addBlockInfo(block: Block, key: String, value: String) =
            addBlockInfo(block.location, key, value, false)

        @JvmStatic
        fun hasBlockInfo(l: Location): Boolean = getStorage(l.world)?.storage?.get(l)?.getString("id") != null

        @JvmStatic
        fun hasBlockInfo(block: Block): Boolean = hasBlockInfo(block.location)

        @JvmStatic
        fun setBlockInfo(l: Location, cfg: Config, updateTicker: Boolean) {
            val storage = getStorage(l.world) ?: run {
                Slimefun.logger()!!.warning(
                    "Could not set Block info for non-registered World '${l.world.name}'. " +
                            "Is some plugin trying to store data in a fake world?"
                )
                return
            }

            val id = cfg.getString("id") ?: return
            val preset = BlockMenuPreset.getPreset(id) ?: return

            when {
                BlockMenuPreset.isUniversalInventory(id) -> {
                    Slimefun.getRegistry().universalInventories.getOrPut(id) { UniversalBlockMenu(preset) }
                }
                !storage.hasInventory(l) -> {
                    val file = File("${PATH_INVENTORIES}${serializeLocation(l)}.sfi")
                    val menu = if (file.exists()) {
                        BlockMenu(preset, l, io.github.bakedlibs.dough.config.Config(file))
                    } else {
                        storage.loadInventory(l, preset)
                    }
                    storage.inventories[l] = menu
                }
            }

            refreshCache(storage, l, id, serializeBlockInfo(cfg), updateTicker)
        }

        @JvmStatic
        fun clearBlockInfo(l: Location, destroy: Boolean = true) = Slimefun.tickerTask.queueDelete(l, destroy)

        @JvmStatic
        fun clearBlockInfo(b: Block, destroy: Boolean = true) = clearBlockInfo(b.location, destroy)

        @JvmStatic
        fun clearAllBlockInfoAtChunk(world: World, chunkX: Int, chunkZ: Int, destroy: Boolean) {
            val blockStorage = getStorage(world) ?: return
            val toClear = blockStorage.storage.keys
                .filter { it.blockX shr 4 == chunkX && it.blockZ shr 4 == chunkZ }
                .associateWith { destroy }
            Slimefun.tickerTask.queueDelete(toClear)
        }

        @JvmStatic
        fun clearAllBlockInfoAtChunk(chunk: Chunk, destroy: Boolean) {
            clearAllBlockInfoAtChunk(chunk.world, chunk.x, chunk.z, destroy)
        }

        @JvmStatic
        fun deleteLocationInfoUnsafely(l: Location, destroy: Boolean) {
            val storage = getStorage(l.getWorld())
                ?: error("World \"${l.world.name}\" seems to have been deleted. Do not call unsafe methods directly!")

            if (!hasBlockInfo(l)) return

            val id = getLocationInfo(l).getString("id")

            refreshCache(storage, l, id, null, destroy)
            storage.storage -= l

            if (destroy) {
                if (storage.hasInventory(l)) storage.clearInventory(l)
                getUniversalInventory(l)?.let {
                    it.close()
                    it.save()
                }
                Slimefun.tickerTask.disableTicker(l)
            }
        }

        @JvmStatic
        fun moveBlockInfo(from: Location, to: Location) = Slimefun.tickerTask.queueMove(from, to)

        @JvmStatic
        fun moveLocationInfoUnsafely(from: Location, to: Location) {
            if (!hasBlockInfo(from)) return

            val storage = getStorage(from.getWorld()) ?: return
            val previousData: Config = getLocationInfo(from)
            setBlockInfo(to, previousData, true)

            storage.inventories[from]?. let { menu ->
                storage.inventories[to] = menu
                storage.clearInventory(from)
                menu.move(to)
            }

            refreshCache(storage, from, previousData.getString("id"), null, true)
            storage.storage -= from
            Slimefun.tickerTask.disableTicker(from)
        }

        private fun refreshCache(
            storage: BlockStorage,
            loc: Location,
            key: String?,
            value: String?,
            updateTicker: Boolean
        ) {
            if (key == null) return

            val cfg = storage.blocksCache.getOrPut(key) {
                Config("${PATH_BLOCKS}${loc.world.name}/$key.sfb")
            }
            cfg.setValue(serializeLocation(loc), value)

            if (updateTicker) {
                val item = SlimefunItem.getById(key) ?: return
                if (value != null &&
                    loc.world != null &&
                    item.isTicking &&
                    !item.isDisabledIn(loc.world)
                ) {
                    Slimefun.tickerTask.enableTicker(loc)
                }
            }
        }

        private fun parseJSON(json: String?): MutableMap<String?, String?> {
            val map: MutableMap<String?, String?> = HashMap()

            if (json != null && json.length > 2) {
                val parser = JsonParser()
                val obj = parser.parse(json).getAsJsonObject()

                for (entry in obj.entrySet()) {
                    map[entry.key] = entry.value.asString
                }
            }

            return map
        }

        private fun parseBlockInfo(l: Location, json: String?): BlockInfoConfig? {
            try {
                return BlockInfoConfig(parseJSON(json))
            } catch (x: Exception) {
                val logger = Slimefun.logger()
                logger!!.log(Level.WARNING, x.javaClass.getName())
                logger.log(
                    Level.WARNING,
                    "Failed to parse BlockInfo for Block @ {0}, {1}, {2}",
                    arrayOf<Any>(l.blockX, l.blockY, l.blockZ)
                )
                logger.log(Level.WARNING, json)
                logger.log(Level.WARNING, "")
                logger.log(Level.WARNING, "IGNORE THIS ERROR UNLESS IT IS SPAMMING")
                logger.log(Level.WARNING, "")
                logger.log(
                    Level.SEVERE,
                    x
                ) { "An Error occurred while parsing Block Info for Slimefun ${Slimefun.version}" }
                return null
            }
        }

        private fun serializeBlockInfo(cfg: Config): String? {
            val string = StringWriter()

            try {
                JsonWriter(string).use { writer ->
                    writer.isLenient = true
                    writer.beginObject()

                    for (key in cfg.getKeys()!!) {
                        writer.name(key).value(cfg.getString(key))
                    }

                    writer.endObject()
                    return string.toString()
                }
            } catch (x: IOException) {
                Slimefun.logger()!!.log(Level.SEVERE, "An error occurred while serializing BlockInfo", x)
                return null
            }
        }



        @JvmStatic
        fun check(b: Block): SlimefunItem? {
            val id: String? = checkID(b)
            return if (id == null) null else SlimefunItem.getById(id)
        }

        @JvmStatic
        fun check(l: Location): SlimefunItem? {
            val id: String? = checkID(l)
            return if (id == null) null else SlimefunItem.getById(id)
        }

        @JvmStatic
        fun check(block: Block, slimefunItem: String?): Boolean {
            val id: String? = checkID(block)
            return id != null && id == slimefunItem
        }

        @JvmStatic
        fun checkID(b: Block): String? {
            // Only access the BlockState when on the main thread
            if (Bukkit.isPrimaryThread() && Slimefun.getBlockDataService().isTileEntity(b.getType())) {
                val blockData = Slimefun.getBlockDataService().getBlockData(b)

                if (blockData.isPresent()) {
                    return blockData.get()
                }
            }

            return checkID(b.getLocation())
        }

        @JvmStatic
        fun checkID(l: Location): String? {
            return getLocationInfo(l, "id")
        }

        @JvmStatic
        fun check(l: Location, slimefunItem: String?): Boolean {
            if (slimefunItem == null) {
                return false
            }

            val id: String? = checkID(l)
            return id != null && id == slimefunItem
        }

        @JvmStatic
        fun isWorldLoaded(world: World): Boolean {
            return Slimefun.getRegistry().getWorlds().containsKey(world.getName())
        }

        @JvmStatic
        fun hasUniversalInventory(id: String?): Boolean {
            return Slimefun.getRegistry().getUniversalInventories().containsKey(id)
        }

        @JvmStatic
        fun getUniversalInventory(block: Block): UniversalBlockMenu? {
            return getUniversalInventory(block.getLocation())
        }

        @JvmStatic
        fun getUniversalInventory(l: Location): UniversalBlockMenu? {
            val id: String? = checkID(l)
            return if (id == null) null else getUniversalInventory(id)
        }

        @JvmStatic
        fun getUniversalInventory(id: String?): UniversalBlockMenu? {
            return Slimefun.getRegistry().getUniversalInventories().get(id)
        }

        @JvmStatic
        fun getInventory(b: Block): BlockMenu? {
            return getInventory(b.getLocation())
        }

        @JvmStatic
        fun hasInventory(b: Block): Boolean {
            val storage: BlockStorage? = getStorage(b.getWorld())

            if (storage == null) {
                return false
            } else {
                return storage.hasInventory(b.getLocation())
            }
        }

        @JvmStatic
        fun getInventory(l: Location): BlockMenu? {
            val storage: BlockStorage? = getStorage(l.getWorld())

            if (storage == null) {
                return null
            }

            val menu = storage.inventories.get(l)

            if (menu != null) {
                return menu
            } else {
                return storage.loadInventory(l, BlockMenuPreset.getPreset(checkID(l))!!)
            }
        }

        @JvmStatic
        fun getChunkInfo(world: World, x: Int, z: Int): Config {
            try {
                if (!isWorldLoaded(world)) {
                    return emptyBlockData
                }

                val key: String = serializeChunk(world, x, z)
                var cfg = Slimefun.getRegistry().chunks.get(key)

                if (cfg == null) {
                    cfg = BlockInfoConfig()
                    Slimefun.getRegistry().chunks.put(key, cfg)
                }

                return cfg
            } catch (e: Exception) {
                Slimefun.logger()!!.log(Level.SEVERE, e) { "Failed to parse ChunkInfo for Slimefun " + Slimefun.version }
                return emptyBlockData
            }
        }

        @JvmStatic
        fun setChunkInfo(world: World, x: Int, z: Int, key: String?, value: String?) {
            val serializedChunk: String = serializeChunk(world, x, z)
            var cfg = Slimefun.getRegistry().chunks.get(serializedChunk)

            if (cfg == null) {
                cfg = BlockInfoConfig()
                Slimefun.getRegistry().chunks.put(serializedChunk, cfg)
            }

            cfg.setValue(key!!, value)

            chunkChanges++
        }

        @JvmStatic
        fun hasChunkInfo(world: World, x: Int, z: Int): Boolean {
            val serializedChunk: String = serializeChunk(world, x, z)
            return Slimefun.getRegistry().chunks.containsKey(serializedChunk)
        }

        @JvmStatic
        fun getChunkInfo(world: World, x: Int, z: Int, key: String): String? {
            return getChunkInfo(world, x, z)!!.getString(key)
        }

        @JvmStatic
        fun getBlockInfoAsJson(block: Block): String? {
            return getBlockInfoAsJson(block.getLocation())
        }

        @JvmStatic
        fun getBlockInfoAsJson(l: Location): String? {
            return serializeBlockInfo(getLocationInfo(l))
        }
    }
}