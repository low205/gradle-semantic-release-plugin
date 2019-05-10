package de.maltsev.gradle.semanticrelease.vcs

data class VcsRelease(
    val commitId: VcsCommitId,
    val version: String
)
