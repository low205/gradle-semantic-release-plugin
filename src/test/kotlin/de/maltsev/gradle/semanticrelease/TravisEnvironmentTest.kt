package de.maltsev.gradle.semanticrelease

import io.kotlintest.shouldBe
import io.kotlintest.specs.ShouldSpec
import org.apache.commons.lang3.RandomStringUtils

class TravisEnvironmentTest : ShouldSpec() {

    init {
        should("read travis values from env") {
            val mock: Environment = MockEnvironment(
                mapOf("TRAVIS_PULL_REQUEST_BRANCH" to RandomStringUtils.randomAlphanumeric(20),
                    "TRAVIS_BRANCH" to RandomStringUtils.randomAlphanumeric(20),
                    "TRAVIS_REPO_SLUG" to RandomStringUtils.randomAlphanumeric(20))
            )
            val env = TravisEnvironment(mock)
            env.branch shouldBe mock.getEnv("TRAVIS_BRANCH")
            env.pullRequestBranch shouldBe mock.getEnv("TRAVIS_PULL_REQUEST_BRANCH")
            env.repoSlug shouldBe mock.getEnv("TRAVIS_REPO_SLUG")
        }
    }
}
