package de.maltsev.gradle.semanticrelease.vcs

data class VcsCommitId(
    val id: String,
    val shortId: String
) {
    override fun toString() = id
}
