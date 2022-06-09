import ProjectVersions.openosrsVersion

buildscript {
    repositories {
        gradlePluginPortal()
    }
}

plugins {
    checkstyle
    java
}

project.extra["GithubUrl"] = "https://github.com/Willemmmo/plugins"
apply<BootstrapPlugin>()
apply<VersionPlugin>()

allprojects {
    group = "com.openosrs.externals"
    version = openosrsVersion
    apply<MavenPublishPlugin>()
    repositories {
        mavenLocal()
        mavenCentral()
    }
}

subprojects {
    group = "com.openosrs.externals"

    project.extra["PluginProvider"] = "Willemmmo"
    project.extra["ProjectUrl"] = "https://github.com/Willemmmo"
    project.extra["ProjectSupportUrl"] = "https://github.com/Willemmmo"
    project.extra["PluginLicense"] = "3-Clause BSD License"

    repositories {
        jcenter {
            content {
                excludeGroupByRegex("com\\.openosrs.*")
                excludeGroupByRegex("com\\.runelite.*")
            }
        }

        exclusiveContent {
            forRepository {
                mavenLocal()
            }
            filter {
                includeGroupByRegex("com\\.openosrs.*")
            }
        }
    }

    apply<JavaPlugin>()
    apply(plugin = "checkstyle")

    dependencies {
        annotationProcessor(Libraries.lombok)
        annotationProcessor(Libraries.pf4j)

        compileOnly("com.openosrs:http-api:$openosrsVersion+")
        compileOnly("com.openosrs:runelite-api:$openosrsVersion+")
        compileOnly("com.openosrs:runelite-client:$openosrsVersion+")
        compileOnly("com.openosrs.rs:runescape-api:$openosrsVersion+")

        compileOnly(Libraries.findbugs)
        compileOnly(Libraries.apacheCommonsText)
        compileOnly(Libraries.gson)
        compileOnly(Libraries.guice)
        compileOnly(Libraries.lombok)
        compileOnly(Libraries.okhttp3)
        compileOnly(Libraries.pf4j)
        compileOnly(Libraries.rxjava)
    }

    checkstyle {
        maxWarnings = 0
        toolVersion = "9.1"
        isShowViolations = true
        isIgnoreFailures = false
    }

    configure<PublishingExtension> {
        repositories {
            maven {
                url = uri("$buildDir/repo")
            }
        }
        publications {
            register("mavenJava", MavenPublication::class) {
                from(components["java"])
            }
        }
    }

    configure<JavaPluginConvention> {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }

    tasks {
        withType<JavaCompile> {
            options.encoding = "UTF-8"
        }

        withType<Jar> {
            doLast {
                copy {
                    from("./build/libs/")
                    into("../release/")
                }
            }
        }
        withType<Jar> {
            doLast {
                copy {
                    from("./build/libs/")
                    into(System.getProperty("user.home") + "/.openosrs/plugins")
                }
            }
        }

        withType<AbstractArchiveTask> {
            isPreserveFileTimestamps = false
            isReproducibleFileOrder = true
            dirMode = 493
            fileMode = 420
        }

        withType<Checkstyle> {
            group = "verification"

            exclude("**/ScriptVarType.java")
            exclude("**/LayoutSolver.java")
            exclude("**/RoomType.java")
        }

        register<Copy>("copyDeps") {
            into("./build/deps/")
            from(configurations["runtimeClasspath"])
        }
    }
}
repositories {
    mavenCentral()
}