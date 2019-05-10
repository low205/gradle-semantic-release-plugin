package de.maltsev.gradle.semanticrelease.vcs

import arrow.core.Option
import de.maltsev.gradle.semanticrelease.versions.SemanticVersion

interface VCSSource {
    fun latestRelease(): Option<VcsRelease>
    fun commitsBefore(release: VcsRelease): List<VcsCommit>
    fun publishRelease(nextVersion: SemanticVersion, releaseNotes: String)
}
