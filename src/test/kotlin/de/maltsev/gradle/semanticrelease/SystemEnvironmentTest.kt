package de.maltsev.gradle.semanticrelease

import io.kotlintest.extensions.system.withEnvironment
import io.kotlintest.shouldBe
import io.kotlintest.specs.ShouldSpec

class SystemEnvironmentTest : ShouldSpec() {

    init {
        should("read from System.env") {
            withEnvironment(mapOf("TestFooKey" to "TestBarValue")) {
                val env = SystemEnvironment()
                env.getEnv("TestFooKey") shouldBe "TestBarValue"
            }
        }
    }
}
