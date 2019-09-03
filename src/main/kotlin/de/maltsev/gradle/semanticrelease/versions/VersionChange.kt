package de.maltsev.gradle.semanticrelease.versions

import de.maltsev.gradle.semanticrelease.vcs.VcsCommitId

class VersionChange(
    val descriptor: SemanticCommitMessage,
    val commitId: VcsCommitId,
    val group: VersionChangeGroup
)
