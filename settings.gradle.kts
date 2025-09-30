rootProject.name = "Ducklin"

pluginManagement {
    repositories {
        mavenCentral()
        gradlePluginPortal()
    }
}

include(
    ":ducklin-api",
    ":ducklin-core",
    ":ducklin-items",
    ":ducklin-menus",
    ":ducklin-storage",
)