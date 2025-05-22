import io.ktor.plugin.features.DockerImageRegistry

plugins {
    alias(libs.plugins.kotlinJvm)
    alias(libs.plugins.ktor)
    application
}

group = "cc.worldmandia"
version = "1.0.0"
application {
    mainClass.set("cc.worldmandia.ApplicationKt")
    
    val isDevelopment: Boolean = project.ext.has("development")
    applicationDefaultJvmArgs = listOf("-Dio.ktor.development=$isDevelopment")
}

dependencies {
    implementation(projects.shared)
    implementation(libs.klogging)
    implementation(libs.ktor.serverCore)
    implementation(libs.ktor.serverCIO)
    testImplementation(libs.ktor.serverTestHost)
    testImplementation(libs.kotlin.testJunit)
}

ktor {
    docker {
        jreVersion.set(JavaVersion.VERSION_24)
        localImageName.set("worldmandia-web-local")
        imageTag.set(version.toString())
        customBaseImage.set("azul/zulu-openjdk:24-latest")

        externalRegistry.set(
            DockerImageRegistry.dockerHub(
                appName = provider { "worldmandia-web" },
                username = providers.environmentVariable("DOCKER_HUB_USERNAME"),
                password = providers.environmentVariable("DOCKER_HUB_PASSWORD")
            )
        )

        jib {
            container {
                workingDirectory = "/home/container" // pterodactyl based
                jvmFlags = listOf(
                    "-XX:+UseNUMA",
                )
            }
        }
    }
}
