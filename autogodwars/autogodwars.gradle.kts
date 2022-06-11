import ProjectVersions.openosrsVersion
version = "1.0.0"

project.extra["PluginName"] = "Auto Godwars"
project.extra["PluginDescription"] = "Plugin for Automatic Godwars"
dependencies {
    compileOnly("com.openosrs:runelite-api:$openosrsVersion+")
    compileOnly("com.openosrs:runelite-client:$openosrsVersion+")
    compileOnly(group = "com.openosrs.externals", name = "willemmmoapi", version = "1.0.0+");
    compileOnly(group = "com.openosrs.externals", name = "iutils", version = "5.0.0+");
}

tasks {
    jar {
        manifest {
            attributes(
                mapOf(
                    "Plugin-Version" to project.version,
                    "Plugin-Id" to nameToId(project.extra["PluginName"] as String),
                    "Plugin-Provider" to project.extra["PluginProvider"],
                    "Plugin-Dependencies" to
                            arrayOf(
                                nameToId("iUtils")
                            ).joinToString(),
                    "Plugin-Description" to project.extra["PluginDescription"],
                    "Plugin-License" to project.extra["PluginLicense"]
                )
            )
        }
    }
}