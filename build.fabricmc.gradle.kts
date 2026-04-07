plugins {
	id("mod-platform")
	id("net.fabricmc.fabric-loom")
}

platform {
	loader = "fabric"
	dependencies {
		required("minecraft") {
			versionRange = stonecutter.current.version
		}
		required("fabric-api") {
			slug("fabric-api")
			versionRange = ">=${prop("fabric_api_version")}"
		}
		required("fabricloader") {
			versionRange = ">=${libs.fabric.loader.get().version}"
		}
		optional("modmenu") {}
	}
}

configurations.configureEach {
	resolutionStrategy {
		force("net.fabricmc:fabric-loader:${prop("fabric_loader_version")}")
	}
}

loom {
	accessWidenerPath = rootProject.file(platform.awFile)
	runs.named("client") {
		client()
		ideConfigGenerated(true)
		runDir = "run/"
		environment = "client"
		programArgs("--username=Dev")
		configName = "Fabric Client"
	}
	runs.named("server") {
		server()
		ideConfigGenerated(true)
		runDir = "run/"
		environment = "server"
		configName = "Fabric Server"
	}
}

fabricApi {
	configureDataGeneration {
		outputDirectory = file("${rootDir}/versions/datagen/${stonecutter.current.version.split("-")[0]}/src/main/generated")
		client = true
	}
}

repositories {
	mavenCentral()
	strictMaven("https://maven.terraformersmc.com/", "com.terraformersmc") { name = "TerraformersMC" }
	strictMaven("https://api.modrinth.com/maven", "maven.modrinth") { name = "Modrinth" }
}

dependencies {
	minecraft("com.mojang:minecraft:${stonecutter.current.version}")
	implementation(libs.fabric.loader)
	implementation("net.fabricmc.fabric-api:fabric-api:${prop("fabric_api_version")}")
	implementation("com.terraformersmc:modmenu:${prop("modmenu_version")}")
	include(api("teamreborn:energy:${prop("team_reborn_energy_version")}") {
		exclude(group = "net.fabricmc.fabric-api")
		exclude(group = "net.fabricmc.fabric-loader")
	})
	implementation(libs.moulberry.mixinconstraints)
	include(libs.moulberry.mixinconstraints)
}

tasks.withType<Javadoc> {
	enabled = false
}