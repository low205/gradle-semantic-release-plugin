package de.maltsev.gradle.semanticrelease.versions

import io.kotlintest.shouldBe
import io.kotlintest.specs.ShouldSpec

class BranchSemanticVersionTest : ShouldSpec() {

    init {
        should("use branch name and commitHash for vcs version") {
            BranchSemanticVersion(
                "branch", "commitHash"
            ).vscString() shouldBe "branch.commitHash"
        }

        should("have same artifactVersion as vcsVersion") {
            val version = BranchSemanticVersion(
                "branch", "commitHash"
            )
            version.artifactVersion() shouldBe version.vscString()
        }
    }

}
