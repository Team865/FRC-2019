@file:Suppress("UnusedImport", "SpellCheckingInspection")

import edu.wpi.first.gradlerio.frc.FRCJavaArtifact
import edu.wpi.first.gradlerio.frc.RoboRIO
import edu.wpi.first.toolchain.NativePlatforms
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm")
    id("edu.wpi.first.GradleRIO")
}

repositories {
    mavenCentral()
    maven { setUrl("https://jitpack.io") }
}

tasks.withType<KotlinCompile> {
    kotlinOptions {
        freeCompilerArgs = listOf(
                "-Xnew-inference",
                "-Xuse-experimental=kotlin.Experimental",
                "-Xallow-kotlin-package",
                "-Xno-call-assertions",
                "-Xno-param-assertions"
        )
        jvmTarget = "11"
    }
}

deploy {
    targets {
        target("roborio", RoboRIO::class.java, closureOf<RoboRIO> {
            team = 865
        })
    }

    artifacts {
        artifact("frcJava", FRCJavaArtifact::class.java, closureOf<FRCJavaArtifact> {
            targets.add("roborio")
            debug = false
            jvmArgs = listOf("-XX:+UseG1GC")
        })
    }
}

buildDir = File(rootProject.projectDir, "build/" + project.name)

dependencies {
    // Kotlin Standard Library and Coroutines
    compile(kotlin("stdlib-jdk8"))

    // WPILib and Vendors
    wpi.deps.wpilib().forEach { compile(it) }
    wpi.deps.vendor.java().forEach { compile(it) }
    wpi.deps.vendor.jni(NativePlatforms.raspbian).forEach { nativeZip(it) }
    wpi.deps.vendor.jni(NativePlatforms.desktop).forEach { nativeDesktopZip(it) }

    simulation("edu.wpi.first.halsim:halsim_ds_socket:" +
            "${wpi.wpilibVersion}:${NativePlatforms.desktop}@zip")
}

tasks.jar {
    doFirst {
        from(configurations.compile.get().map {
            if (it.isDirectory) it else zipTree(it)
        })
        manifest {
            attributes("Main-Class" to "ca.warp7.frc2019.Astro")
        }
    }
}