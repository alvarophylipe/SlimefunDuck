package io.github.ducklin.api.services

import io.github.ducklin.api.items.DucklinItem
import org.bukkit.Location
import org.bukkit.block.Block

interface BlockDataService {

    fun getBlockData(location: Location): DucklinItem?

    fun getBlockData(block: Block): DucklinItem?
}