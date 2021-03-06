package de.maltsev.gradle.semanticrelease.versions

import de.maltsev.gradle.semanticrelease.asChange
import de.maltsev.gradle.semanticrelease.versions.VersionChangeGroup.MAJOR
import de.maltsev.gradle.semanticrelease.versions.VersionChangeGroup.MINOR
import de.maltsev.gradle.semanticrelease.versions.VersionChangeGroup.PATCH
import de.maltsev.gradle.semanticrelease.versions.VersionChangeGroup.values
import io.kotlintest.assertions.arrow.option.shouldBeNone
import io.kotlintest.assertions.arrow.option.shouldBeSome
import io.kotlintest.shouldBe
import io.kotlintest.specs.ShouldSpec

class MasterSemanticVersionTest : ShouldSpec() {

    private val version = MasterSemanticVersion("v", 1, 2, 3)

    init {
        should("have proper artifact version") {
            val artifactVersion = version.artifactVersion()
            artifactVersion shouldBe "1.2.3"
            version.prefix shouldBe "v"
            version.major shouldBe 1
            version.minor shouldBe 2
            version.patch shouldBe 3
        }

        should("have vcs version with prefix") {
            val artifactVersion = version.vscString()
            artifactVersion shouldBe "v1.2.3"
        }

        should("increase major") {
            version.nextMajor().artifactVersion() shouldBe "2.0.0"
        }

        should("increase minor ") {
            version.nextMinor().artifactVersion() shouldBe "1.3.0"
        }

        should("increase patch") {
            version.nextPatch().artifactVersion() shouldBe "1.2.4"
        }

        should("change version for any MAJOR changes") {
            version.nextVersion(
                values().map { it.asChange() }
            ).map { it.artifactVersion() }.shouldBeSome("2.0.0")
        }

        should("change version for any MINOR changes") {
            version.nextVersion(
                values().filterNot { it in setOf(MAJOR) }.map { it.asChange() }
            ).map { it.artifactVersion() }.shouldBeSome("1.3.0")
        }

        should("change version for any PATCH changes") {
            version.nextVersion(
                values().filterNot { it in setOf(MAJOR, MINOR) }.map { it.asChange() }
            ).map { it.artifactVersion() }.shouldBeSome("1.2.4")
        }

        should("not have next  version for any other changes") {
            version.nextVersion(
                values().filterNot { it in setOf(MAJOR, MINOR, PATCH) }.map { it.asChange() }
            ).shouldBeNone()
        }
    }
}
