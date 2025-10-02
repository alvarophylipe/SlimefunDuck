package io.github.ducklin.api.attributes

import io.github.ducklin.api.machines.MachineOperation
import io.github.ducklin.api.machines.MachineProcessor


interface MachineProcessHolder<T : MachineOperation> : ItemAttribute {
    val machineProcessor: MachineProcessor<T>
}
