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
        exclude("com.oracle.xml", "xmlparserv2")
    }
    implementation("ch.qos.logback:logback-classic:1.5.13")
    implementation("xerces:xercesImpl:2.12.2")
}
