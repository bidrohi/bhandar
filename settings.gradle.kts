rootProject.name = "BhandarLibrary"
enableFeaturePreview("TYPESAFE_PROJECT_ACCESSORS")

pluginManagement {
    repositories {
        google {
            mavenContent {
                includeGroupAndSubgroups("androidx")
                includeGroupAndSubgroups("com.android")
                includeGroupAndSubgroups("com.google")
            }
        }
        mavenCentral()
        gradlePluginPortal()
    }
}

dependencyResolutionManagement {
    repositories {
        google {
            mavenContent {
                includeGroupAndSubgroups("androidx")
                includeGroupAndSubgroups("com.android")
                includeGroupAndSubgroups("com.google")
            }
        }
        maven {
            url = uri("https://maven.pkg.jetbrains.space/kotlin/p/wasm/experimental")
            mavenContent {
                includeGroupAndSubgroups("io.ktor")
            }
        }
        mavenCentral()
    }
}

include(":examples:android")
include(":examples:compose")
include(":examples:jvm_cli")
include(":bhandar")
