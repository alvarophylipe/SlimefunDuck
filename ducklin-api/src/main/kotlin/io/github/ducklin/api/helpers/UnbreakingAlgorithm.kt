package io.github.ducklin.api.helpers

import java.util.function.IntFunction

enum class UnbreakingAlgorithm(
    private val function: IntFunction<Boolean>
) {
    ARMOR({ lvl -> Math.random() >= 0.6 + 0.4 / (lvl + 1) }),
    ITEM({ lvl -> Math.random() >= 1.0 / (lvl + 1) });

    fun evaluate(unbreakingLevel: Int): Boolean = unbreakingLevel > 0 && function.apply(unbreakingLevel)
}