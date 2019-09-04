package de.maltsev.gradle.semanticrelease.releasenotes

import de.maltsev.gradle.semanticrelease.vcs.VcsCommit
import de.maltsev.gradle.semanticrelease.vcs.VcsCommitId
import de.maltsev.gradle.semanticrelease.versions.MasterSemanticVersion
import de.maltsev.gradle.semanticrelease.versions.VersionChange
import de.maltsev.gradle.semanticrelease.versions.VersionChangeGroup
import de.maltsev.gradle.semanticrelease.versions.VersionContext
import de.maltsev.gradle.semanticrelease.versions.asSemanticCommitMessage
import de.maltsev.gradle.semanticrelease.versions.asVersionChange
import io.kotlintest.shouldBe
import io.kotlintest.specs.ShouldSpec
import java.time.LocalDate

class MarkdownReleaseNotesGeneratorTest : ShouldSpec() {

    init {
        should("sort changes by priority") {
            val message = VersionContext(
                hasNewVersion = true,
                version = MasterSemanticVersion("v", 1, 1, 1),
                changes = listOf(
                    VersionChange("change 1".asSemanticCommitMessage(), VcsCommitId("1", "1"), VersionChangeGroup.OTHER),
                    VersionChange("change 2".asSemanticCommitMessage(), VcsCommitId("2", "2"), VersionChangeGroup.STYLE),
                    VersionChange("fix: change 3".asSemanticCommitMessage(), VcsCommitId("3", "3"), VersionChangeGroup.PATCH),
                    VersionChange("change 4".asSemanticCommitMessage(), VcsCommitId("4", "4"), VersionChangeGroup.REFACTOR),
                    VersionChange("feat: change 5".asSemanticCommitMessage(), VcsCommitId("5", "5"), VersionChangeGroup.MINOR),
                    VersionChange("change 6".asSemanticCommitMessage(), VcsCommitId("6", "6"), VersionChangeGroup.TEST),
                    VersionChange("feat(subType): change 7\n\nBREAKING CHANGE: broke something".asSemanticCommitMessage(), VcsCommitId("7", "7"), VersionChangeGroup.MAJOR),
                    VersionChange("change 8".asSemanticCommitMessage(), VcsCommitId("8", "8"), VersionChangeGroup.CHORE),
                    VersionChange("change 9".asSemanticCommitMessage(), VcsCommitId("9", "9"), VersionChangeGroup.REVERT),
                    VersionChange("change 10".asSemanticCommitMessage(), VcsCommitId("10", "10"), VersionChangeGroup.DOCS),
                    VersionChange("change 11".asSemanticCommitMessage(), VcsCommitId("11", "11"), VersionChangeGroup.OTHER),
                    VersionChange("change 12".asSemanticCommitMessage(), VcsCommitId("12", "12"), VersionChangeGroup.PERFORMANCE),
                    VersionChange("change 13".asSemanticCommitMessage(), VcsCommitId("13", "13"), VersionChangeGroup.OTHER)
                )
            ).releaseNotes(VersionChangeGroup.values().toSet())

            message shouldBe """|## v1.1.1 (${LocalDate.now()})
                                |
                                |#### Breaking Changes
                                |
                                |* **subType**: change 7 (7)
                                |${'\t'}```BREAKING CHANGE: broke something```
                                |
                                |#### Features
                                |
                                |* change 5 (5)
                                |
                                |#### Bug Fixes
                                |
                                |* change 3 (3)
                                |
                                |#### Performance Improvements
                                |
                                |* change 12 (12)
                                |
                                |#### Reverts
                                |
                                |* change 9 (9)
                                |
                                |#### Documentation
                                |
                                |* change 10 (10)
                                |
                                |#### Style
                                |
                                |* change 2 (2)
                                |
                                |#### Code Refactoring
                                |
                                |* change 4 (4)
                                |
                                |#### Tests
                                |
                                |* change 6 (6)
                                |
                                |#### Chores
                                |
                                |* change 8 (8)
                                |
                                |#### Others
                                |
                                |* change 1 (1)
                                |* change 11 (11)
                                |* change 13 (13)""".trimMargin()
        }

        should("generate expected fix release notes") {
            val releaseNotes = VersionContext(
                true,
                MasterSemanticVersion("v", 1, 1, 1),
                listOf(VcsCommit(
                    VcsCommitId("commitHash", "1"),
                    "fix: add check for null pointer exception"
                ).asVersionChange())
            ).releaseNotes(VersionChangeGroup.values().toSet())

            releaseNotes shouldBe """
                ## v1.1.1 (${LocalDate.now()})

                #### Bug Fixes

                * add check for null pointer exception (commitHash)
            """.trimIndent()
        }

        should("generate expected fix release notes with subType") {
            val releaseNotes = VersionContext(
                true,
                MasterSemanticVersion("v", 1, 1, 1),
                listOf(VcsCommit(
                    VcsCommitId("commitHash", "1"),
                    "fix(payments): add check for null pointer exception"
                ).asVersionChange())
            ).releaseNotes(VersionChangeGroup.values().toSet())

            releaseNotes shouldBe """
                ## v1.1.1 (${LocalDate.now()})

                #### Bug Fixes

                * **payments**: add check for null pointer exception (commitHash)
            """.trimIndent()
        }

        should("generate expected feat release notes") {
            val releaseNotes = VersionContext(
                true,
                MasterSemanticVersion("v", 1, 1, 1),
                listOf(VcsCommit(
                    VcsCommitId("commitHash", "1"),
                    "feat: create new repository"
                ).asVersionChange())
            ).releaseNotes(VersionChangeGroup.values().toSet())

            releaseNotes shouldBe """
                ## v1.1.1 (${LocalDate.now()})

                #### Features

                * create new repository (commitHash)
            """.trimIndent()
        }

        should("generate expected feat release notes with subType") {
            val releaseNotes = VersionContext(
                true,
                MasterSemanticVersion("v", 1, 1, 1),
                listOf(VcsCommit(
                    VcsCommitId("commitHash", "1"),
                    "feat(jpa): create new repository"
                ).asVersionChange())
            ).releaseNotes(VersionChangeGroup.values().toSet())

            releaseNotes shouldBe """
                ## v1.1.1 (${LocalDate.now()})

                #### Features

                * **jpa**: create new repository (commitHash)
            """.trimIndent()
        }

        should("generate expected breaking change release notes") {
            val releaseNotes = VersionContext(
                true,
                MasterSemanticVersion("v", 1, 1, 1),
                listOf(VcsCommit(
                    VcsCommitId("commitHash", "1"),
                    """
                        feat: new version 2 api 
      
                        BREAKING CHANGE: old version 1 api removed
                    """.trimIndent()
                ).asVersionChange())
            ).releaseNotes(VersionChangeGroup.values().toSet())

            releaseNotes shouldBe """
                ## v1.1.1 (${LocalDate.now()})

                #### Breaking Changes

                * new version 2 api (commitHash)
                ${'\t'}```BREAKING CHANGE: old version 1 api removed```
            """.trimIndent()
        }

        should("generate expected breaking change release notes with subType") {
            val releaseNotes = VersionContext(
                true,
                MasterSemanticVersion("v", 1, 1, 1),
                listOf(VcsCommit(
                    VcsCommitId("commitHash", "1"),
                    """
                        feat(api): new version 2 api 
      
                        BREAKING CHANGE: old version 1 api removed
                    """.trimIndent()
                ).asVersionChange())
            ).releaseNotes(VersionChangeGroup.values().toSet())

            releaseNotes shouldBe """
                ## v1.1.1 (${LocalDate.now()})

                #### Breaking Changes

                * **api**: new version 2 api (commitHash)
                ${'\t'}```BREAKING CHANGE: old version 1 api removed```
            """.trimIndent()
        }
    }
}
