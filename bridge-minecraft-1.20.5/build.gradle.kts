import org.gradle.kotlin.dsl.compileOnly
import java.time.Instant

plugins {
    alias(libs.plugins.shadow)
}

val projName = "multichat-1-20-5"
val utf8 = Charsets.UTF_8.name()
val jdk = 21

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(jdk)
    }
}

repositories {
    maven {
        url = uri("https://libraries.minecraft.net")
    }
    maven {
        name = "papermc"
        url = uri("https://repo.papermc.io/repository/maven-public/")
    }
    maven {
        url = uri("https://s01.oss.sonatype.org/content/repositories/snapshots/");
    }
}

dependencies {
    implementation(project(":bridge-core"))

    compileOnly(libs.lombok)
    annotationProcessor(libs.lombok)

    compileOnly(libs.jetbrains.annotations)
    annotationProcessor(libs.jetbrains.annotations)

    implementation(libs.jackson)

    implementation(libs.spigot.api)
    compileOnly(libs.luckperms.api)

    compileOnly(libs.spigot.annotations)
    annotationProcessor(libs.spigot.annotations)
}

tasks {
    withType<JavaCompile> {
        options.encoding = utf8
        options.release = jdk
    }

    withType<ProcessResources> {
        filteringCharset = utf8
    }
}

tasks {
    shadowJar {
        archiveBaseName.set(projName)
        archiveClassifier.set("")
        archiveVersion.set("")

        mergeServiceFiles()

        manifest {
            attributes(
                "Created-By" to project.ext.get("creator").toString(),
                "Implementation-Title" to projName,
                "Implementation-Version" to project.version,
                "Build-Timestamp" to Instant.now(),
                "Build-Jdk" to "${System.getProperty("java.version")} (${System.getProperty("java.vendor")})",
            )
        }
    }

    build {
        dependsOn(shadowJar)
    }
}