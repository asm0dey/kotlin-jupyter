package build.util

import org.gradle.api.Project
import org.gradle.kotlin.dsl.maven
import org.gradle.kotlin.dsl.repositories

const val INTERNAL_TEAMCITY_URL = "https://buildserver.labs.intellij.net"
const val PUBLIC_TEAMCITY_URL = "https://teamcity.jetbrains.com"

class TeamcityProject(
    val url: String,
    val projectId: String
)

val INTERNAL_KOTLIN_TEAMCITY = TeamcityProject(INTERNAL_TEAMCITY_URL, "Kotlin_KotlinDev_Artifacts")
val PUBLIC_KOTLIN_TEAMCITY = TeamcityProject(PUBLIC_TEAMCITY_URL, "Kotlin_KotlinPublic_Artifacts")

const val TEAMCITY_REQUEST_ENDPOINT = "guestAuth/app/rest/builds"

fun Project.addAllBuildRepositories() {
    val kotlinVersion = rootProject.defaultVersionCatalog.versions.devKotlin

    repositories {
        mavenCentral()
        gradlePluginPortal()

        // Kotlin Dev releases are published here every night
        maven("https://maven.pkg.jetbrains.space/kotlin/p/kotlin/dev")

        for (teamcity in listOf(INTERNAL_KOTLIN_TEAMCITY, PUBLIC_KOTLIN_TEAMCITY)) {
            val locator = "buildType:(id:${teamcity.projectId}),number:$kotlinVersion,branch:default:any/artifacts/content/maven"
            maven("${teamcity.url}/$TEAMCITY_REQUEST_ENDPOINT/$locator")
        }

        // Used for TeamCity build
        val m2LocalPath = file(".m2/repository")
        if (m2LocalPath.exists()) {
            maven(m2LocalPath.toURI())
        }

        // Checking for local artifacts (should only be used during development)
        if (System.getenv("KOTLIN_JUPYTER_USE_MAVEN_LOCAL") != null) {
            mavenLocal()
        }
    }
}
