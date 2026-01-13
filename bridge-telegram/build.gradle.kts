import java.time.Instant

plugins {
    alias(libs.plugins.shadow)
}

val projName = "telegram-bot"
val utf8 = Charsets.UTF_8.name()
val jdk = 21

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(jdk)
    }
}

dependencies {
    implementation(project(":bridge-core"))

    compileOnly(libs.lombok)
    annotationProcessor(libs.lombok)

    compileOnly(libs.jetbrains.annotations)
    annotationProcessor(libs.jetbrains.annotations)

    implementation(libs.jackson)
    implementation(libs.kafka)

    implementation(libs.telegrambots)
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

        manifest {
            attributes(
                "Main-Class" to "ru.geoderma.multichat.BotApplication",
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