package io.github.ducklin.api.attributes

interface ProtectiveArmor : ItemAttribute {
    fun getProtectionTypes(): Array<ProtectionType>

    fun isFullSetRequired(): Boolean

    fun getArmorSetId(): String?

}