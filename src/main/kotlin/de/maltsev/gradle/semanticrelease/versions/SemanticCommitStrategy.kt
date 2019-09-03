package de.maltsev.gradle.semanticrelease.versions

import arrow.core.Some
import de.maltsev.gradle.semanticrelease.vcs.VcsCommit

fun VcsCommit.asVersionChange(): VersionChange {
    val semanticMessage = this.message.asSemanticCommitMessage()
    return when {
        semanticMessage.breakingChangeMessage.nonEmpty() -> VersionChange(semanticMessage, this.id, VersionChangeGroup.MAJOR)
        semanticMessage.type is Some -> VersionChange(semanticMessage, this.id, VersionChangeGroup.of(semanticMessage.type.t))
        else -> VersionChange(semanticMessage, this.id, VersionChangeGroup.OTHER)
    }
}
