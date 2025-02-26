import org.jetbrains.compose.desktop.application.dsl.TargetFormat

plugins {
    kotlin("jvm")
    id("org.jetbrains.compose")
    id("org.jetbrains.kotlin.plugin.compose")
}

group = "io.karn"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
    maven("https://maven.pkg.jetbrains.space/public/p/compose/dev")
    google()
}

dependencies {
    // Note, if you develop a library, you should use compose.desktop.common.
    // compose.desktop.currentOs should be used in launcher-sourceSet
    // (in a separate module for demo project and in testMain).
    // With compose.desktop.common you will also lose @Preview functionality
    implementation(compose.desktop.currentOs)
}

compose.desktop {
    application {
        mainClass = "MainKt"

        nativeDistributions {
            targetFormats(TargetFormat.Dmg, TargetFormat.Msi, TargetFormat.Deb)
            packageName = "shrinkwrap"
            packageVersion = "1.0.0"

            macOS {
                bundleID = "io.karn.shrinkwrap"
                dockName = "Shrinkwrap"
                iconFile.set(project.file("assets/icon.icns"))
                entitlementsFile.set(project.file("assets/macOS.entitlements"))
                runtimeEntitlementsFile.set(project.file("assets/macOS.entitlements"))
            }
        }
    }
}
