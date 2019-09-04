package de.maltsev.gradle.semanticrelease.versions

import io.kotlintest.matchers.types.shouldNotBeNull
import io.kotlintest.specs.ShouldSpec
import io.kotlintest.shouldBe

class VersionsTest : ShouldSpec() {

    init {
        should("be expected") {
            PREFIX_GROUP_NAME shouldBe "prefix"
            VERSION_PREFIX shouldBe "v"
            MAJOR_GROUP_NAME shouldBe "major"
            MINOR_GROUP_NAME shouldBe "minor"
            PATCH_GROUP_NAME shouldBe "patch"
        }

        should("have patterns") {
            changeMessagePattern.shouldNotBeNull()
            breakingMessagePattern.shouldNotBeNull()
        }
    }
}
