import com.jfrog.bintray.gradle.BintrayExtension
import com.jfrog.bintray.gradle.tasks.BintrayUploadTask
import org.gradle.api.tasks.testing.logging.TestExceptionFormat
import org.gradle.api.tasks.testing.logging.TestLogEvent
import java.util.Date

plugins {
    `java-gradle-plugin`
    `maven-publish`
    `common-library`
    id("com.gradle.plugin-publish")
    id("com.jfrog.bintray")
    id("de.maltsev.gradle.semanticrelease")
}

group = "de.maltsev"

gradlePlugin {
    plugins {
        create("semanticReleasePlugin") {
            id = "de.maltsev.gradle.semanticrelease"
            displayName = "Gradle semantic release plugin"
            description = "Automated release version evaluation based on commit messages for Travis and GitHub"
            implementationClass = "de.maltsev.gradle.semanticrelease.SemanticReleasePlugin"
        }
    }
}

tasks.test {
    useJUnitPlatform()
    testLogging {
        // set options for log level LIFECYCLE
        events = setOf(
            TestLogEvent.FAILED,
            TestLogEvent.PASSED,
            TestLogEvent.SKIPPED,
            TestLogEvent.STANDARD_OUT
        )
        exceptionFormat = TestExceptionFormat.FULL
        showExceptions = true
        showCauses = true
        showStackTraces = true
    }
}

tasks.validateTaskProperties {
    enableStricterValidation = true
}

pluginBundle {
    website = "https://github.com/low205/gradle-semantic-release-plugin"
    vcsUrl = "https://github.com/low205/gradle-semantic-release-plugin"
    description = "Gradle semantic release plugin"
    tags = listOf("kotlin", "semantic-release", "travis", "github")

    (plugins) {
        "semanticReleasePlugin" {
            id = "de.maltsev.gradle.semanticrelease"
            displayName = "Gradle semantic release plugin"
        }
    }
}

//tasks.dokka {
//    reportUndocumented = false
//    outputFormat = "javadoc"
//    outputDirectory = "$buildDir/javadoc"
//}

sourceSets["main"].java.srcDir("$buildDir/generated/src")

val sourcesJar by tasks.creating(Jar::class) {
    dependsOn("classes")
    archiveClassifier.set("sources")
    from(sourceSets["main"].allSource)
}

//val javadocJar by tasks.creating(Jar::class) {
//    dependsOn("dokka")
//    archiveClassifier.set("javadoc")
//    from(buildDir.resolve("javadoc"))
//}

artifacts {
    archives(sourcesJar)
//    archives(javadocJar)
}

publishing {
    publications.create<MavenPublication>("SemanticReleasePublication") {
        from(components["java"])
        artifact(sourcesJar)
//        artifact(javadocJar)
        groupId = rootProject.group.toString()
        artifactId = rootProject.name
        version = rootProject.version.toString()
        pom.withXml {
            asNode().apply {
                appendNode("description", "Gradle semantic release plugin")
                appendNode("name", "semanticrelease")
                appendNode("url", "https://github.com/low205/gradle-semantic-release-plugin")

                val license = appendNode("licenses").appendNode("license")
                license.appendNode("name", "MIT License")
                license.appendNode("url", "https://github.com/low205/gradle-semantic-release-plugin/blob/master/LICENSE")
                license.appendNode("distribution", "repo")

                val developer = appendNode("developers").appendNode("developer")
                developer.appendNode("id", "Evgeniy Maltsev")
                developer.appendNode("name", "Evgeniy Maltsev")
                developer.appendNode("email", "low205@gmail.com")

                appendNode("scm").appendNode("url", "https://github.com/low205/gradle-semantic-release-plugin")
            }
        }
    }
}

bintray {
    user = System.getenv("BINTRAY_USER") ?: ""
    key = System.getenv("BINTRAY_API_KEY") ?: ""
    setPublications("SemanticReleasePublication")
    override = true
    pkg(delegateClosureOf<BintrayExtension.PackageConfig> {
        repo = "gradle-plugins"
        name = "gradle-semantic-release-plugin"
        userOrg = "low205"
        setLicenses("MIT")
        vcsUrl = "https://github.com/low205/gradle-semantic-release-plugin"
        version(delegateClosureOf<BintrayExtension.VersionConfig> {
            name = project.version.toString()
            released = Date().toString()

            gpg(delegateClosureOf<BintrayExtension.GpgConfig> {
                sign = true
            })
        })
    })
}

tasks {
    "bintrayUpload"(BintrayUploadTask::class) {
        onlyIf { version != "unspecified" && project.hasProperty("semanticVersion")}
    }
    "publishPlugins" {
        onlyIf { version != "unspecified" && project.hasProperty("semanticVersion")}
    }
}
