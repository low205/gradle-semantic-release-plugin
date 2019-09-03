package de.maltsev.gradle.semanticrelease

import de.maltsev.gradle.semanticrelease.ci.travis.Travis
import de.maltsev.gradle.semanticrelease.publish.github.GitHubPublisher
import de.maltsev.gradle.semanticrelease.vcs.github.Github
import de.maltsev.gradle.semanticrelease.vcs.github.GithubClient

class DefaultFactory(
    env: Environment,
    extension: SemanticReleasePluginExtension
) {
    val travis: Travis? by lazy {
        when {
            env.hasTravis() -> Travis(TravisEnvironment(env), extension.targetBranch.get())
            else -> null
        }
    }

    private val currentBranch: String? by lazy {
        travis?.currentBranchName()
    }

    private val githubClient: GithubClient? by lazy {
        when {
            env.hasGitHub() && travis != null && currentBranch != null -> GithubClient(
                token = env.gitHubToken,
                repositoryName = travis!!.repositorySlug().split("/")[1],
                repositoryOwner = travis!!.repositorySlug().split("/")[0],
                branch = currentBranch!!
            )
            else -> null
        }
    }

    val gitHub: Github? by lazy {
        when {
            githubClient != null && currentBranch != null -> Github(githubClient!!, currentBranch!!)
            else -> null
        }
    }

    val gitHubPublisher: GitHubPublisher? by lazy {
        when {
            githubClient != null -> GitHubPublisher(githubClient!!)
            else -> null
        }
    }
}
