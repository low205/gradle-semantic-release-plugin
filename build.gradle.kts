import com.jfrog.bintray.gradle.BintrayExtension
import org.gradle.api.tasks.testing.logging.TestExceptionFormat
import org.gradle.api.tasks.testing.logging.TestLogEvent
import java.util.Date

plugins {
    `java-gradle-plugin`
    `maven-publish`
    `common-library`
    id("com.gradle.plugin-publish")
    id("com.jfrog.bintray")
}

group = "de.maltsev.gradle.semanticrelease"
version = "0.1.0"

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

tasks.dokka {
    // suppresses undocumented classes but not dokka warnings
    // https://github.com/Kotlin/dokka/issues/229 && https://github.com/Kotlin/dokka/issues/319
    reportUndocumented = false
    outputFormat = "javadoc"
    outputDirectory = "$buildDir/javadoc"
}

sourceSets["main"].java.srcDir("$buildDir/generated/src")

val sourcesJar by tasks.creating(Jar::class) {
    dependsOn("classes")
    archiveClassifier.set("sources")
    from(sourceSets["main"].allSource)
}

val javadocJar by tasks.creating(Jar::class) {
    dependsOn("dokka")
    archiveClassifier.set("javadoc")
    from(buildDir.resolve("javadoc"))
}

artifacts {
    archives(sourcesJar)
    archives(javadocJar)
}

publishing {
    publications.create<MavenPublication>("SemanticReleasePublication") {
        from(components["java"])
        artifact(sourcesJar)
        artifact(javadocJar)
        groupId = rootProject.group as? String
        artifactId = rootProject.name
        version = rootProject.version as? String
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
        setLicenses("Apache-2.0", "MIT")
        vcsUrl = "https://github.com/low205/gradle-semantic-release-plugin"
        version(delegateClosureOf<BintrayExtension.VersionConfig> {
            name = project.version as? String
            released = Date().toString()

            gpg(delegateClosureOf<BintrayExtension.GpgConfig> {
                sign = true
            })
        })
    })
}

tasks {
    "bintrayUpload" {
//        dependsOn("semanticReleasePublish")
        onlyIf { version != "unspecified" }
    }
    "publishPlugins" {
        //        dependsOn("semanticReleasePublish")
        onlyIf { version != "unspecified" }
    }
}
