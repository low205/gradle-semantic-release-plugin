import io.gitlab.arturbosch.detekt.detekt
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm")
    kotlin("kapt")
    id("io.gitlab.arturbosch.detekt")
    id("org.jetbrains.dokka")
    jacoco
}

repositories {
    mavenCentral()
    jcenter()
    gradlePluginPortal()
}

apply {
    from("${rootProject.rootDir}/properties.gradle.kts")
}

val ktlint: Configuration = configurations.create("ktlint")

val kotlinVersion: String by extra
val mockkVersion: String by extra
val kotlinTestVersion: String by extra
val arrowVersion: String by extra
val ktlintVersion: String by extra
val fuelVersion: String by extra
val koinVersion: String by extra
val arrowCoreExtVersion: String by extra

dependencies {
    implementation(kotlin("stdlib-jdk8", kotlinVersion))
    implementation(kotlin("stdlib-jdk7", kotlinVersion))
    implementation(kotlin("stdlib", kotlinVersion))
    implementation(kotlin("reflect", kotlinVersion))
    implementation("com.github.kittinunf.fuel:fuel:$fuelVersion")
    implementation("com.github.kittinunf.fuel:fuel-jackson:$fuelVersion")
    implementation("io.arrow-kt:arrow-core:$arrowVersion")
    implementation("io.arrow-kt:arrow-core-extensions:$arrowCoreExtVersion")
    implementation("org.koin:koin-core:$koinVersion")

    testImplementation("io.mockk:mockk:$mockkVersion")
    testImplementation("io.kotlintest:kotlintest-runner-junit5:$kotlinTestVersion")
    testImplementation("io.kotlintest:kotlintest-assertions:$kotlinTestVersion")
    testImplementation("io.kotlintest:kotlintest-assertions-arrow:$kotlinTestVersion")

    ktlint("com.github.shyiko:ktlint:$ktlintVersion")
}

tasks {
    withType<KotlinCompile> {
        kotlinOptions {
            jvmTarget = JavaVersion.VERSION_1_8.toString()
            freeCompilerArgs = freeCompilerArgs + listOf(
                "-java-parameters",
                "-Xjsr305=strict",
                "-progressive")
        }
    }

    val ktlint by creating(JavaExec::class) {
        group = "verification"
        description = "Check Kotlin code style."
        classpath = configurations["ktlint"]
        main = "com.github.shyiko.ktlint.Main"
        args = listOf("src/**/*.kt")
    }

    "check" {
        dependsOn(ktlint)
    }

    create("ktlintFormat", JavaExec::class) {
        group = "formatting"
        description = "Fix Kotlin code style deviations."
        classpath = configurations["ktlint"]
        main = "com.github.shyiko.ktlint.Main"
        args = listOf("-F", "src/**/*.kt")
    }

    withType<Test> {
        useJUnitPlatform()
    }
}

val detektVersion: String by project

detekt {
    toolVersion = detektVersion
    input = files("$rootDir/src/main/kotlin")
    config = files("$rootDir/detekt-config.yml")
    filters = ".*/resources/.*,.*/build/.*"
}
