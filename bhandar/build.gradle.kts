import kotlinx.kover.gradle.plugin.dsl.AggregationType
import kotlinx.kover.gradle.plugin.dsl.CoverageUnit
import kotlinx.kover.gradle.plugin.dsl.GroupingEntityType
import org.jetbrains.kotlin.gradle.ExperimentalWasmDsl
import org.jetbrains.kotlin.gradle.plugin.mpp.apple.XCFramework

plugins {
    alias(libs.plugins.kotlin.multiplatform)
    alias(libs.plugins.android.library)
    alias(libs.plugins.kotlinx.kover)
    alias(libs.plugins.vanniktech.publish)
}

val libNamespace = rootProject.extra.get("libNamespace") as String
val libVersion = rootProject.extra.get("libVersion") as String
group = libNamespace
version = libVersion

kotlin {
    androidTarget()

    val frameworkName = "Bhandar"
    val xcf = XCFramework(frameworkName)
    listOf(
        iosX64(),
        iosArm64(),
        iosSimulatorArm64(),
        macosX64(),
        macosArm64(),
        watchosX64(),
        watchosArm32(),
        watchosArm64(),
        watchosSimulatorArm64(),
        tvosX64(),
        tvosArm64(),
        tvosSimulatorArm64(),
    ).forEach {
        it.binaries.framework {
            baseName = frameworkName
            xcf.add(this)
            isStatic = true
        }
    }

    jvm()

    js {
        browser()
        nodejs()
    }
    @OptIn(ExperimentalWasmDsl::class)
    wasmJs()

    linuxX64()
    linuxArm64()

    mingwX64()

    sourceSets {
        commonMain.dependencies {
            implementation(libs.kotlinx.coroutines.core)
        }
        commonTest.dependencies {
            implementation(libs.test.kotlin.core)
            implementation(libs.test.kotlinx.coroutines)
            implementation(libs.test.turbine)
        }
    }
}

android {
    namespace = "com.bidyut.tech.bhandar"
}

kover {
    reports {
        total {
            verify {
                onCheck = true
                rule {
                    groupBy = GroupingEntityType.APPLICATION
                    bound {
                        minValue = 95
                        coverageUnits = CoverageUnit.LINE
                        aggregationForGroup = AggregationType.COVERED_PERCENTAGE
                    }
                }
            }
        }
    }
}

mavenPublishing {
    coordinates(
        groupId = libNamespace,
        artifactId = "bhandar",
        version = libVersion,
    )

    pom {
        name = "Bhandar"
        url = "https://github.com/bidrohi/bhandar"
        inceptionYear = "2024"
        description = """
            Bhandar provides a simple repository implementation that combines network and local source.
        """.trimIndent()

        licenses {
            license {
                name = "CC BY-SA 4.0"
                url = "https://creativecommons.org/licenses/by-sa/4.0/"
            }
        }
        developers {
            developer {
                id = "bidrohi"
                name = "Saud Khan"
                url = "https://github.com/bidrohi/"
            }
        }
        scm {
            url = "https://github.com/bidrohi/bhandar"
        }
    }

    publishToMavenCentral()

    signAllPublications()
}
