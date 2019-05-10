package de.maltsev.gradle.semanticrelease.vcs

data class VcsCommit(
    val id: VcsCommitId,
    val message: String
)
