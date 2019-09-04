package de.maltsev.gradle.semanticrelease.project

import de.maltsev.gradle.semanticrelease.SemanticReleasePlugin
import de.maltsev.gradle.semanticrelease.SemanticReleasePluginExtension
import org.gradle.api.Project

fun Project.semanticRelease(configure: SemanticReleasePluginExtension.() -> Unit) =
    extensions.configure(SemanticReleasePluginExtension::class.java, configure)

fun Project.hasNewSemanticVersion() = this.version != "unspecified" && this.findProperty(SemanticReleasePlugin.HAS_NEW_SEMANTIC_VERSION) ?: false == true

fun Project.isOnTargetBranch() = this.findProperty(SemanticReleasePlugin.IS_ON_TARGET) ?: false == true
