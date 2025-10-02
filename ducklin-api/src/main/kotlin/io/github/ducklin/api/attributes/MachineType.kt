package io.github.ducklin.api.attributes

enum class MachineType(val suffix: String) {

    CAPACITOR("Capacitor"),
    GENERATOR("Generator"),
    MACHINE("Machine");

    override fun toString(): String = suffix

}