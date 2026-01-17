plugins {
    java
    id("com.github.johnrengelman.shadow") version "8.1.1"
}

group = providers.gradleProperty("group").get()
version = providers.gradleProperty("version").get()

tasks.wrapper {
    gradleVersion = "8.7"
}

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(21))
    }
}

repositories {
    mavenCentral()
    maven("https://repo.papermc.io/repository/maven-public/")
}

dependencies {
    compileOnly("io.papermc.paper:paper-api:1.21.1-R0.1-SNAPSHOT")
    implementation("org.xerial:sqlite-jdbc:3.45.3.0")
    implementation("com.fasterxml.jackson.core:jackson-databind:2.17.2")
}

tasks.processResources {
    filteringCharset = "UTF-8"
    filesMatching("plugin.yml") {
        expand(
            mapOf(
                "version" to project.version,
                "name" to project.name,
            )
        )
    }
}

tasks.shadowJar {
    archiveClassifier.set("")
}

tasks.build {
    dependsOn(tasks.shadowJar)
}

val exportJar = tasks.register<Copy>("exportJar") {
    dependsOn(tasks.shadowJar)

    val shadow = tasks.shadowJar.get().archiveFile
    from(shadow)

    into(layout.projectDirectory.dir("data/plugins"))
    rename { _ -> "pcrykh.jar" }
}

// Always-fresh guarantee for Docker Compose: `gradle clean exportJar`
tasks.register("cleanExport") {
    dependsOn(tasks.clean)
    dependsOn(exportJar)
}
