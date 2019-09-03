package de.maltsev.gradle.semanticrelease

class TravisEnvironment(private val env: Environment) {
    val pullRequestBranch by lazy {
        checkNotNull(env.getEnv("TRAVIS_PULL_REQUEST_BRANCH")) { "No TRAVIS_PULL_REQUEST_BRANCH found" }
    }
    val branch by lazy {
        checkNotNull(env.getEnv("TRAVIS_BRANCH")) { "No TRAVIS_BRANCH found" }
    }
    val repoSlug by lazy {
        checkNotNull(env.getEnv("TRAVIS_REPO_SLUG")) { "No TRAVIS_REPO_SLUG found" }
    }
}
