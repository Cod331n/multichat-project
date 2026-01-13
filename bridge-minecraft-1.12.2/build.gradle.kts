import java.time.Instant

plugins {
    alias(libs.plugins.shadow)
}

val projName = "multichat-1-12-2"
val utf8 = Charsets.UTF_8.name()
val jdk = 8

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
        url = uri("https://repo.c7x.dev/repository/maven-public/")
        credentials {
            username = System.getenv("CRI_REPO_LOGIN")
            password = System.getenv("CRI_REPO_PASSWORD")
        }
    }

}

tasks {
    withType<JavaCompile> {
        options.encoding = utf8
    }

    withType<ProcessResources> {
        filteringCharset = utf8
    }
}

dependencies {
    implementation(project(":bridge-core"))

    compileOnly(libs.lombok)
    annotationProcessor(libs.lombok)

    compileOnly(libs.jetbrains.annotations)
    annotationProcessor(libs.jetbrains.annotations)

    compileOnly(libs.spigot.annotations)
    annotationProcessor(libs.spigot.annotations)

    implementation(libs.jackson)

    compileOnly(libs.bukkit.api)
    compileOnly(libs.diamondpaper)

}

tasks {
    shadowJar {
        archiveBaseName.set(projName)
        archiveClassifier.set("")
        archiveVersion.set("")

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