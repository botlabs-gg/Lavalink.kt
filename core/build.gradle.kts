import com.vanniktech.maven.publish.JavadocJar
import com.vanniktech.maven.publish.KotlinMultiplatform
import java.net.URI

plugins {
    `lavalink-module`
    `lavalink-publishing`
    kotlin("plugin.serialization")
    id("kotlinx-atomicfu")
    id("maven-publish")
}

kotlin {
    sourceSets {
        all {
            languageSettings.optIn("kotlinx.coroutines.ExperimentalCoroutinesApi")
        }
        commonMain {
            dependencies {
                api(libs.kotlinx.coroutines.core)
                api(libs.kotlinx.serialization.json)
                api(libs.kotlinx.datetime)
                api(libs.lavalink.protocol)

                implementation(libs.ktor.io)
                implementation(libs.ktor.utils)
                implementation(libs.ktor.client.websockets)
                api(libs.ktor.client.core)
                implementation(libs.ktor.client.resources)
                implementation(libs.ktor.serialization.kotlinx.json)
                implementation(libs.ktor.client.content.negotiation)
                implementation(libs.ktor.client.logging)

                implementation(libs.kotlinlogging)
            }
        }

        commonTest {
            dependencies {
                implementation(kotlin("test-common"))
                implementation(kotlin("test-annotations-common"))
                implementation(libs.ktor.client.mock)
                implementation(libs.kotlinx.coroutines.test)
            }
        }

        jvmMain {
            dependencies {
                implementation(libs.ktor.client.cio)
            }
        }

        jvmTest {
            dependencies {
                implementation(kotlin("test-junit5"))
                runtimeOnly(libs.junit.jupiter.engine)
                runtimeOnly(libs.sl4fj.simple)
            }
        }

        jsMain {
            dependencies {
                implementation(libs.ktor.client.js)
            }
        }

        jsTest {
            dependencies {
                implementation(kotlin("test-js"))
            }
        }
    }
}

mavenPublishing {
    configure(KotlinMultiplatform(JavadocJar.Dokka("dokkaHtml")))
}

publishing {
    repositories {
        maven {
            name = "GitHubPackages"
            url = URI("https://maven.pkg.github.com/botlabs-gg/lavalink.kt")
            credentials {
                username = System.getenv("GITHUB_ACTOR")
                password = System.getenv("GITHUB_TOKEN")
            }
        }
    }
}
