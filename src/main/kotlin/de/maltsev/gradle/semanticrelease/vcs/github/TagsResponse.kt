package de.maltsev.gradle.semanticrelease.vcs.github

data class TagsResponse(
    val data: Data? = null
) : BaseResponse() {
    data class Data(
        val repository: Repository?
    ) {
        data class Repository(
            val refs: Refs
        )

        data class Refs(
            val edges: List<Edge>
        ) {
            data class Edge(
                val node: Node
            ) {
                data class Node(
                    val name: String,
                    val target: Target
                ) {
                    data class Target(
                        val oid: String
                    )
                }
            }
        }
    }
}
