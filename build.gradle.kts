plugins {
    kotlin("jvm") version "1.5.10"
}

plugins.apply("artemis")

group = "org.example"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
    maven("https://maven.pkg.jetbrains.space/public/p/compose/dev")
}

buildscript {
    repositories {
        mavenCentral()
    }
    dependencies {
        classpath("net.onedaybeard.artemis:artemis-odb-gradle-plugin:2.3.0")
    }
}

tasks.named("weave", net.onedaybeard.gradle.ArtemisWeavingTask::class) {
    classesDirs = sourceSets.main.get().output.classesDirs
    isEnableArtemisPlugin = true
    isEnablePooledWeaving = true
    isGenerateLinkMutators = true
    isOptimizeEntitySystems = true
}
tasks.build {
    finalizedBy(tasks.named("weave"))
}

val osName = System.getProperty("os.name")
val targetOs = when {
    osName == "Mac OS X" -> "macos"
    osName.startsWith("Win") -> "windows"
    osName.startsWith("Linux") -> "linux"
    else -> error("Unsupported OS: $osName")
}

val osArch = System.getProperty("os.arch")
var targetArch = when (osArch) {
    "x86_64", "amd64" -> "x64"
    "aarch64" -> "arm64"
    else -> error("Unsupported arch: $osArch")
}

val skikoVersion = "0.7.8"
val target = "${targetOs}-${targetArch}"
dependencies {
    implementation(kotlin("stdlib"))
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.5.10")

    implementation("net.onedaybeard.artemis:artemis-odb:2.3.0")
    implementation("net.onedaybeard.artemis:artemis-odb-serializer-json:2.3.0")
    implementation("net.mostlyoriginal.artemis-odb:contrib-plugin-singleton:2.5.0")

    implementation("com.esotericsoftware:kryo:5.3.0")

    implementation("io.github.classgraph:classgraph:4.8.141")

    implementation("org.jetbrains.skiko:skiko-awt-runtime-$target:$skikoVersion")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-swing:1.6.0")
}
