plugins {
    id("kotlin")
    `java-library`
    `maven-publish`
}

val kotlinVersion: String by rootProject.extra

dependencies {
    implementation("org.jetbrains.kotlin:kotlin-scripting-common:$kotlinVersion")
    implementation("org.jetbrains.kotlin:kotlin-scripting-jvm:$kotlinVersion")
    implementation("org.jetbrains.kotlin:kotlin-scripting-dependencies:$kotlinVersion")
    implementation("io.github.microutils:kotlin-logging:3.0.4")
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin:2.14.1")
}

java {
    withSourcesJar()
}

publishing {
    publications {
        create<MavenPublication>("maven") {
            groupId = "${rootProject.group}"
            artifactId = "kotlin-tracker-${project.name}"
            version = "${rootProject.version}"
            from(components["java"])
        }
    }
}
