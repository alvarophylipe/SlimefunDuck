package io.github.ducklin.api.machines

import io.github.bakedlibs.dough.blocks.BlockPosition
import io.github.ducklin.api.attributes.MachineProcessHolder
import org.bukkit.Bukkit
import org.bukkit.Location
import org.bukkit.block.Block
import org.bukkit.inventory.ItemStack
import java.util.concurrent.ConcurrentHashMap

class MachineProcessor<T : MachineOperation>(
    private val owner: MachineProcessHolder<T>
) {

    private val machines = ConcurrentHashMap<BlockPosition, T>()
    var progressBar: ItemStack? = null

    fun getOwner(): MachineProcessHolder<T> = owner

    fun startOperation(loc: Location, operation: T): Boolean =
        startOperation(BlockPosition(loc), operation)

    fun startOperation(b: Block, operation: T): Boolean =
        startOperation(BlockPosition(b), operation)

    fun startOperation(pos: BlockPosition, operation: T): Boolean =
        machines.putIfAbsent(pos, operation) == null

    fun getOperation(loc: Location): T? = getOperation(BlockPosition(loc))
    fun getOperation(b: Block): T? = getOperation(BlockPosition(b))
    fun getOperation(pos: BlockPosition): T? = machines[pos]

    fun endOperation(loc: Location): Boolean = endOperation(BlockPosition(loc))
    fun endOperation(b: Block): Boolean = endOperation(BlockPosition(b))

    fun endOperation(pos: BlockPosition): Boolean {
        val op = machines.remove(pos) ?: return false
        if (op.isFinished()) {
//            Bukkit.getPluginManager().callEvent(AsyncMachineOperationFinishEvent(pos, this, op))
        } else {
            op.onCancel(pos)
        }
        return true
    }

//    fun updateProgressBar(inv: BlockMenu, slot: Int, operation: T) {
//        val bar = progressBar ?: return
//        val remaining = operation.remainingTicks
//        val total = operation.totalTicks
//        if (remaining > 0 || total > 0) {
//            ChestMenuUtils.updateProgressbar(inv, slot, remaining, total, bar)
//        }
//    }
}