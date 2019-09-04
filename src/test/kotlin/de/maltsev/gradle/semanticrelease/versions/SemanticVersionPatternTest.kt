package de.maltsev.gradle.semanticrelease.versions

import io.kotlintest.data.suspend.forall
import io.kotlintest.shouldBe
import io.kotlintest.shouldThrow
import io.kotlintest.specs.ShouldSpec
import io.kotlintest.tables.row

class SemanticVersionPatternTest : ShouldSpec() {

    init {
        should("have first version as 0.1.0") {
            firstVersion shouldBe MasterSemanticVersion("v", 0, 1, 0)
        }

        should("parse semantic versions") {
            "v2.3.41".asSemanticVersion() shouldBe MasterSemanticVersion("v", 2, 3, 41)
        }

        should("parse versions without prefix") {
            "1.2.3".asSemanticVersion() shouldBe MasterSemanticVersion("v", 1, 2, 3)
        }

        should("throw if not semantic version") {
            forall(
                row("a"),
                row("3"),
                row("3.13"),
                row("0.2.2.2"),
                row("0.2.3-rc")
            ) { version ->
                val ex = shouldThrow<IllegalStateException> {
                    version.asSemanticVersion()
                }

                ex.message shouldBe "Version $version doesn't match semantic format v<major>.<minor>.<patch>. Please remove it from repository."
            }
        }
    }
}
