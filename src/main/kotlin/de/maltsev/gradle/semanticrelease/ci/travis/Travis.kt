package de.maltsev.gradle.semanticrelease.ci.travis

import de.maltsev.gradle.semanticrelease.TravisEnvironment
import de.maltsev.gradle.semanticrelease.ci.CIApi

class Travis(private val env: TravisEnvironment, private val targetBranch: String) : CIApi {
    override fun currentBranchName(): String {
        return when {
            env.pullRequestBranch.isNotBlank() -> env.pullRequestBranch
            env.branch.isNotBlank() -> env.branch
            else -> throw IllegalStateException("TRAVIS_PULL_REQUEST_BRANCH and TRAVIS_BRANCH cannot be blank in Travis environment")
        }
    }

    override fun repositorySlug(): String {
        return env.repoSlug
    }
}
