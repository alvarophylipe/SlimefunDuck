package io.github.thebusybiscuit.slimefun4.api

import io.github.bakedlibs.dough.common.CommonPatterns
import io.github.thebusybiscuit.slimefun4.implementation.Slimefun
import org.apache.commons.lang.Validate
import javax.annotation.Nonnull

/**
 * This enum represents the branch this Slimefun build is on.
 * development or stable, unofficial or even unknown.
 *
 * @author TheBusyBiscuit
 */
enum class SlimefunBranch(
    @get:Nonnull var branchName: String,
    official: Boolean
) {
    /**
     * This build stems from the official "development" branch, it is prefixed with `DEV - X`
     */
    DEVELOPMENT("development build", true),

    /**
     * This build stems from the official "stable" branch, it is prefixed with `RC - X`
     */
    STABLE("\"stable\" build", true),

    /**
     * This build stems from an unofficial branch, it contains the string `UNOFFICIAL`
     */
    UNOFFICIAL("Unofficial build", false),

    /**
     * This build comes from any other branch. The version does not look like anything we recognize.
     * It is definitely not an official build.
     */
    UNKNOWN("Unofficial build", false);

    /**
     * This returns the name of this [SlimefunBranch]. The name is just a more readable
     * version of the enum constant.
     *
     * @return The name of this [SlimefunBranch]
     */

    /**
     * This method returns whether this [SlimefunBranch] is considered official.
     * Or whether it was unofficially modified.
     *
     * @return Whether this branch is an official one.
     */
    val isOfficial: Boolean

    init {
        Validate.notNull(name, "The branch name cannot be null")

        this.branchName = name
        this.isOfficial = official

        check(
            CommonPatterns.ASCII.matcher(name).matches()
        ) { "The SlimefunBranch enum contains ILLEGAL CHARACTERS. DO NOT TRANSLATE THIS FILE." }
    }
}