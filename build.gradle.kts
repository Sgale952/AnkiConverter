plugins {
    id("java")
    id("com.github.johnrengelman.shadow") version "8.1.1"
}

group = "github.sgale.ankiconverter"
version = "1.0"

repositories {
    mavenCentral()
}

dependencies {
    testImplementation(platform("org.junit:junit-bom:5.9.1"))
    testImplementation("org.junit.jupiter:junit-jupiter")

    implementation("net.bramp.ffmpeg:ffmpeg:0.8.0")
    implementation("com.github.gotson:webp-imageio:0.2.2")
}

tasks.jar {
    manifest {
        attributes["Main-Class"] = "github.sgale.Main"
    }
}

tasks.test {
    useJUnitPlatform()
}