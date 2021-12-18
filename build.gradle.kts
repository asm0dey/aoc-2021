plugins {
    kotlin("jvm") version "1.6.0"
}

repositories {
    mavenCentral()
}

dependencies {
    implementation("com.google.code.gson:gson:2.8.9")
    implementation("com.github.h0tk3y.betterParse:better-parse:0.4.3")
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin:2.13.+")
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
