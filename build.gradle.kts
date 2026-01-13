plugins {
    id("java")
}

allprojects {
    group = "ru.geoderma.multichat"
    version = "1.0.0"

    project.ext.set("creator", "GeoDerma");
}

subprojects {
    repositories {
        mavenCentral()
    }

    arrayOf("java").forEach {
        apply(plugin = it)
    }
}