package io.github.ducklin.api.machines

import io.github.bakedlibs.dough.blocks.BlockPosition

interface MachineOperation {
    fun addProgress(ticks: Int)

    fun getProgress(): Int

    fun getTotalTicks(): Int

    fun getRemainingTicks(): Int = getTotalTicks() - getProgress()

    fun isFinished(): Boolean = getRemainingTicks() <= 0

    fun onCancel(position: BlockPosition)

}