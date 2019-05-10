package de.maltsev.gradle.semanticrelease.vcs.github

import de.maltsev.gradle.semanticrelease.vcs.VcsCommit
import de.maltsev.gradle.semanticrelease.vcs.VcsCommitId

data class CommitResponse(
    val data: Data? = null
) : BaseResponse() {
    fun hasMoreCommits(): Boolean {
        return commits.isNotEmpty() &&
            !data?.repository?.ref?.target?.history?.pageInfo?.endCursor.isNullOrBlank() &&
            (data?.repository?.ref?.target?.history?.pageInfo?.hasNextPage ?: false)
    }

    val endCursor: String
        get() = checkNotNull(data?.repository?.ref?.target?.history?.pageInfo?.endCursor) { "No endCursor is found" }

    val commits: List<VcsCommit>
        get() = data?.repository?.ref?.target?.history?.edges?.map { VcsCommit(VcsCommitId(it.node.oid), it.node.message) }
            ?: emptyList()

    data class Data(
        val repository: Repository?
    ) {
        data class Repository(
            val ref: Ref
        )

        data class Ref(
            val target: Target
        ) {
            data class Target(
                val history: History
            ) {
                data class History(
                    val pageInfo: PageInfo,
                    val edges: List<Edge>
                ) {
                    data class PageInfo(
                        val hasNextPage: Boolean,
                        val startCursor: String? = null,
                        val endCursor: String? = null
                    )

                    data class Edge(
                        val node: Node
                    ) {
                        data class Node(
                            val oid: String,
                            val message: String
                        )
                    }
                }
            }
        }
    }
}
