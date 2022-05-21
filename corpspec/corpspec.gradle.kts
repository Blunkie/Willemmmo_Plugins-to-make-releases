import ProjectVersions.openosrsVersion

version = "1.1.0"

project.extra["PluginName"] = "CorpSpec"
project.extra["PluginDescription"] = "Spec Plugin for Corp"

dependencies {
    compileOnly("com.openosrs:runelite-api:$openosrsVersion+")
    compileOnly("com.openosrs:runelite-client:$openosrsVersion+")
    compileOnly(project(":willemmmoapi"))
    //compileOnly(group = "com.openosrs.externals", name = "willemmmoapi", version = "1.0.0+");
}
tasks {
    jar {
        manifest {
            attributes(mapOf(
                    "Plugin-Version" to project.version,
                    "Plugin-Id" to nameToId(project.extra["PluginName"] as String),
                    "Plugin-Provider" to project.extra["PluginProvider"],
                    "Plugin-Dependencies" to
                            arrayOf(
                                    nameToId("Willemmmo_Api")
                            ).joinToString(),
                    "Plugin-Description" to project.extra["PluginDescription"],
                    "Plugin-License" to project.extra["PluginLicense"]
            ))
        }
    }
}

