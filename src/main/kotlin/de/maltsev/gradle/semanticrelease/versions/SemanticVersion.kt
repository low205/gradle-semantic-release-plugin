package de.maltsev.gradle.semanticrelease.versions

import arrow.core.None
import arrow.core.Option
import arrow.core.Some

data class SemanticVersion(
    val prefix: String,
    val major: Int,
    val minor: Int,
    val patch: Int,
    val stage: Option<Stage> = None
) {
    private fun nextMajor(): SemanticVersion = this.copy(major = major + 1, minor = 0, patch = 0)
    private fun nextMinor(): SemanticVersion = this.copy(minor = minor + 1, patch = 0)
    private fun nextPatch(): SemanticVersion = this.copy(patch = patch + 1)
    private fun nextStage(count: Int): SemanticVersion = this.copy(stage = stage.map { it.copy(commit = it.commit + count) })

    fun nextVersion(changes: List<VersionChange>): SemanticVersion {
        return when {
            staged() -> nextStage(changes.count())
            else -> {
                val semanticChanges = changes.filter { it is SemanticChange }
                when {
                    semanticChanges.any { it is MajorChange } -> nextMajor()
                    semanticChanges.any { it is MinorChange } -> nextMinor()
                    semanticChanges.any { it is PatchChange } -> nextPatch()
                    else -> this
                }
            }
        }
    }

    data class Stage(
        val name: String,
        val commit: Int
    )

    fun staged() = stage is Some

    override fun toString() = when (stage) {
        is Some -> "$major.$minor.$patch-${stage.t.name}.${stage.t.commit}"
        is None -> "$major.$minor.$patch"
    }

    fun toVcsString() = "$prefix${toString()}"

    fun withStageAt(stage: Stage): SemanticVersion {
        return this.copy(stage = Some(stage))
    }
}
