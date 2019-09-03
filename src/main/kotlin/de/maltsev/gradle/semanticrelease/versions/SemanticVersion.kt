package de.maltsev.gradle.semanticrelease.versions

import arrow.core.None
import arrow.core.Option
import arrow.core.Some

sealed class SemanticVersion

data class BranchSemanticVersion(
    private val branchName: String,
    private val commitHash: String
) : SemanticVersion() {
    override fun toString() = "$branchName.$commitHash"
}

data class MasterSemanticVersion(
    val prefix: String,
    val major: Int,
    val minor: Int,
    val patch: Int
) : SemanticVersion() {
    fun nextMajor(): MasterSemanticVersion = copy(
        major = major + 1,
        minor = 0,
        patch = 0
    )

    fun nextMinor(): MasterSemanticVersion = copy(
        minor = minor + 1,
        patch = 0
    )

    fun nextPatch(): MasterSemanticVersion = copy(
        patch = patch + 1
    )

    override fun toString() = "$prefix$major.$minor.$patch"
}

fun MasterSemanticVersion.nextVersion(changes: List<VersionChange>): Option<MasterSemanticVersion> {
    val version = this
    return changes
        .map { it.group }
        .toSet()
        .run {
            when {
                contains(VersionChangeGroup.MAJOR) -> Some(version.nextMajor())
                contains(VersionChangeGroup.MINOR) -> Some(version.nextMinor())
                contains(VersionChangeGroup.PATCH) -> Some(version.nextPatch())
                else -> None
            }
        }
}
