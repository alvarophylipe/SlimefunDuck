package io.github.ducklin.migration

import org.apache.commons.lang.Validate
import javax.annotation.Nonnull

/**
 * This enum holds all versions of Minecraft that we currently support.
 *
 * @author TheBusyBiscuit
 * @author Walshy
 *
 * @see io.github.thebusybiscuit.slimefun4.implementation.Slimefun
 */
enum class MinecraftVersion(
    val majorVersion: Int,
    val minorVersion: Int = -1,
    val maxMinorVersion: Int = -1,
    val displayName: String,
    val virtual: Boolean = false
) {
    /**
     * This constant represents Minecraft (Java Edition) Version 1.20
     * ("The Trails &amp; Tales Update")
     */
    MINECRAFT_1_20(20, 0, 4, "1.20.x"),

    /**
     * This constant represents Minecraft (Java Edition) Version 1.20.5
     * ("The Armored Paws Update")
     */
    MINECRAFT_1_20_5(20, 5, "1.20.5+"),

    /**
     * This constant represents Minecraft (Java Edition) Version 1.21
     * ("Tricky Trials")
     */
    MINECRAFT_1_21(21, 0, "1.21.x"),

    /**
     * This constant represents an exceptional state in which we were unable
     * to identify the Minecraft Version we are using
     */
    UNKNOWN("Unknown", true),

    /**
     * This is a very special state that represents the environment being a Unit
     * Test and not an actual running Minecraft Server.
     */
    UNIT_TEST("Unit Test Environment", true);

    /**
     * This constructs a new [MinecraftVersion] with the given name.
     * This constructor forces the [MinecraftVersion] to be real.
     * It must be a real version of Minecraft.
     *
     * @param majorVersion
     * The major version of minecraft as an [Integer]
     * @param name
     * The display name of this [MinecraftVersion]
     */

    constructor(majorVersion: Int, name: String) : this(majorVersion, -1, -1, name, false)
    constructor(majorVersion: Int, minorVersion: Int, name: String) : this(majorVersion, minorVersion, -1, name, false)
    constructor(name: String, virtual: Boolean) : this(0, -1, -1, name, virtual)

    /**
     * This tests if the given minecraft version number matches with this
     * [MinecraftVersion].
     *
     *
     * You can obtain the version number by doing [io.papermc.lib.PaperLib.getMinecraftVersion].
     * It is equivalent to the "major" version
     *
     *
     * Example: &quot;1.13&quot; returns 13
     *
     * @param minecraftVersion
     * The [Integer] version to match
     *
     * @return Whether this [MinecraftVersion] matches the specified version id
     */
    fun isMinecraftVersion(minecraftVersion: Int): Boolean {
        return this.isMinecraftVersion(minecraftVersion, -1)
    }

    /**
     * This tests if the given minecraft version matches with this
     * [MinecraftVersion].
     *
     *
     * You can obtain the version number by doing [io.papermc.lib.PaperLib.getMinecraftVersion].
     * It is equivalent to the "major" version<br></br>
     * You can obtain the patch version by doing [io.papermc.lib.PaperLib.getMinecraftPatchVersion].
     * It is equivalent to the "minor" version
     *
     *
     * Example: &quot;1.13&quot; returns 13<br></br>
     * Example: &quot;1.13.2&quot; returns 13_2
     *
     * @param minecraftVersion
     * The [Integer] version to match
     *
     * @return Whether this [MinecraftVersion] matches the specified version id
     */
    fun isMinecraftVersion(minecraftVersion: Int, patchVersion: Int): Boolean {
        return !this.virtual && this.majorVersion == minecraftVersion && (this.minorVersion == -1 || this.minorVersion <= patchVersion)
                && (this.maxMinorVersion == -1 || patchVersion <= this.maxMinorVersion)
    }

    /**
     * This method checks whether this [MinecraftVersion] is newer or equal to
     * the given [MinecraftVersion],
     *
     * An unknown version will default to false.
     *
     * @param version
     * The [MinecraftVersion] to compare
     *
     * @return Whether this [MinecraftVersion] is newer or equal to the given [MinecraftVersion]
     */
    fun isAtLeast(@Nonnull version: MinecraftVersion): Boolean {
        Validate.notNull(version, "A Minecraft version cannot be null!")

        if (this == UNKNOWN) {
            return false
        }

        /**
         * Unit-Test only code.
         * Running #isAtLeast(...) should always be meaningful.
         * If the provided version equals the lowest supported version, then
         * this will essentially always return true and result in a tautology.
         * This is most definitely an oversight from us and should be fixed, therefore
         * we will trigger an exception.
         *
         * In order to not disrupt server operations, this exception is only thrown during
         * unit tests since the oversight itself will be harmless.
         */
        require(!(this == UNIT_TEST && version.ordinal == 0)) { "Version $version is the lowest supported version already!" }

        return this.ordinal >= version.ordinal
    }

    /**
     * This checks whether this [MinecraftVersion] is older than the specified [MinecraftVersion].
     *
     * An unknown version will default to true.
     *
     * @param version
     * The [MinecraftVersion] to compare
     *
     * @return Whether this [MinecraftVersion] is older than the given one
     */
    fun isBefore(@Nonnull version: MinecraftVersion): Boolean {
        Validate.notNull(version, "A Minecraft version cannot be null!")

        if (this == UNKNOWN) {
            return true
        }

        return version.ordinal > this.ordinal
    }

    /**
     * Checks whether this [MinecraftVersion] is older than the specified minecraft and patch versions
     * @param minecraftVersion The minecraft version
     * @param patchVersion The patch version
     * @return True if this version is before, False if this version is virtual or otherwise.
     */
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