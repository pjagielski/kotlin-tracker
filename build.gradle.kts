plugins {
    kotlin("jvm") version "1.8.10"
}

val kotlinVersion: String by extra("1.8.10")

group = "pl.pjagielski"
version = "0.1.0-SNAPSHOT"

allprojects {
    repositories {
        mavenLocal()
        mavenCentral()
    }
}
