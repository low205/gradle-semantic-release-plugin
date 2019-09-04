package de.maltsev.gradle.semanticrelease.versions

import de.maltsev.gradle.semanticrelease.randomCommitId
import de.maltsev.gradle.semanticrelease.vcs.VCSSource
import de.maltsev.gradle.semanticrelease.vcs.VcsCommit
import io.kotlintest.shouldBe
import io.kotlintest.specs.ShouldSpec
import io.mockk.every
import io.mockk.mockk

class VersionInferTaskOnBranchTest : ShouldSpec() {
    init {
        should("get version from lastCommit") {
            val vcs: VCSSource = mockk()
            val inferTask = VersionInferTaskOnBranch(
                "branch-name", vcs
            )
            val commitId = randomCommitId()
            every { vcs.lastCommit() } returns VcsCommit(commitId, "message")

            inferTask.getVersionContext() shouldBe VersionContext(
                hasNewVersion = true,
                version = BranchSemanticVersion(
                    "branch-name",
                    commitId.shortId
                ),
                changes = emptyList()
            )
        }
    }
}
