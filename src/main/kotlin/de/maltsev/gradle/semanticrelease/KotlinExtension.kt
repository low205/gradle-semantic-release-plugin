package de.maltsev.gradle.semanticrelease

import de.maltsev.gradle.semanticrelease.extensions.SemanticReleaseExtension
import org.gradle.api.Project

fun Project.semanticRelease(configure: SemanticReleaseExtension.() -> Unit) =
    extensions.configure(SemanticReleaseExtension::class.java, configure)
