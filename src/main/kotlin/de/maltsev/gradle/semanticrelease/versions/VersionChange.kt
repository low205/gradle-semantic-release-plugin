package de.maltsev.gradle.semanticrelease.versions

import arrow.core.getOrElse
import arrow.core.toOption
import de.maltsev.gradle.semanticrelease.vcs.CommitMessageDescriptor
import de.maltsev.gradle.semanticrelease.vcs.VcsCommitId

sealed class VersionChange(
    val descriptor: CommitMessageDescriptor,
    val commitId: VcsCommitId
) {
    abstract val name: String
}

class NonSemanticChange(
    descriptor: CommitMessageDescriptor,
    commitId: VcsCommitId,
    private val types: Map<String, String> = emptyMap()
) : VersionChange(descriptor, commitId) {
    override val name: String = descriptor.type.flatMap { types[it].toOption() }.getOrElse { "Others" }
}

abstract class SemanticChange(
    descriptor: CommitMessageDescriptor,
    commitId: VcsCommitId
) : VersionChange(descriptor, commitId)

class MajorChange(descriptor: CommitMessageDescriptor, commitId: VcsCommitId) : SemanticChange(descriptor, commitId) {
    override val name = "Breaking Changes"
}

class MinorChange(descriptor: CommitMessageDescriptor, commitId: VcsCommitId) : SemanticChange(descriptor, commitId) {
    override val name = "Features"

    companion object {
        const val TYPE = "feat"
    }
}

class PatchChange(descriptor: CommitMessageDescriptor, commitId: VcsCommitId) : SemanticChange(descriptor, commitId) {
    override val name = "Bug Fixes"

    companion object {
        const val TYPE = "fix"
    }
}
