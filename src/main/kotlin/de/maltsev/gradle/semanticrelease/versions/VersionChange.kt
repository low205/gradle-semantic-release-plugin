package de.maltsev.gradle.semanticrelease.versions

import de.maltsev.gradle.semanticrelease.vcs.CommitMessageDescriptor
import de.maltsev.gradle.semanticrelease.vcs.VcsCommitId

sealed class VersionChange(
    val descriptor: CommitMessageDescriptor,
    val commitId: VcsCommitId
) {
    abstract val group: VersionGroup

    companion object {
        fun of(descriptor: CommitMessageDescriptor, commitId: VcsCommitId, changeType: String): VersionChange = NonSemanticChange(
            descriptor, commitId, changes.getOrDefault(changeType, VersionGroup(99, "Others"))
        )

        private val changes = mapOf(
            "perf" to VersionGroup(3, "Performance Improvements"),
            "revert" to VersionGroup(4, "Reverts"),
            "docs" to VersionGroup(5, "Documentation"),
            "style" to VersionGroup(6, "Style"),
            "refactor" to VersionGroup(7, "Code Refactoring"),
            "test" to VersionGroup(8, "Tests"),
            "chore" to VersionGroup(9, "Chores")
        )
    }
}

data class VersionGroup(
    val priority: Int,
    val name: String
) : Comparable<VersionGroup> {
    override fun compareTo(other: VersionGroup): Int {
        return this.priority.compareTo(other.priority)
    }

    override fun toString(): String {
        return name
    }
}

class NonSemanticChange(
    descriptor: CommitMessageDescriptor,
    commitId: VcsCommitId,
    override val group: VersionGroup
) : VersionChange(descriptor, commitId)

abstract class SemanticChange(
    descriptor: CommitMessageDescriptor,
    commitId: VcsCommitId
) : VersionChange(descriptor, commitId)

class MajorChange(descriptor: CommitMessageDescriptor, commitId: VcsCommitId) : SemanticChange(descriptor, commitId) {
    override val group = VersionGroup(0, "Breaking Changes")
}

class MinorChange(descriptor: CommitMessageDescriptor, commitId: VcsCommitId) : SemanticChange(descriptor, commitId) {
    override val group = VersionGroup(1, "Features")

    companion object {
        const val TYPE = "feat"
    }
}

class PatchChange(descriptor: CommitMessageDescriptor, commitId: VcsCommitId) : SemanticChange(descriptor, commitId) {
    override val group = VersionGroup(2, "Bug Fixes")

    companion object {
        const val TYPE = "fix"
    }
}
