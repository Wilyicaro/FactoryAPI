plugins {
    id("dev.kikugie.stonecutter")
    //id("dev.architectury.loom") version "1.13.+" apply false
    id("net.fabricmc.fabric-loom") version "1.14.+" apply false
    id("net.fabricmc.fabric-loom-remap") version "1.14.+" apply false
}

stonecutter active "1.20.4-fabric" /* [SC] DO NOT EDIT */

stonecutter parameters {
    val loader = node.project.property("loom.platform").toString()
    constants.match(loader, "fabric", "forge", "neoforge")
}
