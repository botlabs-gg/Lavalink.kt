import com.palantir.gradle.gitversion.GitVersionPlugin
import com.palantir.gradle.gitversion.VersionDetails
import groovy.lang.Closure
import org.ajoberstar.gradle.git.publish.GitPublishExtension
import org.jetbrains.dokka.gradle.DokkaTask
import org.jetbrains.kotlin.gradle.utils.`is`

@Suppress("DSL_SCOPE_VIOLATION")
plugins {
    id("org.jetbrains.dokka")
    alias(libs.plugins.kotlinx.atomicfu) apply false
    alias(libs.plugins.git.publish)
    alias(libs.plugins.git.version) apply false
}

group = "dev.schlaubi.lavakord"
version = "5.1.7"

allprojects {
    repositories {
        mavenCentral()
        maven("https://maven.pkg.jetbrains.space/public/p/kotlinx-html/maven")
        maven("https://oss.sonatype.org/content/repositories/snapshots")
        maven("https://maven.topi.wtf/snapshots")
    }
}

tasks {
    dokkaHtmlMultiModule {
        outputDirectory = rootProject.file("docs")
    }

    gitPublishCopy {
        dependsOn(dokkaHtmlMultiModule)
    }
}

configure<GitPublishExtension> {
    repoUri = "https://github.com/DRSchlaubi/lavakord.git"
    branch = "gh-pages"

    contents {
        from(file("docs"))
        from(file("CNAME"))
    }

    commitMessage = "Update Docs"
}

subprojects {
    apply<GitVersionPlugin>()
    val versionDetails: Closure<VersionDetails> by extra
    val details = versionDetails()
    version = if(details.isCleanTag) {
        details.version
    } else {
        details.branchName + "-SNAPSHOT"
    }
    group = rootProject.group

    tasks {
        withType<DokkaTask>().configureEach {
            dokkaSourceSets {
                configureEach {
                    includeNonPublic = false

                    perPackageOption {
                        matchingRegex = ".*\\.internal.*" // will match all .internal packages and sub-packages
                        suppress = true
                    }
                }
            }
        }
    }
}
