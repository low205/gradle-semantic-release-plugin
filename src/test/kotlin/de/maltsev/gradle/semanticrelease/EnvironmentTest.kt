package de.maltsev.gradle.semanticrelease

import io.kotlintest.specs.ShouldSpec
import io.kotlintest.shouldBe
import org.apache.commons.lang3.RandomStringUtils

class EnvironmentTest : ShouldSpec() {

    init {
        should("find travis") {
            val env: Environment = MockEnvironment(
                mapOf("TRAVIS" to "true")
            )
            env.hasTravis() shouldBe true
        }

        should("not find travis") {
            MockEnvironment(
                emptyMap()
            ).hasTravis() shouldBe false
        }

        should("find github") {
            val randomToken = RandomStringUtils.randomAlphanumeric(20)
            val env: Environment = MockEnvironment(
                mapOf("GITHUB_TOKEN" to randomToken)
            )
            env.hasGitHub() shouldBe true
            env.gitHubToken shouldBe randomToken
        }
    }
}
