plugins {
	id("mod-platform")
	id("fabric-loom")
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

	mixin {
		useLegacyMixinAp = true
		defaultRefmapName = "${prop("mod_id")}.refmap.json"
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
	mappings(
		loom.layered {
			officialMojangMappings()
			if (hasProperty("deps.parchment")) parchment("org.parchmentmc.data:parchment-${prop("deps.parchment")}@zip")
		})
	modImplementation(libs.fabric.loader)
	modImplementation("net.fabricmc.fabric-api:fabric-api:${prop("fabric_api_version")}")
	modImplementation("com.terraformersmc:modmenu:${prop("modmenu_version")}")
	include(modApi("teamreborn:energy:${prop("team_reborn_energy_version")}") {
		exclude(group = "net.fabricmc.fabric-api")
		exclude(group = "net.fabricmc.fabric-loader")
	})
	implementation(libs.moulberry.mixinconstraints)
	include(libs.moulberry.mixinconstraints)
}

tasks.withType<Javadoc> {
	enabled = false
}