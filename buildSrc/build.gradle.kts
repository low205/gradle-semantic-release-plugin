plugins {
    `kotlin-dsl`
}

repositories {
    mavenCentral()
    jcenter()
    gradlePluginPortal()
}

apply {
    from("${rootDir.parentFile}/properties.gradle.kts")
}

val kotlinVersion: String by extra
val detektVersion: String by extra
val gradlePublishVersion: String by extra
val dokkaVersion: String by extra
val bintrayVersion: String by extra
val semanticReleaseVersion: String by extra

dependencies {
    compile(kotlin("gradle-plugin", version = kotlinVersion))
    compile(kotlin("stdlib-jdk8", version = kotlinVersion))
    compile(kotlin("reflect", version = kotlinVersion))
    compile("io.gitlab.arturbosch.detekt:detekt-gradle-plugin:$detektVersion")
    compile("com.gradle.publish:plugin-publish-plugin:$gradlePublishVersion")
    compile("org.jetbrains.dokka:dokka-gradle-plugin:$dokkaVersion")
    compile("com.jfrog.bintray.gradle:gradle-bintray-plugin:$bintrayVersion")
    compile("de.maltsev:gradle-semantic-release-plugin:$semanticReleaseVersion")
}
