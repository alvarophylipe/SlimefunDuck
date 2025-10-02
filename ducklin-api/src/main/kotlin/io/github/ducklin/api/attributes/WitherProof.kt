package io.github.ducklin.api.attributes

import org.bukkit.block.Block
import org.bukkit.entity.Wither

interface WitherProof : ItemAttribute {
    fun onAttack(block: Block, wither: Wither)
}