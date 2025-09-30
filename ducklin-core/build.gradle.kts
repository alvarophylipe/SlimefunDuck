import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar

plugins {
    id("com.gradleup.shadow") version "9.0.0-rc2"
}

dependencies {
    // internal dependencies
    implementation(project(":ducklin-api"))
    implementation(project(":ducklin-items"))
    implementation(project(":ducklin-menus"))
    implementation(project(":ducklin-storage"))

    // soft dependencies
    compileOnly("com.sk89q.worldedit:worldedit-core:7.3.14")
    compileOnly("com.sk89q.worldedit:worldedit-bukkit:7.3.14")
    compileOnly("com.gmail.nossr50.mcMMO:mcMMO:2.2.040")
    compileOnly("me.clip:placeholderapi:2.11.6")
    compileOnly("me.minebuilders:clearlag-core:3.1.6")
    compileOnly("com.github.LoneDev6:itemsadder-api:3.6.1")
    compileOnly("net.imprex:orebfuscator-api:5.4.0")
    compileOnly("com.mojang:authlib:6.0.52")
    compileOnly("com.google.code.findbugs:jsr305:3.0.2")

    implementation("com.github.Slimefun.dough:dough-api:cb22e71335")
    implementation("io.papermc:paperlib:1.0.8")
    implementation("commons-lang:commons-lang:2.6")

    // coroutines
    implementation("com.github.shynixn.mccoroutine:mccoroutine-bukkit-api:2.22.0")
    implementation("com.github.shynixn.mccoroutine:mccoroutine-bukkit-core:2.22.0")

    // dependency injection
    implementation("io.insert-koin:koin-core:3.5.0")
}

tasks.withType<ShadowJar> {
    archiveBaseName.set("Ducklin")
    archiveClassifier.set("")
    archiveVersion.set(project.version.toString())


    // relocations
    val basePackage = "com.github.ducklin.libraries"
    relocate("kotlin", "$basePackage.kotlin")
    relocate("io.github.bakedlibs.dough", "$basePackage.dough")
    relocate("io.papermc.lib", "$basePackage.paperlib")
    relocate("org.apache.commons.lang", "$basePackage.commons.lang")
    relocate("org.koin", "$basePackage.koin")

    mergeServiceFiles()
    exclude("META-INF/**")
}

tasks.build {
    dependsOn(tasks.shadowJar)
}