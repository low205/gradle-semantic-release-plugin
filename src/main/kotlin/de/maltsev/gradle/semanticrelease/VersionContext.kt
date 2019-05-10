package de.maltsev.gradle.semanticrelease

import de.maltsev.gradle.semanticrelease.vcs.VcsCommit
import de.maltsev.gradle.semanticrelease.versions.SemanticVersion
import de.maltsev.gradle.semanticrelease.versions.VersionChange

data class VersionContext(
    val latestVersion: SemanticVersion,
    val changes: List<VersionChange>,
    val commits: List<VcsCommit>
)
