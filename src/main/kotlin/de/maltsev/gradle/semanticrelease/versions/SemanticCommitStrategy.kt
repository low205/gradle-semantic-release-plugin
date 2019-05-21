package de.maltsev.gradle.semanticrelease.versions

import arrow.core.None
import arrow.core.Some
import de.maltsev.gradle.semanticrelease.vcs.CommitMessageParser
import de.maltsev.gradle.semanticrelease.vcs.VcsCommit

class SemanticCommitStrategy(private val otherTypes: Map<String, String>) : SemanticStrategy<VcsCommit> {
    override fun execute(source: VcsCommit): VersionChange {
        val descriptor = CommitMessageParser.parse(source.message)

        if (descriptor.breakingChangeMessage.nonEmpty()) {
            return MajorChange(descriptor, source.id)
        }
        return when (descriptor.type) {
            is Some -> {
                when (descriptor.type.t) {
                    MinorChange.TYPE -> MinorChange(descriptor, source.id)
                    PatchChange.TYPE -> PatchChange(descriptor, source.id)
                    else -> VersionChange.of(descriptor, source.id, descriptor.type.t)
                }
            }
            is None -> {
                VersionChange.of(descriptor, source.id, "none")
            }
        }
    }
}
