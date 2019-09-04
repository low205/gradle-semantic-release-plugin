package de.maltsev.gradle.semanticrelease

import io.kotlintest.extensions.system.withEnvironment
import io.kotlintest.specs.ShouldSpec
import io.kotlintest.shouldBe

class SystemEnvironmentTest : ShouldSpec() {

    init {
        should("read from System.env") {
            withEnvironment(mapOf(
                "FooKey" to "BarValue",
                "GITHUB_TOKEN" to "token",
                "TRAVIS" to "true"
            )) {
                val env = SystemEnvironment()
                env.getEnv("FooKey") shouldBe "BarValue"
                env.gitHubToken shouldBe "token"
                env.hasGitHub() shouldBe true
                env.hasTravis() shouldBe true
            }
        }
    }
}
