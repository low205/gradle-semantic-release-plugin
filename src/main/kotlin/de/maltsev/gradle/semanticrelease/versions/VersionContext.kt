package de.maltsev.gradle.semanticrelease.versions

data class VersionContext(
    val hasNewVersion: Boolean,
    val version: SemanticVersion,
    val changes: List<VersionChange>
)
