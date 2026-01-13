plugins {
    id("java-library")
}

val utf8 = Charsets.UTF_8.name()
val jdk = 8

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(jdk)
    }
}

dependencies {
    compileOnly(libs.lombok)
    annotationProcessor(libs.lombok)

    compileOnly(libs.jetbrains.annotations)
    annotationProcessor(libs.jetbrains.annotations)

    implementation(libs.jackson)
    api(libs.kafka)
}

tasks {
    withType<JavaCompile> {
        options.encoding = utf8
    }

    withType<ProcessResources> {
        filteringCharset = utf8
    }
}