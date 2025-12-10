plugins {
    id("com.github.johnrengelman.shadow") version "8.1.1"
    id("edu.sc.seis.launch4j") version "3.0.3"
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

    testImplementation(platform("org.junit:junit-bom:5.10.0"))
    testImplementation("org.junit.jupiter:junit-jupiter")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
}

launch4j {
    mainClassName = "dev.cattyn.serialmonitorapp.Launcher" // Replace with your main class
    outfile = "serial-monitor.exe"

//    icon = 'src/main/resources/app_icon.ico' // Optional: path to your icon file
//    jrePath = 'jre' // Optional: path to a bundled JRE relative to the executable
    // ... other Launch4j configurations
}

application {
    mainClass = "dev.cattyn.serialmonitorapp.Launcher"
}

tasks.test {
    useJUnitPlatform()
}
