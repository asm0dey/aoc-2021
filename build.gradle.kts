plugins {
    kotlin("jvm") version "1.6.0"
}

repositories {
    mavenCentral()
    maven { setUrl("https://maven.pkg.jetbrains.space/public/p/kotlinx-html/maven") }
    maven { setUrl("https://dl.bintray.com/arrow-kt/arrow-kt/") }
}

dependencies {
    implementation("com.google.code.gson:gson:2.8.9")
    implementation("com.github.h0tk3y.betterParse:better-parse:0.4.3")
    implementation("org.jetbrains.kotlinx:kotlinx-html-jvm:0.7.3")
    implementation ("io.arrow-kt:arrow-syntax:0.10.4")
}

tasks {
    sourceSets {
        main {
            java.srcDirs("src")
        }
    }

    wrapper {
        gradleVersion = "7.3"
    }
}
