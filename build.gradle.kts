group = "github.sgale.ankiconverter"
version = "1.8"

plugins {
    id("java")
    id("com.github.johnrengelman.shadow") version "8.1.1"
}

repositories {
    mavenCentral()
}

dependencies {
    testImplementation(platform("org.junit:junit-bom:5.9.1"))
    testImplementation("org.junit.jupiter:junit-jupiter")

    implementation("net.bramp.ffmpeg:ffmpeg:0.8.0")
    implementation("com.github.gotson:webp-imageio:0.2.2")
    implementation("com.deepl.api:deepl-java:1.5.0")

    implementation("com.google.code.gson:gson:2.10.1")
    implementation("org.jsoup:jsoup:1.17.2")

    implementation("org.apache.logging.log4j:log4j-core:2.23.1")
}

tasks.jar {
    manifest {
        attributes["Main-Class"] = "github.sgale.ankiConverter.Main"
    }
}

tasks.test {
    useJUnitPlatform()
}