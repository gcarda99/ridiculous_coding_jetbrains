plugins {
    id("java")
    id("org.jetbrains.intellij.platform") version "2.3.0"
}

group = "com.gcarda99"
version = "0.1.0"

repositories {
    mavenCentral()
    intellijPlatform {
        defaultRepositories()
    }
}

dependencies {
    intellijPlatform {
        intellijIdeaCommunity("2024.3")
        bundledPlugin("com.intellij.java")
    }
}

intellijPlatform {
    pluginConfiguration {
        name = "Ridiculous Coding"
        version = "0.1.0"
        description = "Makes your coding experience ridiculous with visual keystroke effects."
        changeNotes = "Initial release."
        ideaVersion {
            sinceBuild = "243"
        }
    }

    signing {
        // Configure signing for Marketplace publishing
    }

    publishing {
        // Configure for Marketplace publishing
    }
}
