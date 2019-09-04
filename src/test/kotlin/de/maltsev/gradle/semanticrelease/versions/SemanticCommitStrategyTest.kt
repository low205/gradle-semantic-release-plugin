package de.maltsev.gradle.semanticrelease.versions

import arrow.core.None
import arrow.core.Some
import de.maltsev.gradle.semanticrelease.randomCommitId
import de.maltsev.gradle.semanticrelease.vcs.VcsCommit
import io.kotlintest.data.forall
import io.kotlintest.shouldBe
import io.kotlintest.specs.ShouldSpec
import io.kotlintest.tables.row

class SemanticCommitStrategyTest : ShouldSpec() {
    private val commitId = randomCommitId()

    init {
        should("create major change if have BREAKING CHANGE no matter the group") {
            val versionChange = VcsCommit(
                commitId, "some message BREAKING CHANGE: broken"
            ).asVersionChange()

            versionChange.group shouldBe VersionChangeGroup.MAJOR
            versionChange.commitId shouldBe commitId
            versionChange.descriptor shouldBe SemanticCommitMessage("some message", None, None, Some("broken"))
        }

        should("gather BREAKING CHANGE changes") {
            val versionChange = VcsCommit(
                commitId, """feat: some message 
                    |BREAKING CHANGE: broken1
                    |BREAKING CHANGE: broken2
                    |BREAKING CHANGE: broken3
                    |BREAKING CHANGE: broken4""".trimMargin()
            ).asVersionChange()

            versionChange.group shouldBe VersionChangeGroup.MAJOR
            versionChange.commitId shouldBe commitId
            versionChange.descriptor shouldBe SemanticCommitMessage("some message", Some("feat"), None, Some("broken1\nbroken2\nbroken3\nbroken4"))
        }

        should("find BREAKING CHANGES") {
            val versionChange = VcsCommit(
                commitId, """feat: some message 
                    |BREAKING CHANGES: broken1
                    |BREAKING CHANGE: broken2
                    |BREAKING CHANGES: broken3
                    |BREAKING CHANGE: broken4""".trimMargin()
            ).asVersionChange()

            versionChange.group shouldBe VersionChangeGroup.MAJOR
            versionChange.commitId shouldBe commitId
            versionChange.descriptor shouldBe SemanticCommitMessage("some message", Some("feat"), None, Some("broken1\nbroken2\nbroken3\nbroken4"))
        }

        should("find BREAKING CHANGE in one line ") {
            val versionChange = VcsCommit(
                commitId, """feat: some message BREAKING CHANGES: broken1 BREAKING CHANGE: broken2 BREAKING CHANGES: broken3 BREAKING CHANGE: broken4""".trimMargin()
            ).asVersionChange()

            versionChange.group shouldBe VersionChangeGroup.MAJOR
            versionChange.commitId shouldBe commitId
            versionChange.descriptor shouldBe SemanticCommitMessage("some message", Some("feat"), None, Some("broken1\nbroken2\nbroken3\nbroken4"))
        }

        should("add multiline text from BREAKING CHANGE") {
            val versionChange = VcsCommit(
                commitId, """feat: some message 
                    |BREAKING CHANGE: broken1
                    |broken2
                    |
                    |broken3
                    |
                    |broken4""".trimMargin()
            ).asVersionChange()

            versionChange.group shouldBe VersionChangeGroup.MAJOR
            versionChange.commitId shouldBe commitId
            versionChange.descriptor shouldBe SemanticCommitMessage("some message", Some("feat"), None, Some("broken1\nbroken2\n\nbroken3\n\nbroken4"))
        }

        should("use BREAKING CHANGE as marker") {
            val versionChange = VcsCommit(
                commitId, """feat: some message 
                    |
                    |
                    |BREAKING CHANGE""".trimMargin()
            ).asVersionChange()

            versionChange.group shouldBe VersionChangeGroup.MAJOR
            versionChange.commitId shouldBe commitId
            versionChange.descriptor shouldBe SemanticCommitMessage("some message", Some("feat"), None, Some(""))
        }

        VersionChangeGroup.values().filterNot { it == VersionChangeGroup.MAJOR }.forEach { group ->
            should("create change of type for group $group") {
                val versionChange = VcsCommit(
                    commitId, "${group.type}: some message"
                ).asVersionChange()

                versionChange.group shouldBe group
                versionChange.commitId shouldBe commitId
                versionChange.descriptor shouldBe SemanticCommitMessage("some message", Some(group.type), None, None)
            }
        }

        should("create change in OTHER group all other groups") {
            forall(
                row("some message", None, None),
                row("unknown: some message", Some("unknown"), None),
                row("draft for type 2(val) : some message", Some("draft for type 2"), Some("val"))
            ) { message, type, subType ->
                val versionChange = VcsCommit(
                    commitId, message
                ).asVersionChange()

                versionChange.group shouldBe VersionChangeGroup.OTHER
                versionChange.commitId shouldBe commitId
                versionChange.descriptor shouldBe SemanticCommitMessage("some message", type, subType, None)
            }
        }
    }
}
