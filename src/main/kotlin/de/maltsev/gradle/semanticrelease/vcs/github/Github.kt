package de.maltsev.gradle.semanticrelease.vcs.github

import arrow.core.Option
import arrow.core.toOption
import de.maltsev.gradle.semanticrelease.vcs.VCSSource
import de.maltsev.gradle.semanticrelease.vcs.VcsCommit
import de.maltsev.gradle.semanticrelease.vcs.VcsCommitId
import de.maltsev.gradle.semanticrelease.vcs.VcsRelease

class Github(private val client: GithubClient, private val currentBranch: String) : VCSSource {
    override fun latestRelease(): Option<VcsRelease> {
        val lastTagResponse = client.lastTag()

        return lastTagResponse
            .toOption()
            .flatMap { it.data.toOption() }.flatMap { it.repository.toOption() }.map { it.refs }
            .map { it.edges }.flatMap { it.firstOrNull().toOption() }.map { it.node }
            .map { node ->
                VcsRelease(
                    commitId = VcsCommitId(node.target.oid, node.target.abbreviatedOid),
                    version = node.name
                )
            }
    }

    override fun lastCommit(): VcsCommit {
        val lastCommitResponse = client.lastCommit()
        check(lastCommitResponse.commits.isNotEmpty()) { "No commits found on $currentBranch, cannot get last commit" }
        return lastCommitResponse.commits.first()
    }

    override fun commitsBefore(commitId: VcsCommitId): List<VcsCommit> {
        val firstCommitsPage = client.commits()

        val commits = firstCommitsPage.commits.toMutableList()

        if (commits.isEmpty()) {
            return emptyList()
        }

        var prevResponsePage: CommitResponse = firstCommitsPage

        while (commits.indexOfFirst { it.id == commitId } == -1 && prevResponsePage.hasMoreCommits()) {
            prevResponsePage = client.commits(prevResponsePage.endCursor)
            commits.addAll(prevResponsePage.commits)
        }

        val indexOfRelease = commits.indexOfFirst { it.id == commitId }
        check(indexOfRelease != -1) { "From all ${commits.count()} commits commit with oid $commitId could not be found." }

        return commits.take(indexOfRelease)
    }
}
