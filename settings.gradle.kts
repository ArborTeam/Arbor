pluginManagement {
    val weightVersion: String by settings

    repositories {
        gradlePluginPortal()
        mavenLocal()
        maven("https://repo.papermc.io/repository/maven-public/")
        maven("https://repo.bacteriawa.com/repository/maven-public/")
        maven("https://repo.littleovo.cn/public")
    }

    plugins {
        id("moe.luminolmc.hyacinthusweight.patcher") version weightVersion
        id("moe.luminolmc.hyacinthusweight.core") version weightVersion
    }
}

plugins {
    id("org.gradle.toolchains.foojay-resolver-convention") version "1.0.0"
}

rootProject.name = "arbor"

for (name in listOf("arbor-api", "arbor-server")) {
    include(name)
    file(name).mkdirs()
}

optionalInclude("test-plugin")
optionalInclude("arbor-generator")

fun optionalInclude(name: String, op: (ProjectDescriptor.() -> Unit)? = null) {
    val settingsFile = file("$name.settings.gradle.kts")
    if (settingsFile.exists()) {
        apply(from = settingsFile)
        findProject(":$name")?.let { op?.invoke(it) }
    } else {
        settingsFile.writeText(
            """
            // Uncomment to enable the '$name' project
            // include(":$name")

            """.trimIndent()
        )
    }
}

gradle.lifecycle.beforeProject {
    val mcVersion = providers.gradleProperty("mcVersion").get().trim()
    val arborVersionChannel = providers.gradleProperty("channel").get().trim()
    val arborBuildNumber = providers.environmentVariable("BUILD_NUMBER").orNull?.trim()?.toInt()
    val versionString = if (arborBuildNumber == null) {
        "$mcVersion.local-SNAPSHOT"
    } else {
        "$mcVersion.build.$arborBuildNumber-${arborVersionChannel.lowercase()}"
    }
    version = versionString
}
