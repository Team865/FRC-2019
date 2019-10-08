import org.gradle.internal.os.OperatingSystem

pluginManagement {
    repositories {
        mavenLocal()
        gradlePluginPortal()
        val frcYear = "2019"
        val frcHome: File
        if (OperatingSystem.current().isWindows) {
            val publicFolder = System.getenv("PUBLIC") ?: "C:\\Users\\Public"
            frcHome = File(publicFolder, "frc$frcYear")
        } else {
            val userFolder = System.getProperty("user.home")
            frcHome = File(userFolder, "frc$frcYear")
        }
        val frcHomeMaven = File(frcHome, "maven")
        maven {
            name = "frcHome"
            url = uri(frcHomeMaven)
        }
    }
}

include(":ActionJ")
project(":ActionJ").name = "ActionAPI-Java"

include(":ActionKt")
project(":ActionKt").name = "ActionDSL-Kotlin"

include(":Commons")
project(":Commons").name = "Robot-Commons"

include(":Robot")
project(":Robot").name = "Team865-Robot-2019"

include(":Test")
project(":Test").name = "Robot-Tests"

include(":PathPlanner")