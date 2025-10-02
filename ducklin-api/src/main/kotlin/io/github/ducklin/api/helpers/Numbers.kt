package io.github.ducklin.api.helpers


import net.kyori.adventure.text.format.NamedTextColor
import java.text.DecimalFormat
import java.text.DecimalFormatSymbols
import java.text.NumberFormat
import java.time.Duration
import java.time.LocalDateTime
import java.util.Locale

object Numbers {

    private val DECIMAL_FORMAT = DecimalFormat("#.##", DecimalFormatSymbols.getInstance(Locale.ROOT))

    fun formatBigNumber(number: Int): String = NumberFormat.getNumberInstance(Locale.US).format(number)

    fun getCompactDouble(value: Double): String = when {
        value < 0 -> "-" + getCompactDouble(-value)
        value < 1_000.0 -> DECIMAL_FORMAT.format(value)
        value < 1_000_000.0 -> DECIMAL_FORMAT.format(value / 1_000) + "K"
        value < 1_000_000_000.0 -> DECIMAL_FORMAT.format(value / 1_000_000) + "M"
        value < 1_000_000_000_000.0 -> DECIMAL_FORMAT.format(value / 1_000_000_000) + "B"
        value < 1_000_000_000_000_000.0 -> DECIMAL_FORMAT.format(value / 1_000_000_000) + "T"
        else -> DECIMAL_FORMAT.format(value / 1_000_000_000_000) + "Q"
    }

    fun parseGitHubDate(date: String): LocalDateTime = LocalDateTime.parse(date.dropLast(1))

    fun getElapsedTime(from: LocalDateTime, to: LocalDateTime = LocalDateTime.now()) : String {
        val hours = Duration.between(from, to).toHours()
        return when {
            hours == 0L -> "< 1h"
            hours < 24 -> "${hours}h"
            hours % 24 == 0L -> "${hours / 24}d"
            else -> "${hours / 24}d ${hours % 24}h"
        }
    }

    fun getTimeLeft(seconds: Int): String = buildString {
        val m = seconds / 60
        if (m > 0) append("${m}m ")
        append("${seconds - m * 60}s")
    }

    fun getColorFromPercentage(percentage: Float): NamedTextColor = when {
        percentage < 16  -> NamedTextColor.DARK_RED
        percentage < 32  -> NamedTextColor.RED
        percentage < 48  -> NamedTextColor.GOLD
        percentage < 64  -> NamedTextColor.YELLOW
        percentage < 80  -> NamedTextColor.DARK_GREEN
        else             -> NamedTextColor.GREEN
    }

    fun getInt(str: String, default: Int): Int =
        if (str.isNotEmpty() && str.all { it.isDigit() }) str.toInt() else default

    fun getAsMillis(nanoseconds: Long): String = when (nanoseconds) {
        0L -> "0ms"
        else -> {
            val ms = roundDecimalNumber(nanoseconds / 1_000_000.0)
            ms.replace('.', '.') + "ms" // DecimalFormat already uses '.'
        }
    }

    fun roundDecimalNumber(number: Double): String = DECIMAL_FORMAT.format(number)

    fun reparseDouble(number: Double): Double = roundDecimalNumber(number).toDouble()

    fun <T : Number> T.orElse(default: T): T = this

    fun clamp(min: Int, value: Int, max: Int): Int = value.coerceIn(min, max)

    fun getJavaVersion(): Int = System.getProperty("java.version")
        .substringAfter("1.")
        .substringBefore('.')
        .substringBefore('-')
        .toIntOrNull()
        ?: 0.also { error("Cannot identify Java version") }

    fun flowSafeAddition(a: Int, b: Int): Int =
        limitedAddition(a, b, Int.MIN_VALUE, Int.MAX_VALUE)

    fun limitedAddition(a: Int, b: Int, min: Int, max: Int): Int = when {
        (a == max && b > 0) || (b == max && a > 0) || (a > 0 && b > max - a) -> max
        (a == min && b < 0) || (b == min && a < 0) || (a < 0 && b < min - a) -> min
        else -> a + b
    }
 }