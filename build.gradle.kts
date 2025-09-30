import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    java
    id("com.gradleup.shadow") version "9.0.0-rc2" apply false
    kotlin("jvm") version "2.2.0" apply false
}

buildscript {
    val objectboxVersion by extra("5.0.0")

    repositories {
        mavenCentral()
    }

    dependencies {
        classpath("io.objectbox:objectbox-gradle-plugin:$objectboxVersion")
    }
}

allprojects {
    apply(plugin = "org.jetbrains.kotlin.jvm")

    group = "io.github.ducklin"
    version = "MIGRATION"

    repositories {
        mavenCentral()
    }
}

subprojects {

    repositories {
        maven("https://hub.spigotmc.org/nexus/content/repositories/snapshots")
        maven("https://repo.papermc.io/repository/maven-public/")
        maven("https://maven.enginehub.org/repo/")
        maven("https://jitpack.io")
        maven("https://repo.extendedclip.com/content/repositories/placeholderapi")
        maven("https://nexus.neetgames.com/repository/maven-public")
        maven("https://repo.walshy.dev/public")
        maven("https://repo.codemc.io/repository/maven-public/")
        maven("https://libraries.minecraft.net")
        maven("https://repo.dmulloy2.net/repository/public/")
    }

    dependencies {
        compileOnly("io.papermc.paper:paper-api:1.21.5-R0.1-SNAPSHOT")
    }

    java {
        toolchain.languageVersion.set(JavaLanguageVersion.of(21))
    }

    tasks.withType<JavaCompile>().configureEach {
        options.encoding = "UTF-8"
    }

    tasks.withType<KotlinCompile>().configureEach {
        compilerOptions.jvmTarget.set(JvmTarget.JVM_21)
    }

}

//val junitVersion = "5.11.4"
//
//dependencies {
//    compileOnly("io.papermc.paper:paper-api:$paperVersion")
//
//    compileOnly("com.sk89q.worldedit:worldedit-core:7.3.14")
//    compileOnly("com.sk89q.worldedit:worldedit-bukkit:7.3.14")
//    compileOnly("com.gmail.nossr50.mcMMO:mcMMO:2.2.040")
//    compileOnly("me.clip:placeholderapi:2.11.6")
//    compileOnly("me.minebuilders:clearlag-core:3.1.6")
//    compileOnly("com.github.LoneDev6:itemsadder-api:3.6.1")
//    compileOnly("net.imprex:orebfuscator-api:5.4.0")
//    compileOnly("com.mojang:authlib:6.0.52")
//
//    implementation("com.github.Slimefun.dough:dough-api:cb22e71335")
//    implementation("io.papermc:paperlib:1.0.8")
//    implementation("commons-lang:commons-lang:2.6")
//    implementation(kotlin("stdlib"))
//    implementation("com.github.shynixn.mccoroutine:mccoroutine-bukkit-api:2.22.0")
//    implementation("com.github.shynixn.mccoroutine:mccoroutine-bukkit-core:2.22.0")
//
//    compileOnly("com.google.code.findbugs:jsr305:3.0.2")
//
//    testImplementation(platform("org.junit:junit-bom:$junitVersion"))
//    testImplementation("org.junit.jupiter:junit-jupiter")
//    testImplementation("org.mockito:mockito-core:5.15.2")
//    testImplementation("org.slf4j:slf4j-simple:2.0.16")
//    testImplementation("com.github.seeseemelk:MockBukkit-v1.21:3.133.2") {
//        exclude(group = "org.jetbrains", module = "annotations")
//    }
//}
//
//java {
//    toolchain.languageVersion.set(JavaLanguageVersion.of(21))
//    withSourcesJar()
//    withJavadocJar()
//}

//kotlin {
//    jvmToolchain(21)
//}

//sourceSets {
//    main {
//        kotlin.srcDir("src/main/kotlin")
//    }
//}

//tasks.withType<JavaCompile>().configureEach {
//    options.encoding = "UTF-8"
//}

//tasks.test {
//    useJUnitPlatform()
//    javaLauncher.set(
//        serviceOf<JavaToolchainService>().launcherFor {
//            languageVersion.set(JavaLanguageVersion.of(21))
//        }
//    )
//}

//tasks.withType<ShadowJar> {
//    archiveBaseName.set("Ducklin")
//    archiveClassifier.set("")
//    archiveVersion.set(project.version.toString())
//
//    relocate("io.github.bakedlibs.dough", "io.github.thebusybiscuit.slimefun4.libraries.dough")
//    relocate("io.papermc.lib", "io.github.thebusybiscuit.slimefun4.libraries.paperlib")
//    relocate("org.apache.commons.lang", "io.github.thebusybiscuit.slimefun4.libraries.commons.lang")
//
//    mergeServiceFiles()
//    exclude("META-INF/**")
//    minimize()
//}
//
//tasks.build {
//    dependsOn(tasks.shadowJar)
//}