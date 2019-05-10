package de.maltsev.gradle.semanticrelease.vcs.github

import com.github.kittinunf.fuel.core.extensions.authentication
import com.github.kittinunf.fuel.core.extensions.jsonBody
import com.github.kittinunf.fuel.core.isSuccessful
import com.github.kittinunf.fuel.httpPost
import com.github.kittinunf.fuel.jackson.defaultMapper
import com.github.kittinunf.fuel.jackson.responseObject

class GithubClient(
    private val token: String,
    repositoryOwner: String,
    repositoryName: String,
    branch: String
) {
    private val releasesUrl: String = "$baseUrl/repos/$repositoryOwner/$repositoryName/releases"

    private val baseCommitParameters = mapOf(
        "owner" to repositoryOwner,
        "repo" to repositoryName,
        "branchName" to branch
    )

    private val lastTagRequest = GraphqlRequest(
        query = tagQuery,
        variables = mapOf(
            "owner" to repositoryOwner,
            "repo" to repositoryName,
            "prefix" to "refs/tags/"
        )
    )

    fun publishRelease(release: GitHubRelease) {
        val (_, response, result) = releasesUrl
            .httpPost()
            .authentication().bearer(token)
            .header(header = "Accept", value = "application/vnd.github.v3+json")
            .jsonBody(defaultMapper.writeValueAsString(release))
            .responseString()

        if (!response.isSuccessful) {
            throw IllegalStateException("Could not create release $release in GitHub. Body: ${result.component1()}", result.component2())
        }
    }

    private fun prepareCommitsRequest(endOfPreviousCursor: String? = null): GraphqlRequest {
        val parameters = when {
            endOfPreviousCursor != null -> baseCommitParameters + ("endCursor" to endOfPreviousCursor)
            else -> baseCommitParameters
        }

        val query = when {
            endOfPreviousCursor != null -> nextCommitsQuery
            else -> commitsQuery
        }

        return GraphqlRequest(
            query = query,
            variables = parameters
        )
    }

    fun getLastTag(): TagsResponse {
        return request(lastTagRequest)
    }

    private inline fun <reified T : BaseResponse> request(request: GraphqlRequest): T {
        val (_, _, result) = graphqlUrl
            .httpPost().authentication().bearer(token)
            .body(defaultMapper.writeValueAsString(request))
            .responseObject<T>()

        val (response, error) = result

        check(response != null || error != null) { "Empty response from github" }

        if (error != null) {
            throw error
        }

        check(response?.errors == null) { "${response?.errors?.map { "${it.type ?: "ERROR"}: ${it.message}" }}" }
        return checkNotNull(response)
    }

    fun commits(endOfPreviousCursor: String? = null): CommitResponse {
        return request(prepareCommitsRequest(endOfPreviousCursor))
    }

    companion object {
        private const val baseUrl: String = "https://api.github.com"
        private const val graphqlUrl: String = "$baseUrl/graphql"
    }
}
