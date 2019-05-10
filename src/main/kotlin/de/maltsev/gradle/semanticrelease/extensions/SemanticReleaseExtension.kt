package de.maltsev.gradle.semanticrelease.extensions

import org.gradle.api.Project
import org.gradle.api.provider.Property

open class SemanticReleaseExtension(project: Project) {
    val otherChangeTypes: Property<Map<String, String>> = project.lazyWith(DEFAULT_OTHER_CHANGE_TYPES)

    val inferStaged: Property<Boolean> = project.lazyWith(true)

    companion object {
        val DEFAULT_OTHER_CHANGE_TYPES = mapOf(
            "perf" to "Performance Improvements",
            "revert" to "Reverts",
            "docs" to "Documentation",
            "style" to "Style",
            "refactor" to "Code Refactoring",
            "test" to "Tests",
            "chore" to "Chores"
        )
    }
}
