plugins {
    id("java")
}

group = "me.kalmemarq"
version = "0.2.0"

repositories {
    mavenCentral()
}

dependencies {
    testImplementation(platform("org.junit:junit-bom:5.10.0"))
    testImplementation("org.junit.jupiter:junit-jupiter")
}

tasks.withType<Jar> {
    manifest {
        attributes["Implementation-Title"] = project.name
        attributes["Implementation-Version"] = project.version
    }

    archiveBaseName = project.name
}

tasks.test {
    useJUnitPlatform()
}