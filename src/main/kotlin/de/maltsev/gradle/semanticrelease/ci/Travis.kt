package de.maltsev.gradle.semanticrelease.ci

import de.maltsev.gradle.semanticrelease.Environment
import de.maltsev.gradle.semanticrelease.SystemEnvironment

class Travis(private val environment: Environment = SystemEnvironment()) : CITool {
    override fun currentBranchName(): String {
        val pullRequestBranch = checkNotNull(environment.getEnv("TRAVIS_PULL_REQUEST_BRANCH")) { "No TRAVIS_PULL_REQUEST_BRANCH found" }
        val branch = checkNotNull(environment.getEnv("TRAVIS_BRANCH")) { "No TRAVIS_BRANCH found" }

        return when {
            pullRequestBranch.isNotBlank() -> pullRequestBranch
            branch.isNotBlank() -> branch
            else -> throw IllegalStateException("TRAVIS_PULL_REQUEST_BRANCH and TRAVIS_BRANCH cannot be blank in Travis environment")
        }
    }

    override fun isMaster(): Boolean {
        return currentBranchName() == "master"
    }

    override fun isStage(): Boolean {
        return !isMaster()
    }

    override fun repositorySlug(): String {
        return checkNotNull(environment.getEnv("TRAVIS_REPO_SLUG")) { "No TRAVIS_REPO_SLUG found" }
    }
}
