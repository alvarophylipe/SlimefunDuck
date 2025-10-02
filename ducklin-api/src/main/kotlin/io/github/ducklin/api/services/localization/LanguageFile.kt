package io.github.ducklin.api.services.localization

enum class LanguageFile(val fileName: String) {

    MESSAGES("messages.yml"),
    CATEGORIES("categories.yml"),
    RECIPES("recipes.yml"),
    RESOURCES("resources.yml"),
    RESEARCHES("researches.yml");

    companion object {
        val valuesCached: Array<LanguageFile> = entries.toTypedArray()
    }

    fun path(language: Language): String = path(language.id)

    fun path(languageId: String): String {
        require(languageId.isNotBlank()) {
            "Language id must not be blank!"
        }

        return "/languages/$languageId/$fileName"
    }
}