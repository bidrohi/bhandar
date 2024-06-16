plugins {
    alias(libs.plugins.kotlin.jvm)
}

group = rootProject.extra.get("libNamespace") as String
version = rootProject.extra.get("libVersion") as String

dependencies {
    implementation(project(":bhandar"))

    implementation(libs.kotlinx.coroutines.core)

    implementation(libs.ktor.client.core)
    implementation(libs.ktor.client.cio)
}
