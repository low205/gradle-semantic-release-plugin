package de.maltsev.gradle.semanticrelease

import de.maltsev.gradle.semanticrelease.SemanticReleasePlugin.Companion.HAS_NEW_SEMANTIC_VERSION
import de.maltsev.gradle.semanticrelease.SemanticReleasePlugin.Companion.IS_ON_TARGET
import de.maltsev.gradle.semanticrelease.project.hasNewSemanticVersion
import de.maltsev.gradle.semanticrelease.project.isOnTargetBranch
import de.maltsev.gradle.semanticrelease.project.semanticRelease
import io.kotlintest.shouldBe
import io.kotlintest.specs.ShouldSpec
import io.mockk.Runs
import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import io.mockk.verify
import org.gradle.api.Project
import org.gradle.api.plugins.ExtensionContainer

class ExtensionsKtTest : ShouldSpec() {

    init {
        should("provide configure SemanticReleasePluginExtension extension method") {
            val mock: Project = mockk()
            val configure: SemanticReleasePluginExtension.() -> Unit = { }
            val container: ExtensionContainer = mockk()
            every { container.configure(SemanticReleasePluginExtension::class.java, any()) } just Runs
            every { mock.extensions } returns container
            mock.semanticRelease(configure)
            verify { container.configure(SemanticReleasePluginExtension::class.java, any()) }
        }

        should("have no NewSemanticVersion when version is unspecified") {
            val mock: Project = mockk()
            every { mock.version } returns "unspecified"
            mock.hasNewSemanticVersion() shouldBe false
            verify { mock.version }
        }

        should("have no NewSemanticVersion when no $HAS_NEW_SEMANTIC_VERSION property is not set") {
            val mock: Project = mockk()
            every { mock.version } returns "0.1.2"
            every { mock.findProperty(HAS_NEW_SEMANTIC_VERSION) } returns null
            mock.hasNewSemanticVersion() shouldBe false
            verify { mock.findProperty(HAS_NEW_SEMANTIC_VERSION) }
            verify { mock.version }
        }

        should("have no NewSemanticVersion when version and $HAS_NEW_SEMANTIC_VERSION property is set to false") {
            val mock: Project = mockk()
            every { mock.version } returns "0.1.2"
            every { mock.findProperty(HAS_NEW_SEMANTIC_VERSION) } returns false
            mock.hasNewSemanticVersion() shouldBe false
            verify { mock.findProperty(HAS_NEW_SEMANTIC_VERSION) }
            verify { mock.version }
        }

        should("have no NewSemanticVersion when version and $HAS_NEW_SEMANTIC_VERSION property is set to true") {
            val mock: Project = mockk()
            every { mock.version } returns "0.1.2"
            every { mock.findProperty(HAS_NEW_SEMANTIC_VERSION) } returns true
            mock.hasNewSemanticVersion() shouldBe true
            verify { mock.findProperty(HAS_NEW_SEMANTIC_VERSION) }
            verify { mock.version }
        }

        should("have isOnTargetBranch set to false when no $IS_ON_TARGET property is not set") {
            val mock: Project = mockk()
            every { mock.findProperty(IS_ON_TARGET) } returns null
            mock.isOnTargetBranch() shouldBe false
            verify { mock.findProperty(IS_ON_TARGET) }
        }

        should("have isOnTargetBranch set to false when $IS_ON_TARGET property is set to false") {
            val mock: Project = mockk()
            every { mock.findProperty(IS_ON_TARGET) } returns false
            mock.isOnTargetBranch() shouldBe false
            verify { mock.findProperty(IS_ON_TARGET) }
        }

        should("have isOnTargetBranch set to true when $IS_ON_TARGET property is set to true") {
            val mock: Project = mockk()
            every { mock.findProperty(IS_ON_TARGET) } returns true
            mock.isOnTargetBranch() shouldBe true
            verify { mock.findProperty(IS_ON_TARGET) }
        }
    }
}
