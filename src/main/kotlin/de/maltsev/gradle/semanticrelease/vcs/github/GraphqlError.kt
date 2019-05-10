package de.maltsev.gradle.semanticrelease.vcs.github

data class GraphqlError(
    val type: String? = null,
    val path: List<String>? = null,
    val locations: List<GraphqlErrorLocation>? = null,
    val message: String? = null
) {
    data class GraphqlErrorLocation(
        val line: Int,
        val column: Int
    )
}
