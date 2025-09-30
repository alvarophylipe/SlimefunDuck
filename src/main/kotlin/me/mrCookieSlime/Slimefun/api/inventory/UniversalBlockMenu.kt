package me.mrCookieSlime.Slimefun.api.inventory

import io.github.bakedlibs.dough.config.Config
import java.io.File

// This class will be deprecated, relocated and rewritten in a future version.
class UniversalBlockMenu(
    preset: BlockMenuPreset,
    cfg: Config? = null
) : DirtyChestMenu(preset) {

    init {
        cfg?.let { loadFromConfig(it) }
        preset.clone(this)
        if (cfg == null) save() else getContents()
    }

    private fun loadFromConfig(cfg: Config) {
        for (i in 0..53) {
            if (cfg.contains(i.toString())) {
                addItem(i, cfg.getItem(i.toString()))
            }
        }

        if (preset.presetSize > -1 &&
            preset.presetSize - 1 !in preset.presetSlots &&
            cfg.contains((preset.presetSize - 1).toString())
        ) {
            addItem(preset.presetSize - 1, cfg.getItem((preset.presetSize - 1).toString()))
        }
    }

    fun save() {
        if (!isDirty()) return

        getContents()

        val file = File("data-storage/Slimefun/universal-inventories/" + preset.id + ".sfi")
        val cfg = Config(file)
        cfg.setValue("preset", preset.id)

        for (slot in preset.inventorySlots) {
            cfg.setValue(slot.toString(), getItemInSlot(slot))
        }

        cfg.save()
        changes  = 0
    }
}
