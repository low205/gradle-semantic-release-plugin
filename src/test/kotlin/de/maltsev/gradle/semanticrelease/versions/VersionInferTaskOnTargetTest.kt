package de.maltsev.gradle.semanticrelease.versions

import arrow.core.None
import arrow.core.toOption
import de.maltsev.gradle.semanticrelease.asCommit
import de.maltsev.gradle.semanticrelease.randomCommitId
import de.maltsev.gradle.semanticrelease.randomCommitMessage
import de.maltsev.gradle.semanticrelease.randomMajorMessage
import de.maltsev.gradle.semanticrelease.vcs.VCSSource
import de.maltsev.gradle.semanticrelease.vcs.VcsCommit
import de.maltsev.gradle.semanticrelease.vcs.VcsCommitId
import de.maltsev.gradle.semanticrelease.vcs.VcsRelease
import de.maltsev.gradle.semanticrelease.versions.VersionChangeGroup.MAJOR
import de.maltsev.gradle.semanticrelease.versions.VersionChangeGroup.MINOR
import de.maltsev.gradle.semanticrelease.versions.VersionChangeGroup.PATCH
import de.maltsev.gradle.semanticrelease.versions.VersionChangeGroup.values
import io.kotlintest.shouldBe
import io.kotlintest.shouldThrow
import io.kotlintest.specs.ShouldSpec
import io.mockk.every
import io.mockk.mockk

class VersionInferTaskOnTargetTest : ShouldSpec() {
    private val commitId = VcsCommitId("longHash", "shortHash")
    private val someRelease = VcsRelease(commitId, "v1.0.3")

    init {
        should("throw if latest found release is not semantic") {
            val vcs: VCSSource = mockk()
            val inferTask = VersionInferTaskOnTarget(
                vcs
            )

            every { vcs.latestRelease() } returns VcsRelease(commitId, "3.1.0.3").toOption()

            shouldThrow<IllegalStateException> {
                inferTask.getVersionContext()
            }
        }

        should("create first version with empty changes") {
            val vcs: VCSSource = mockk()
            val inferTask = VersionInferTaskOnTarget(
                vcs
            )

            every { vcs.latestRelease() } returns None

            val versionContext = inferTask.getVersionContext()
            versionContext.hasNewVersion shouldBe true
            versionContext.version shouldBe firstVersion
            versionContext.changes shouldBe emptyList()

            versionContext shouldBe VersionContext(
                hasNewVersion = true,
                version = firstVersion,
                changes = emptyList()
            )
        }

        should("create next version for major change") {
            val vcs: VCSSource = mockk()
            val majorCommit = VcsCommit(randomCommitId(), randomMajorMessage())
            val inferTask = VersionInferTaskOnTarget(
                vcs
            )

            every { vcs.latestRelease() } returns someRelease.toOption()
            every { vcs.commitsBefore(eq(commitId)) } returns listOf(majorCommit)

            val versionContext = inferTask.getVersionContext()

            versionContext.hasNewVersion shouldBe true
            versionContext.version shouldBe someRelease.version.asSemanticVersion().nextMajor()
            versionContext.changes shouldBe listOf(
                majorCommit.asVersionChange()
            )

            inferTask.getVersionContext() shouldBe VersionContext(
                hasNewVersion = true,
                version = someRelease.version.asSemanticVersion().nextMajor(),
                changes = listOf(
                    majorCommit.asVersionChange()
                )
            )
        }

        should("create next version for minor change") {
            val vcs: VCSSource = mockk()
            val minorCommit = VcsCommit(randomCommitId(), randomCommitMessage(MINOR))
            val inferTask = VersionInferTaskOnTarget(
                vcs
            )

            every { vcs.latestRelease() } returns someRelease.toOption()
            every { vcs.commitsBefore(eq(commitId)) } returns listOf(minorCommit)

            inferTask.getVersionContext() shouldBe VersionContext(
                hasNewVersion = true,
                version = someRelease.version.asSemanticVersion().nextMinor(),
                changes = listOf(
                    minorCommit.asVersionChange()
                )
            )
        }

        should("create next version for patch change") {
            val vcs: VCSSource = mockk()
            val patchCommit = VcsCommit(randomCommitId(), randomCommitMessage(PATCH))
            val inferTask = VersionInferTaskOnTarget(
                vcs
            )

            every { vcs.latestRelease() } returns someRelease.toOption()
            every { vcs.commitsBefore(eq(commitId)) } returns listOf(patchCommit)

            inferTask.getVersionContext() shouldBe VersionContext(
                hasNewVersion = true,
                version = someRelease.version.asSemanticVersion().nextPatch(),
                changes = listOf(
                    patchCommit.asVersionChange()
                )
            )
        }

        should("return old version for other changes") {
            val vcs: VCSSource = mockk()
            val commits = values()
                .filterNot { it in setOf(MAJOR, MINOR, PATCH) }
                .map { it.asCommit() }
            val inferTask = VersionInferTaskOnTarget(
                vcs
            )

            every { vcs.latestRelease() } returns someRelease.toOption()
            every { vcs.commitsBefore(eq(commitId)) } returns commits

            inferTask.getVersionContext() shouldBe VersionContext(
                hasNewVersion = false,
                version = someRelease.version.asSemanticVersion(),
                changes = commits.map { it.asVersionChange() }
            )
        }

        should("return old version for no changes") {
            val vcs: VCSSource = mockk()
            val inferTask = VersionInferTaskOnTarget(
                vcs
            )

            every { vcs.latestRelease() } returns someRelease.toOption()
            every { vcs.commitsBefore(eq(commitId)) } returns emptyList()

            inferTask.getVersionContext() shouldBe VersionContext(
                hasNewVersion = false,
                version = someRelease.version.asSemanticVersion(),
                changes = emptyList()
            )
        }
    }
}
