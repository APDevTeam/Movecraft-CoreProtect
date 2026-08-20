plugins {
    `java-library`
    `maven-publish`
    id("io.github.apdevteam.github-packages") version "1.2.3"
    id("io.papermc.hangar-publish-plugin") version "0.1.4"
}

repositories {
    gradlePluginPortal()
    mavenLocal()
    mavenCentral()
    maven("https://repo.papermc.io/repository/maven-public/")
    maven { githubPackage("apdevteam/movecraft")(this) }
    maven("https://maven.playpro.com")
}

dependencies {
    api("org.jetbrains:annotations-java5:24.1.0")
    compileOnly("io.papermc.paper:paper-api:1.20.6-R0.1-SNAPSHOT")
    compileOnly("net.countercraft:movecraft:+")
    compileOnly("net.coreprotect:coreprotect:22.4")
}

group = "net.countercraft.movecraft.coreprotect"
version = System.getenv("RELEASE_VERSION")?.takeIf { it.isNotBlank() }
    ?: runCatching {
        val sha = ProcessBuilder("git", "rev-parse", "--short", "HEAD")
            .start().inputStream.bufferedReader().readLine() ?: "unknown"
        val tag = ProcessBuilder("git", "describe", "--tags", "--abbrev=0")
            .start().inputStream.bufferedReader().readLine() ?: "untagged"
        val dirty = ProcessBuilder("git", "status", "--porcelain")
            .start().inputStream.bufferedReader().readLine() != null
        if (dirty) "$tag+$sha-dirty" else "$tag+$sha"
    }.getOrElse { "unknown" }
description = "Movecraft-CoreProtect"
java.toolchain.languageVersion = JavaLanguageVersion.of(21)

tasks.jar {
    archiveBaseName.set("Movecraft-CoreProtect")
    archiveClassifier.set("")
    archiveVersion.set("")
}

tasks.processResources {
    from(rootProject.file("LICENSE.md"))
    filesMatching("*.yml") {
        expand(mapOf("projectVersion" to project.version))
    }
}

publishing {
    publications {
        create<MavenPublication>("maven") {
            groupId = "net.countercraft.movecraft.coreprotect"
            artifactId = "movecraft-coreprotect"
            version = "${project.version}"

            artifact(tasks.jar)
        }
    }
    repositories {
        maven {
            name = "GitHubPackages"
            url = uri("https://maven.pkg.github.com/apdevteam/movecraft-coreprotect")
            credentials {
                username = System.getenv("GITHUB_ACTOR")
                password = System.getenv("GITHUB_TOKEN")
            }
        }
    }
}

hangarPublish {
    publications.register("plugin") {
        version.set(project.version as String)
        channel.set("Release")
        id.set("Airship-Pirates/Movecraft-CoreProtect")
        apiKey.set(System.getenv("HANGAR_API_TOKEN"))
        platforms {
            register(io.papermc.hangarpublishplugin.model.Platforms.PAPER) {
                jar.set(tasks.jar.flatMap { it.archiveFile })
                platformVersions.set(listOf("1.20.6-26.2"))
                dependencies {
                    hangar("Movecraft") {
                        required.set(true)
                    }
                    hangar("CoreProtect") {
                        required.set(true)
                    }
                }
            }
        }
    }
}
