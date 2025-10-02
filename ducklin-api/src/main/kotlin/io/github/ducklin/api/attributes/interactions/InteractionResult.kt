package io.github.ducklin.api.attributes.interactions

open class InteractionResult(open val successful: Boolean, val message: String? = null) {
    fun hasMessage(): Boolean = message?.isNotBlank() ?: false
}