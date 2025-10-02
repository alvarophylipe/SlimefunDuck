package io.github.ducklin.api

import javax.annotation.Nonnull

enum class MinecraftVersion(
    val majorVersion: Int,
    val minorVersion: Int = -1,
    val maxMinorVersion: Int = -1,
    val displayName: String,
    val virtual: Boolean = false
) {
    MINECRAFT_1_20(20, 0, 4, "1.20.x"),

    MINECRAFT_1_20_5(20, 5, "1.20.5+"),

    MINECRAFT_1_21(21, 0, "1.21.x"),

    UNKNOWN("Unknown", true),

    UNIT_TEST("Unit Test Environment", true);


    constructor(majorVersion: Int, name: String) : this(majorVersion, -1, -1, name, false)
    constructor(majorVersion: Int, minorVersion: Int, name: String) : this(majorVersion, minorVersion, -1, name, false)
    constructor(name: String, virtual: Boolean) : this(0, -1, -1, name, virtual)

    fun isMinecraftVersion(minecraftVersion: Int): Boolean {
        return this.isMinecraftVersion(minecraftVersion, -1)
    }

    fun isMinecraftVersion(minecraftVersion: Int, patchVersion: Int): Boolean {
        return !this.virtual && this.majorVersion == minecraftVersion && (this.minorVersion == -1 || this.minorVersion <= patchVersion)
                && (this.maxMinorVersion == -1 || patchVersion <= this.maxMinorVersion)
    }


    fun isAtLeast(version: MinecraftVersion): Boolean {
        if (this == UNKNOWN) {
            return false
        }

        require(!(this == UNIT_TEST && version.ordinal == 0)) { "Version $version is the lowest supported version already!" }

        return this.ordinal >= version.ordinal
    }

    fun isBefore(@Nonnull version: MinecraftVersion): Boolean {

        if (this == UNKNOWN) {
            return true
        }

        return version.ordinal > this.ordinal
    }

    fun isBefore(minecraftVersion: Int, patchVersion: Int): Boolean {
        // unit tests or whatever
        if (this.virtual) {
            return false
        }

        // major version mismatch
        if (this.majorVersion != minecraftVersion) {
            return this.majorVersion < minecraftVersion
        }

        return if (this.minorVersion == -1) patchVersion > 0 else this.minorVersion < patchVersion
    }
}