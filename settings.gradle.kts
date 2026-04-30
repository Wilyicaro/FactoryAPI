pluginManagement {
    repositories {
        mavenLocal()
        mavenCentral()
        gradlePluginPortal()
        maven("https://maven.fabricmc.net/") { name = "Fabric" }
        maven("https://maven.neoforged.net/releases/") { name = "NeoForged" }
        maven("https://maven.minecraftforge.net/") { name = "MinecraftForge" }
        maven("https://maven.kikugie.dev/snapshots") { name = "KikuGie Snapshots" }
        maven("https://maven.kikugie.dev/releases") { name = "KikuGie Releases" }
        maven("https://maven.parchmentmc.org") { name = "ParchmentMC" }
        maven("https://maven.terraformersmc.com/") { name = "TerraformersMC" }
        exclusiveContent {
            forRepository { maven("https://api.modrinth.com/maven") { name = "Modrinth" } }
            filter { includeGroup("maven.modrinth") }
        }
    }
    includeBuild("build-logic")
}

plugins {
    id("org.gradle.toolchains.foojay-resolver-convention") version "1.0.0"
    id("dev.kikugie.stonecutter") version "0.9"
}

stonecutter {
    create(rootProject) {
        fun match(version: String, vararg loaders: String) =
            loaders.forEach { version("$version-$it", version).buildscript = "build.${if (it == "fabric" && stonecutter.eval(version, ">=26.1")) "fabricmc" else it}.gradle.kts" }

        //Some JDK 17 instances don't work with Forge <=1.20.4, you can just exclude these, so it can be downloaded automatically
        match("1.20.1", "fabric", "forge")
        match("1.20.4", "fabric", "forge", "neoforge")
        match("1.21.1", "fabric", "forge", "neoforge")
        match("1.21.3", "fabric", "forge", "neoforge")
        match("1.21.4", "fabric", "forge", "neoforge")
        match("1.21.5", "fabric", "forge", "neoforge")
        match("1.21.8", "fabric", "forge", "neoforge")
        match("1.21.10", "fabric", "forge", "neoforge")
        match("1.21.11", "fabric", "forge", "neoforge")
        match("26.1.2", "fabric", "forge", "neoforge")
        vcsVersion = "1.20.4-fabric"
    }
}

rootProject.name = "FactoryAPI"
