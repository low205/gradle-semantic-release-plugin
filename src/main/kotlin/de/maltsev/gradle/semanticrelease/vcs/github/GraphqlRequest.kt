package de.maltsev.gradle.semanticrelease.vcs.github

internal data class GraphqlRequest(
    val query: String,
    val variables: Map<String, String>
)
