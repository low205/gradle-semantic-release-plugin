package de.maltsev.gradle.semanticrelease.vcs.github

data class GraphqlRequest(
    val query: String,
    val variables: Map<String, String>
)
