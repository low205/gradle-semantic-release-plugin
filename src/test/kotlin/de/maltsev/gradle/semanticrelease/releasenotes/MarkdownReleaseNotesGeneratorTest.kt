package de.maltsev.gradle.semanticrelease.releasenotes

import arrow.core.Some
import de.maltsev.gradle.semanticrelease.releasenotes.MarkdownReleaseNotesGenerator.generate
import de.maltsev.gradle.semanticrelease.vcs.CommitMessageDescriptor
import de.maltsev.gradle.semanticrelease.vcs.VcsCommitId
import de.maltsev.gradle.semanticrelease.versions.MajorChange
import de.maltsev.gradle.semanticrelease.versions.MinorChange
import de.maltsev.gradle.semanticrelease.versions.PatchChange
import de.maltsev.gradle.semanticrelease.versions.SemanticVersion
import de.maltsev.gradle.semanticrelease.versions.VersionChange
import io.kotlintest.shouldBe
import io.kotlintest.specs.ShouldSpec
import java.time.LocalDate

class MarkdownReleaseNotesGeneratorTest : ShouldSpec() {

    init {
        should("sort changes by priority") {
            val message = generate(SemanticVersion("v", 1, 1, 1), listOf(
                VersionChange.of(CommitMessageDescriptor("change 1"), VcsCommitId("1"), "other1"),
                VersionChange.of(CommitMessageDescriptor("change 2"), VcsCommitId("2"), "style"),
                PatchChange(CommitMessageDescriptor("change 3"), VcsCommitId("3")),
                VersionChange.of(CommitMessageDescriptor("change 4"), VcsCommitId("4"), "refactor"),
                MinorChange(CommitMessageDescriptor("change 5"), VcsCommitId("5")),
                VersionChange.of(CommitMessageDescriptor("change 6"), VcsCommitId("6"), "test"),
                MajorChange(CommitMessageDescriptor("change 7", type = Some("breaking change"), subType = Some("subType"), breakingChangeMessage = Some("broke something")), VcsCommitId("7")),
                VersionChange.of(CommitMessageDescriptor("change 8"), VcsCommitId("8"), "chore"),
                VersionChange.of(CommitMessageDescriptor("change 9"), VcsCommitId("9"), "revert"),
                VersionChange.of(CommitMessageDescriptor("change 10"), VcsCommitId("10"), "docs"),
                VersionChange.of(CommitMessageDescriptor("change 11"), VcsCommitId("11"), "other2"),
                VersionChange.of(CommitMessageDescriptor("change 12"), VcsCommitId("12"), "perf"),
                VersionChange.of(CommitMessageDescriptor("change 13"), VcsCommitId("13"), "other3")
            ))

            message shouldBe """|## 1.1.1 (${LocalDate.now()})
                                |
                                |#### Breaking Changes
                                |
                                |* **subType**: change 7 (7)
                                |${'\t'}```BREAKING CHANGE: broke something```
                                |
                                |#### Features
                                |
                                |*  change 5 (5)
                                |
                                |#### Bug Fixes
                                |
                                |*  change 3 (3)
                                |
                                |#### Performance Improvements
                                |
                                |*  change 12 (12)
                                |
                                |#### Reverts
                                |
                                |*  change 9 (9)
                                |
                                |#### Documentation
                                |
                                |*  change 10 (10)
                                |
                                |#### Style
                                |
                                |*  change 2 (2)
                                |
                                |#### Code Refactoring
                                |
                                |*  change 4 (4)
                                |
                                |#### Tests
                                |
                                |*  change 6 (6)
                                |
                                |#### Chores
                                |
                                |*  change 8 (8)
                                |
                                |#### Others
                                |
                                |*  change 1 (1)
                                |*  change 11 (11)
                                |*  change 13 (13)""".trimMargin()
        }
    }
}
