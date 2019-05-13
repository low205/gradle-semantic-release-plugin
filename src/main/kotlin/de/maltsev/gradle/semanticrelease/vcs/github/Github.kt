package de.maltsev.gradle.semanticrelease.vcs.github

import arrow.core.Option
import arrow.core.toOption
import de.maltsev.gradle.semanticrelease.Environment
import de.maltsev.gradle.semanticrelease.SystemEnvironment
import de.maltsev.gradle.semanticrelease.ci.CITool
import de.maltsev.gradle.semanticrelease.vcs.VCSSource
import de.maltsev.gradle.semanticrelease.vcs.VcsCommit
import de.maltsev.gradle.semanticrelease.vcs.VcsCommitId
import de.maltsev.gradle.semanticrelease.vcs.VcsRelease
import de.maltsev.gradle.semanticrelease.versions.SemanticVersion

class Github(private val ciTool: CITool, environment: Environment = SystemEnvironment()) : VCSSource {
    private val token: String = checkNotNull(environment.getEnv("GITHUB_TOKEN")) { "GITHUB_TOKEN not found" }
    private val client: GithubClient = GithubClient(
        token = token,
        repositoryName = ciTool.repositorySlug().split("/")[1],
        repositoryOwner = ciTool.repositorySlug().split("/")[0],
        branch = ciTool.currentBranchName()
    )

    override fun publishRelease(nextVersion: SemanticVersion, releaseNotes: String) {
        val release = GitHubRelease(
            tagName = nextVersion.toVcsString(),
            branch = ciTool.currentBranchName(),
            name = nextVersion.toVcsString(),
            body = releaseNotes
        )

        client.publishRelease(release)
    }

    override fun latestRelease(): Option<VcsRelease> {
        val lastTagResponse = client.getLastTag()

        return lastTagResponse
            .toOption()
            .flatMap { it.data.toOption() }.flatMap { it.repository.toOption() }.map { it.refs }
            .map { it.edges }.flatMap { it.firstOrNull().toOption() }.map { it.node }
            .map { node ->
                VcsRelease(
                    commitId = VcsCommitId(node.target.oid),
                    version = node.name
                )
            }
    }

    override fun commitsBefore(release: VcsRelease): List<VcsCommit> {
        val firstCommitsPage = client.commits()

        val commits = firstCommitsPage.commits.toMutableList()

        if (commits.isEmpty()) {
            return emptyList()
        }

        var prevResponsePage: CommitResponse = firstCommitsPage

        while (commits.indexOfFirst { it.id == release.commitId } == -1 && prevResponsePage.hasMoreCommits()) {
            prevResponsePage = client.commits(prevResponsePage.endCursor)
            commits.addAll(prevResponsePage.commits)
        }

        val indexOfRelease = commits.indexOfFirst { it.id == release.commitId }
        if (indexOfRelease == -1) {
            throw IllegalStateException("From all ${commits.count()} commits commit with oid ${release.commitId} could not be found.")
        }

        return commits.take(indexOfRelease)
    }
}
