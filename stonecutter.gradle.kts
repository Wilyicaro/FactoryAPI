plugins {
    id("dev.kikugie.stonecutter")
    id("dev.architectury.loom") version "1.11.+" apply false
}

stonecutter active "1.20.4-fabric" /* [SC] DO NOT EDIT */

stonecutter parameters {
    val loader = node.project.property("loom.platform").toString()
    constants.match(loader, "fabric", "forge", "neoforge")
}
