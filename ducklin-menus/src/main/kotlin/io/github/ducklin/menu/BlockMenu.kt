package io.github.ducklin.menu

import io.github.bakedlibs.dough.config.Config
import io.github.thebusybiscuit.slimefun4.implementation.Slimefun.Companion.logger
import org.bukkit.Location
import org.bukkit.block.Block
import java.io.File
import java.io.IOException
import java.nio.file.Files
import java.util.logging.Level

// This class will be deprecated, relocated and rewritten in a future version.
class BlockMenu : DirtyChestMenu {

    private var location: Location

    constructor(preset: BlockMenuPreset, l: Location) : super(preset) {
        this.location = l
        preset.clone(this)
        this.getContents()
    }

    constructor(preset: BlockMenuPreset, l: Location, cfg: Config) : super(preset) {
        this.location = l

        for (i in 0..53) {
            if (cfg.contains(i.toString())) {
                addItem(i, cfg.getItem(i.toString()))
            }
        }

        preset.clone(this)

        if (preset.size > -1 &&
            !preset.presetSlots.contains(preset.size - 1) &&
            cfg.contains((preset.size - 1).toString())
        ) {
            addItem(preset.size - 1, cfg.getItem((preset.size - 1).toString()))
        }

        this.getContents()
    }

    fun save(l: Location) {
        if (!isDirty()) return

        this.getContents()

        val file = File("data-storage/Slimefun/stored-inventories/" + serializeLocation(l) + ".sfi")
        val cfg = Config(file)
        cfg.setValue("preset", preset.id)

        for (slot in preset.inventorySlots) {
            cfg.setValue(slot.toString(), getItemInSlot(slot))
        }

        cfg.save()
        changes = 0
    }

    fun move(l: Location) {
        this.delete(this.location)
        this.location = l
        this.preset.newInstance(this, l)
        this.save(l)
    }


    fun reload() = preset.clone(this)

    val block: Block get() = location.block

    /**
     * This method drops the contents of this [BlockMenu] on the ground at the given
     * [Location].
     *
     * @param l
     * Where to drop these items
     * @param slots
     * The slots of items that should be dropped
     */
    fun dropItems(l: Location, vararg slots: Int) {
        for (slot in slots) {
            val item = getItemInSlot(slot) ?: continue
            l.getWorld().dropItemNaturally(l, item.clone())
            replaceExistingItem(slot, null)
        }
    }

    fun delete(l: Location) {
        val file = File("data-storage/Slimefun/stored-inventories/" + serializeLocation(l) + ".sfi")

        if (file.exists()) {
            try {
                Files.delete(file.toPath())
            } catch (e: IOException) {
                logger()!!.log(Level.WARNING, e) { "Could not delete file \"" + file.getName() + '"' }
            }
        }
    }

    companion object {
        private fun serializeLocation(l: Location): String {
            return l.getWorld().name + ';' + l.blockX + ';' + l.blockY + ';' + l.blockZ
        }
    }
}
