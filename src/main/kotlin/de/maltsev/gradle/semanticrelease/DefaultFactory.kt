package de.maltsev.gradle.semanticrelease

import de.maltsev.gradle.semanticrelease.ci.travis.Travis
import de.maltsev.gradle.semanticrelease.publish.github.GitHubPublisher
import de.maltsev.gradle.semanticrelease.vcs.github.Github
import de.maltsev.gradle.semanticrelease.vcs.github.GithubClient

class DefaultFactory(
    env: Environment,
    extension: SemanticReleasePluginExtension
) {
    val travis: Travis by lazy {
        check(env.hasTravis()) { "Travis is not present" }
        Travis(TravisEnvironment(env), extension.targetBranch.get())
    }

    private val currentBranch: String by lazy {
        travis.currentBranchName()
    }

    private val githubClient: GithubClient by lazy {
        check(env.hasGitHub()) { "GitHub is not present" }
        GithubClient(
            token = env.gitHubToken,
            repositoryName = travis.repositorySlug().split("/")[1],
            repositoryOwner = travis.repositorySlug().split("/")[0],
            branch = currentBranch
        )
    }

    val gitHub: Github by lazy {
        Github(githubClient, currentBranch)
    }

    val gitHubPublisher: GitHubPublisher by lazy {
        GitHubPublisher(githubClient)
    }
}
