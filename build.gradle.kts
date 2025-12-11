plugins {
    id("com.github.johnrengelman.shadow") version "8.1.1"
    id("java")
    id("application")
}

group = "dev.cattyn"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    implementation("com.fazecast:jSerialComm:[2.0.0,3.0.0)")
    implementation("com.profesorfalken:jSensors:2.2.1")
}

application {
    mainClass = "dev.cattyn.serialmonitorapp.Launcher"
}

tasks.test {
    useJUnitPlatform()
}
