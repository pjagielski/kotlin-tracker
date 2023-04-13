plugins {
    id("kotlin")
}

repositories {
    mavenLocal()
    mavenCentral()
    maven("https://dl.bintray.com/punkt/punkt")
}

dependencies {
    implementation("pl.pjagielski", "punkt", "0.4.0-SNAPSHOT") {
        // from http4k
        exclude("org.slf4j", "slf4j-api")
        exclude("org.slf4j", "slf4j-log4j12")
    }
    implementation("ch.qos.logback:logback-classic:1.3.5")
}
