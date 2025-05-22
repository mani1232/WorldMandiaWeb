import org.jetbrains.kotlin.gradle.ExperimentalWasmDsl
import org.jetbrains.kotlin.gradle.targets.js.webpack.KotlinWebpackConfig

plugins {
    alias(libs.plugins.kotlinMultiplatform)
    
}

kotlin {
    jvm()
    
    @OptIn(ExperimentalWasmDsl::class)
    wasmJs {
        browser {
            val rootDirPath = project.rootDir.path
            val projectDirPath = project.projectDir.path
            commonWebpackConfig {
                devServer = (devServer ?: KotlinWebpackConfig.DevServer()).apply {
                    static = (static ?: mutableListOf()).apply {
                        // Serve sources to debug inside browser
                        add(rootDirPath)
                        add(projectDirPath)
                    }
                }
            }
        }
    }
    
    sourceSets {
        wasmJsMain.dependencies {
            implementation(libs.compass.geolocation.browser)
        }
        commonMain.dependencies {
            // put your Multiplatform dependencies here
            implementation(libs.kotlinx.datetime)
            implementation(libs.compass.geocoder)
            implementation(libs.compass.geocoder.web)
            implementation(libs.compass.geolocation)
            implementation(libs.compass.geolocation.googlemaps)
        }
        commonTest.dependencies {
            implementation(libs.kotlin.test)
        }
    }
}

