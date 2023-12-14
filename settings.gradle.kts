@file:Suppress("UnstableApiUsage", "KDocMissingDocumentation")

rootProject.name = "lavakord"
include(
    "example",
    "kord", // GitHub Actions gets mad about this and I can't reproduce this locally
    "jsExample",
    "core",
    ":plugins:kspProcessor",
    ":plugins:sponsorblock",
    ":plugins:lavasrc",
    ":plugins:lavasearch",
    "java",
    "jda",
    "jda-java"
)

enableFeaturePreview("TYPESAFE_PROJECT_ACCESSORS")

pluginManagement {
    resolutionStrategy {
        repositories {
            gradlePluginPortal()
        }

        eachPlugin {
            if (requested.id.id == "kotlinx-atomicfu") {
                useModule("org.jetbrains.kotlinx:atomicfu-gradle-plugin:${requested.version}")
            }
        }
    }
}

dependencyResolutionManagement {
    versionCatalogs {
        create("libs") {
            val codegen = version("codegen", "main-SNAPSHOT")
            kotlinx()
            ktor()
            ksp()
            library("kord-core", "dev.kord", "kord-core").version("0.12.0")
            library(
                "kord-ksp-annotations",
                "dev.kord",
                "kord-ksp-annotations"
            ).version("feature-publish-processor-SNAPSHOT")
            library(
                "kord-ksp-processors",
                "dev.kord",
                "kord-ksp-processors"
            ).version("feature-publish-processor-SNAPSHOT")
            library("junit-jupiter-engine", "org.junit.jupiter", "junit-jupiter-engine").version("5.10.1")
            library("kotlinlogging", "io.github.microutils", "kotlin-logging").version("3.0.5")
            library("sl4fj-simple", "org.slf4j", "slf4j-simple").version("2.0.7")

            library("kotlinx-nodejs", "org.jetbrains.kotlin-wrappers", "kotlin-node").version("18.16.12-pre.594")

            library("lavalink-protocol", "dev.arbjerg.lavalink", "protocol").version("4.0.0")
            library(
                "lavasearch-protocol",
                "com.github.topi314.lavasearch",
                "lavasearch-protocol"
            ).version("1.0.0")
            library(
                "lavasrc-protocol",
                "com.github.topi314.lavasrc",
                "protocol"
            ).version("4.0.0")

            library("kotlinpoet", "com.squareup", "kotlinpoet-ksp").version("1.15.2")

            library("codegen", "dev.kord.codegen", "kotlinpoet").versionRef(codegen)
            library("codegen-ksp", "dev.kord.codegen", "ksp").versionRef(codegen)
            library("codegen-ksp-annotations", "dev.kord.codegen", "ksp-annotations").versionRef(codegen)
            library("codegen-ksp-processor", "dev.kord.codegen", "ksp-processor").versionRef(codegen)

            plugin("kotlinx-atomicfu", "kotlinx-atomicfu").version("0.23.1")
            plugin("git-publish", "org.ajoberstar.git-publish").version("4.2.1")
        }
    }
}

fun VersionCatalogBuilder.kotlinx() {
    val coroutines = version("coroutines", "1.7.3")
    library("kotlinx-coroutines-core", "org.jetbrains.kotlinx", "kotlinx-coroutines-core").versionRef(coroutines)
    library("kotlinx-coroutines-jdk8", "org.jetbrains.kotlinx", "kotlinx-coroutines-jdk8").versionRef(coroutines)
    library("kotlinx-coroutines-jdk9", "org.jetbrains.kotlinx", "kotlinx-coroutines-jdk9").versionRef(coroutines)
    library("kotlinx-coroutines-test", "org.jetbrains.kotlinx", "kotlinx-coroutines-test").versionRef(coroutines)
    library("kotlinx-serialization-json", "org.jetbrains.kotlinx", "kotlinx-serialization-json").version("1.5.0")
    library("kotlinx-datetime", "org.jetbrains.kotlinx", "kotlinx-datetime").version("0.4.0")
}

fun VersionCatalogBuilder.ktor() {
    val ktor = version("ktor", "2.3.6")
    library("ktor-io", "io.ktor", "ktor-io").versionRef(ktor)
    library("ktor-utils", "io.ktor", "ktor-utils").versionRef(ktor)
    library("ktor-client-websockets", "io.ktor", "ktor-client-websockets").versionRef(ktor)
    library("ktor-client-core", "io.ktor", "ktor-client-core").versionRef(ktor)
    library("ktor-client-resources", "io.ktor", "ktor-client-resources").versionRef(ktor)
    library("ktor-serialization-kotlinx-json", "io.ktor", "ktor-serialization-kotlinx-json").versionRef(ktor)
    library("ktor-client-content-negotiation", "io.ktor", "ktor-client-content-negotiation").versionRef(ktor)
    library("ktor-client-logging", "io.ktor", "ktor-client-logging").versionRef(ktor)
    library("ktor-client-cio", "io.ktor", "ktor-client-cio").versionRef(ktor)
    library("ktor-client-js", "io.ktor", "ktor-client-js").versionRef(ktor)
    library("ktor-client-mock", "io.ktor", "ktor-client-mock").versionRef(ktor)
}

fun VersionCatalogBuilder.ksp() {
    val ksp = version("ksp", "1.9.21-1.0.15")
    library("ksp-api", "com.google.devtools.ksp", "symbol-processing-api").versionRef(ksp)
    plugin("ksp", "com.google.devtools.ksp").versionRef(ksp)
}
