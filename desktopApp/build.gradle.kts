import org.jetbrains.compose.desktop.application.dsl.TargetFormat

plugins {
    alias(libs.plugins.jetbrains.kotlin.jvm)
    alias(libs.plugins.jetbrains.compose)
    alias(libs.plugins.jetbrains.kotlin.compose)
}

kotlin {
    jvmToolchain(21)
}

val javafxVersion = "21.0.2"
val javafxPlatform = when {
    System.getProperty("os.name").startsWith("Mac", ignoreCase = true) &&
        System.getProperty("os.arch") in setOf("aarch64", "arm64") -> "mac-aarch64"
    System.getProperty("os.name").startsWith("Mac", ignoreCase = true) -> "mac"
    System.getProperty("os.name").startsWith("Windows", ignoreCase = true) -> "win"
    System.getProperty("os.arch") in setOf("aarch64", "arm64") -> "linux-aarch64"
    else -> "linux"
}

dependencies {
    implementation(compose.desktop.currentOs)
    implementation(compose.material3)
    implementation(compose.foundation)
    implementation(compose.materialIconsExtended)
    implementation("net.jthink:jaudiotagger:3.0.0")
    implementation("org.json:json:20240303")
    implementation("org.openjfx:javafx-base:$javafxVersion:$javafxPlatform")
    implementation("org.openjfx:javafx-graphics:$javafxVersion:$javafxPlatform")
    implementation("org.openjfx:javafx-media:$javafxVersion:$javafxPlatform")
    implementation("org.openjfx:javafx-swing:$javafxVersion:$javafxPlatform")
}

compose.desktop {
    application {
        mainClass = "com.example.verseflow.desktop.MainKt"

        nativeDistributions {
            targetFormats(TargetFormat.Dmg)
            modules("java.net.http")
            packageName = "VerseFlow"
            packageVersion = "1.0.0"
            description = "A cinematic local music player concept for macOS."
            vendor = "VerseFlow"
            copyright = "© 2026 VerseFlow"

            macOS {
                bundleID = "com.example.verseflow.desktop"
                dockName = "VerseFlow"
            }
        }
    }
}
