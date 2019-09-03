package de.maltsev.gradle.semanticrelease.vcs

import arrow.core.Option

interface VCSSource {
    fun latestRelease(): Option<VcsRelease>
    fun commitsBefore(commitId: VcsCommitId): List<VcsCommit>
    fun lastCommit(): VcsCommit
}
