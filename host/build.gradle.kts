plugins {
    id("kotlin")
}

val kotlinVersion: String by rootProject.extra

dependencies {
//    implementation(project(":script"))
    // needed for syntax highlighting
    implementation("pl.pjagielski:kotlin-tracker-script:0.1.0-SNAPSHOT")
    implementation("org.jetbrains.kotlin:kotlin-scripting-common:$kotlinVersion")
    implementation("org.jetbrains.kotlin:kotlin-scripting-jvm:$kotlinVersion")
    implementation("org.jetbrains.kotlin:kotlin-scripting-jvm-host:$kotlinVersion")
    implementation("org.jetbrains.kotlin:kotlin-scripting-dependencies:$kotlinVersion")
    implementation("io.github.microutils:kotlin-logging:3.0.4")
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin:2.14.1")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.6.3")
    implementation("ch.qos.logback:logback-classic:1.3.5")
}
