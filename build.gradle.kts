group = "github.sgale.ankiconverter"
version = "2.0"

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

    implementation("com.github.gotson:webp-imageio:0.2.2")
    implementation("com.deepl.api:deepl-java:1.5.0")

    implementation("com.google.code.gson:gson:2.10.1")
    implementation("org.jsoup:jsoup:1.17.2")

    implementation("org.apache.logging.log4j:log4j-core:2.23.1")
}

tasks.test {
    useJUnitPlatform()
}

tasks.jar {
    manifest {
        attributes["Project-Version"] = version
        attributes["Main-Class"] = "github.sgale.ankiConverter.Main"
    }
}

tasks.named("clean") {
    doLast {
        delete("ankiConverter.log", "ankiConverter.properties")
    }
}