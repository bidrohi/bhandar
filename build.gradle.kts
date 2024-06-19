import com.android.build.api.dsl.CommonExtension
import org.jetbrains.kotlin.gradle.ExperimentalKotlinGradlePluginApi
import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension
import org.jetbrains.kotlin.gradle.plugin.mpp.KotlinAndroidTarget
import org.jetbrains.kotlin.gradle.tasks.KotlinJvmCompile

plugins {
    // this is necessary to avoid the plugins to be loaded multiple times
    // in each subproject's classloader
    alias(libs.plugins.android.application).apply(false)
    alias(libs.plugins.android.library).apply(false)
    alias(libs.plugins.kotlin.android).apply(false)
    alias(libs.plugins.kotlin.compose).apply(false)
    alias(libs.plugins.kotlin.jvm).apply(false)
    alias(libs.plugins.kotlin.multiplatform).apply(false)
    alias(libs.plugins.kotlin.serialization).apply(false)
    alias(libs.plugins.kotlinx.binCompatibility).apply(true)
    alias(libs.plugins.kotlinx.kover).apply(false)
    alias(libs.plugins.vanniktech.publish).apply(false)
}

val libNamespace by rootProject.extra { "com.bidyut.tech.bhandar" }
val libVersion by rootProject.extra { "0.2.0" }

apiValidation {
    ignoredProjects += setOf(
        "android",
        "jvm_cli",
    )
}

allprojects {
    layout.buildDirectory = File("${rootDir}/build/${projectDir.relativeTo(rootDir)}")
}

val jvmVersion = JavaVersion.VERSION_1_8
val kotlinJvmTarget = JvmTarget.JVM_1_8

configure(subprojects) {
    tasks.withType<KotlinJvmCompile>().configureEach {
        compilerOptions {
            jvmTarget.set(kotlinJvmTarget)
        }
    }
    tasks.withType<JavaCompile>().configureEach {
        sourceCompatibility = jvmVersion.toString()
        targetCompatibility = jvmVersion.toString()
    }
}

subprojects {
    afterEvaluate {
        (extensions.findByName("kotlin") as? KotlinMultiplatformExtension)?.apply {
            (targets.findByName("androidTarget") as? KotlinAndroidTarget)?.apply {
                publishLibraryVariants("release")
                @OptIn(ExperimentalKotlinGradlePluginApi::class)
                compilerOptions {
                    jvmTarget.set(kotlinJvmTarget)
                }
            }
        }
        (extensions.findByName("android") as? CommonExtension<*, *, *, *, *, *>)?.apply {
            compileSdk = libs.versions.android.compileSdk.get().toInt()
            buildToolsVersion = libs.versions.android.buildTools.get()
            defaultConfig {
                minSdk = libs.versions.android.minSdk.get().toInt()
                testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
            }
            compileOptions {
                sourceCompatibility = jvmVersion
                targetCompatibility = jvmVersion
            }
            lint {
                warningsAsErrors = true
                disable += "UnusedResources"
            }
            packaging {
                resources {
                    excludes += "/META-INF/{AL2.0,LGPL2.1}"
                }
            }
        }
    }
}
