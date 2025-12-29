plugins {
    kotlin("jvm") version "2.3.0"
}

val kotlinVersion: String by extra("2.3.0")

group = "pl.pjagielski"
version = "0.1.0-SNAPSHOT"

allprojects {
    repositories {
        mavenLocal()
        mavenCentral()
    }
}
