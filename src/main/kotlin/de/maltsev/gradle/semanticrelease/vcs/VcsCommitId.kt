package de.maltsev.gradle.semanticrelease.vcs

data class VcsCommitId(
    val id: String
) {
    override fun toString() = id
}
