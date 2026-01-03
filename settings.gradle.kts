pluginManagement {
    repositories {
        maven("https://maven.fabricmc.net/")
        maven("https://maven.architectury.dev/")
        maven("https://maven.minecraftforge.net/")
        maven("https://maven.kikugie.dev/snapshots")
        maven("https://maven.kikugie.dev/releases")
        gradlePluginPortal()
    }
}

plugins {
    id("dev.kikugie.stonecutter") version "0.8.1"
}

stonecutter {
    create(rootProject) {
        version("1.20.1-fabric", "1.20.1")
        version("1.20.1-forge", "1.20.1")
        version("1.20.4-fabric", "1.20.4")
        version("1.20.4-forge", "1.20.4")
        version("1.20.4-neoforge", "1.20.4")
        version("1.21.1-fabric", "1.21.1")
        version("1.21.1-forge", "1.21.1")
        version("1.21.1-neoforge", "1.21.1")
        version("1.21.3-fabric", "1.21.3")
        version("1.21.3-forge", "1.21.3")
        version("1.21.3-neoforge", "1.21.3")
        version("1.21.4-fabric", "1.21.4")
        version("1.21.4-forge", "1.21.4")
        version("1.21.4-neoforge", "1.21.4")
        version("1.21.5-fabric", "1.21.5")
        version("1.21.5-forge", "1.21.5")
        version("1.21.5-neoforge", "1.21.5")
        version("1.21.8-fabric", "1.21.8")
        version("1.21.8-forge", "1.21.8")
        version("1.21.8-neoforge", "1.21.8")
        version("1.21.10-fabric", "1.21.10")
        version("1.21.10-forge", "1.21.10")
        version("1.21.10-neoforge", "1.21.10")
        version("1.21.11-fabric", "1.21.11")
        vcsVersion = "1.20.4-fabric"
    }
}

rootProject.name = "FactoryAPI"
